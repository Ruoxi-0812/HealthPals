# Reliable health-record submission

## What changed

`POST /user-health/save` now requires an `Idempotency-Key` header. A single Spring
transaction owns the request claim, health-record batch, abnormal-reading alerts,
and completion marker. The previous per-record configuration lookups remain
replaced by one batched MyBatis lookup.

The client generates a random 128-bit request key. The database's primary key on
`(user_id, request_key)` arbitrates concurrent attempts, including requests handled
by different backend instances. The server hashes only the accepted business
fields using SHA-256; user ID comes from authenticated request context. Reusing a
key with a different payload produces HTTP 409. Replaying a committed request
returns the normal success response without writing records or alerts again.

The claim uses `INSERT ... ON DUPLICATE KEY UPDATE` without overwriting the original
hash. `SELECT ... FOR UPDATE` reads the winner's current committed state, including
under MySQL's REPEATABLE READ isolation. There is no check-then-insert race, JVM
mutex, process-local cache, or duplicate-key exception suppression. A duplicate
waits for the winning transaction. If that transaction fails, its claim rolls back
with its records and alerts, so a waiting retry can become the successful attempt.

All notifications in this workflow are database rows written through the same
DataSource. This does **not** provide transactional delivery for external email,
SMS, or other remote side effects.

## Verified results

Measured locally on **MySQL 9.2.0 (InnoDB)** in a disposable, loopback-only server.
No RDS instance or production data was used. The same backend tests also pass on
H2 2.2.224 in MySQL compatibility mode. Both were verified using Java 8, matching
the repository deployment configuration.

| Experiment | Verified outcome |
| --- | --- |
| 32 simultaneous attempts with one key, each carrying 10 out-of-range records | All 32 return success; exactly 10 health records, 10 alerts, and 1 completed submission exist |
| Attempts split between 2 independently constructed service proxies and MyBatis session factories | Deduplication works through the shared database; no shared application lock/cache |
| Configuration lookups across those 32 attempts | Exactly 1 configuration SELECT |
| Throw after the alert INSERT has executed | 0 records, 0 alerts, 0 submission rows remain; same-key retry succeeds |
| Throw after the health-record INSERT has executed | 0 records, 0 alerts, 0 submission rows remain; same-key retry succeeds |
| Throw after the completion UPDATE has executed | 0 records, 0 alerts, 0 submission rows remain; same-key retry succeeds |
| First attempt fails while a second attempt is claiming its key | Waiting attempt succeeds once; exactly one batch persists |
| Replay after discarding the first response and reconstructing the service | No additional record or alert writes |
| Same key with different readings | HTTP 409, original data unchanged |
| Same key for different authenticated users | Separate legitimate submissions |
| Same readings with a new key | Separate legitimate submission |

These are concurrency/correctness tests, not a throughput benchmark. Two service
instances run within one JVM with separate proxies and session factories; this is
not a test across two ECS tasks or a network partition. Restart behavior is tested
by reconstructing the service while retaining committed database state.

### SQL cost, stated precisely

For a new 10-record submission where all records trigger alerts:

- 1 request-claim upsert
- 1 locking request-state SELECT
- 1 batched configuration SELECT
- 1 batched alert INSERT
- 1 batched record INSERT
- 1 completion UPDATE

That is **6 prepared statements** for the first submission. Each committed replay
uses **2 statements** (claim and state read). The 32-attempt test verifies **68
statements total**: `6 + 31 * 2`, with only one configuration lookup.

The earlier query optimization's 10-to-1 configuration-SELECT metric remains
valid. Its three-statement total measured the core save method without this new
idempotency bookkeeping; it must not be represented as the full HTTP workflow's
current cost. No latency, cloud-cost, or uptime improvement is claimed.

## API contract

```http
POST /user-health/save
Content-Type: application/json
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
token: <existing login token>

[{"healthModelConfigId":1,"value":"21"}]
```

