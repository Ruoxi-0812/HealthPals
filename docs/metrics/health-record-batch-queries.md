# Health-record batch save: measured SQL reduction

## Problem and implementation

`POST /user-health/save` accepts multiple health records. Previously,
`UserHealthServiceImpl.dealMessage` called the configuration mapper inside the
record loop. Each lookup selected all configuration fields and joined the user
table, even though notification generation only needs the configuration ID,
name, unit, and expected range. The later access-control change also reads owner ID
and global status in the same lookup to validate which models a user may reference.

The service now deduplicates non-null configuration IDs, fetches the notification fields plus ownership metadata in one parameterized `IN` query, and uses an ID-to-configuration map while
processing the records. Notification and health-record writes remain batched.
This replaces N configuration SELECTs with one SELECT per nonempty batch of IDs;
it does not cache configuration data across requests.

Empty record batches now return without executing SQL. Missing configurations
continue to generate no alert. A null configuration ID no longer accidentally
selects an unrelated configuration; it generates no alert. An empty or null ID
list passed directly to the new mapper selects no rows.

## Scope after the reliability update

The HTTP endpoint now wraps the core save method in a transactional, idempotent
submission workflow. The counts below still describe the core save method only;
the full new-request workflow adds three ledger statements. See
[submission reliability](health-submission-reliability.md) for the full cost,
concurrency results, API contract, and required database migration.

## Results

Measured locally using the actual service, MyBatis XML mappers, and H2 2.2.224
in MySQL compatibility mode. A MyBatis interceptor counts prepared SQL statements;
these are database executions, not mocked mapper invocation counts. Fixture setup
and verification queries are excluded. `SqlSessionTemplate` mirrors the existing
non-transactional service's per-call session lifecycle.

Each batch below contains distinct configuration IDs and out-of-range values,
so it produces one batched notification INSERT and one batched record INSERT.
The fixtures seed 100 configurations; they are synthetic, not production users
or production traffic.

| Records per save | Configuration SELECTs before | After | Total SQL before | After |
| --- | ---: | ---: | ---: | ---: |
| 1 | 1 | 1 | 3 | 3 |
| 10 | 10 | 1 | 12 | 3 |
| 100 | 100 | 1 | 102 | 3 |

For 10 records, configuration SELECTs decrease by **90%**, and total statements
in this all-alert scenario decrease by **75%**. For 100 records, configuration
SELECTs decrease by **99%**. These percentages describe query counts, not latency,
throughput, AWS cost, or production performance. Batches with no alerts omit the
notification INSERT. There is no query-count benefit for a single-record batch.

## Reproduce

From the repository root:

```sh
python3 backend/scripts/measure_batch_queries.py
```

The script copies the test project into a temporary directory and restores the
three original production files from baseline commit
`57a0d56405d5c34e96f4c07d650ac676611fc112`. It runs the three applicable baseline
tests, then the current seven tests, and prints CSV results. It does not change
the working checkout's production files. Maven may download test dependencies.
No application server is started and no RDS credentials are needed.

To run the current backend tests only:

```sh
mvn -f backend/pom.xml test
```

Seven passing integration tests cover batch sizes 1/10/100, duplicate IDs and
inclusive range boundaries, missing configurations, empty batches, null record
configuration IDs, null/empty mapper ID lists, and selective batch lookup.
They also verify persisted row counts, authenticated-user assignment, and
notification contents for readings below and above the configured range.

## Resume wording

```latex
\resumeItem{Eliminated per-record configuration queries in health-record batch saves using MyBatis batch fetching, reducing configuration SELECTs by 90\% (10 to 1) for 10-record batches, verified through database integration tests.}
```

Interview explanation: identified a lookup inside the save loop, measured SQL
executions before changing it, replaced the loop's database calls with a batched
lookup and in-memory map, and verified that record persistence and abnormal-value
notifications still behave correctly. This is local integration-test evidence;
MySQL/RDS deployment validation and performance load testing are separate work.