- Keys contain 16–128 ASCII letters, digits, underscores, or hyphens and are case-sensitive.
- A batch contains 1–100 readings, each with a positive metric ID and a finite numeric value of at most 50 characters.
- The server trims values before hashing and writing. Record order and remaining numeric text are part of request identity: `21` and `21.0` are different payloads.
- Client-supplied user IDs and timestamps are ignored. This endpoint saves for the authenticated caller; it does not implement administrator impersonation.
- Same user + same key + same normalized payload returns success after the first commit.
- Same user + same key + different payload returns HTTP 409.
- Missing or malformed keys and invalid input return HTTP 400. Invalid input writes no claim.
- A database failure rolls back the transaction. The caller can retry using the same key and payload.
- A new logical submission must use a new key, even when its readings are identical.

The user and admin creation screens now supply keys. The admin screen also sends
the API's required array body, rather than an object. Save buttons guard against
repeated clicks while a submission is in progress.

The browser stores only payload fingerprints and keys in sessionStorage, scoped
by user and payload. An unconfirmed response keeps its key across reloads in the
same tab. A confirmed success removes it so the next intentional submission gets
a new key. Clearing storage, signing out, closing the tab, or creating a fresh key
ends that client's ability to replay the original request safely. Separate tabs
that independently generate new keys are separate submissions. Web Crypto requires
HTTPS or localhost. Storage failures stop the request rather than losing its key.

## Deployment and retention

1. Apply `sql/migrations/20260922_health_submission.sql` to the application's MySQL database **before** starting the new backend. It creates an InnoDB ledger table and does not change existing records.
   Fresh Docker Compose databases run this migration automatically after the base
   schema. Existing Docker volumes still require the migration to be applied manually.
2. Verify that existing `user_health` and `message` tables use InnoDB (the repository's schema does). Atomic rollback requires transactional tables on the same DataSource.
3. Release the updated frontend and backend together. The header is mandatory; older clients receive HTTP 400. During a coordinated release, refresh old tabs. Do not claim retry protection while a new frontend is still talking to the old backend, which ignores the header.
4. Keep ledger rows for as long as old requests must remain replay-safe. No automatic cleanup is enabled. Deleting a completed row allows that old key to create another submission. Establish a documented retention/retry window before adding cleanup.

The transaction timeout is 15 seconds. Lock/time-out errors can be retried with
the same key. No production migration or deployment was performed as part of the
local implementation and tests.

## Reproduce

Default local backend suite (now 41 tests, including 21 access-control tests):

```sh
mvn -f backend/pom.xml test
```

Actual MySQL reliability suite (13 tests, disposable server; requires installed MySQL binaries):

```sh
python3 backend/scripts/test_reliability_mysql.py --mysql-bin /opt/homebrew/opt/mysql/bin
```

The script uses `--no-defaults`, a temporary data directory, and a loopback port;
it stops and removes that server afterward. Test schemas have random names.
It does not connect to an existing application database. Use the MySQL binary
version that matches your deployment for deployment-specific verification.

Client retry behavior (5 tests):

```sh
node --test frontend/tests/healthSubmission.test.mjs
```

Frontend production build without modifying tracked build artifacts:

```sh
npm --prefix frontend run build -- --dest /tmp/healthpals-frontend-dist
```

The build succeeds with existing Sass deprecation and bundle-size warnings.

GitHub Actions now runs the backend tests before packaging, repeats the reliability
suite against a disposable MySQL 8.0 service, and runs the client retry tests before
the frontend build. The workflow YAML was validated locally; the updated workflow
has not been pushed or executed on GitHub in this session.

## Resume and interview wording

Recommended single bullet:

```latex
\resumeItem{Built a transactional, idempotent health-record submission workflow with Spring Boot and MySQL, preventing duplicate records and alerts across 32 concurrent retry attempts and verifying atomic rollback through fault-injection tests.}
```

If emphasizing the query optimization instead:

```latex
\resumeItem{Implemented retry-safe health-record submissions using MySQL unique constraints and Spring transactions, with batched MyBatis lookups reducing configuration queries from 10 to 1 per 10-record submission.}
```

Describe the engineering problem first: a mobile connection can lose a success
response, and retrying must not duplicate either health records or their alerts.
Explain why disabling a button alone is insufficient; why request keys must be
scoped to users and tied to payloads; why a shared database constraint works across
service instances; and why the request claim and business writes must share one
transaction. Then explain the tests and their local scope. The 32-attempt result
is a tested scenario, not an asserted production capacity limit.
