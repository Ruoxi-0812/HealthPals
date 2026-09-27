# API access control

## Policy implemented

Authentication is enforced by `JwtInterceptor` before controllers execute. Its
public allowlist matches the HTTP method and exact path, relative to the servlet
context. It no longer exempts paths merely containing `/health` or `/file`.

| Operation | Access |
| --- | --- |
| GET `/health`, GET `/file/getFile` | Public |
| POST `/user/login`, `/user/google-login`, `/user/register` | Public |
| OPTIONS preflight | Public; does not execute the business handler |
| All remaining business routes, including uploads and AI assistance | Valid JWT with a positive user ID and recognized role required |
| User administration and user/system statistics | Admin |
| Article and tag creation, modification, deletion | Admin |
| System notifications and broadcasts | Admin |
| Global health-model creation | Admin |
| Comment management query and bulk deletion | Admin |
| Health-record, bookmark and message queries | Own data for ordinary users; admin may query all |
| Health-record, bookmark and message deletion | Owner or admin |
| Health-record modification | Owner or admin |
| Message read flags | Current recipient only |
| Personal profile lookup | Self or admin |
| Health-model reads | Global models plus caller-owned private models; admin may query all |
| Health-model modification/deletion | Owner of a private model or admin; global models require admin |
| Health-record submission | Caller-owned records; may reference global or owned models (admin can reference any model) |
| Individual comment deletion | Comment author or admin |
| Comment voting | Any authenticated user, toggling only their own vote |

Roles are enum constants in `@Protector`, eliminating the former `Admin`/`admin`
string mismatch. The interceptor enforces role annotations at HTTP entry, while
the existing AOP protector also checks authenticated request context.
Unauthorized authentication returns HTTP 401. Forbidden roles or ownership return
HTTP 403. Invalid batch IDs return HTTP 400. Request identity is cleared before
and after requests, including controller failures and rejected role checks.

## Record ownership

Query scopes are derived from authenticated identity, overriding caller-provided
user IDs. The page rows and count queries share these restrictions. Model-query
visibility is an additional server-controlled predicate, separate from optional
user/global filters.

For restricted updates/deletions, `OwnershipGuard` locks requested rows using
`SELECT ... FOR UPDATE` inside the same Spring transaction as the mutation. It
checks all IDs before any write. A mixed-owner batch or missing row is rejected
in full, so an unauthorized batch does not partially delete valid records.
The table/owner-column mapping is a fixed SQL whitelist, not client-supplied SQL.
Administrators can perform management operations across owners.

Model ownership and global status are included in the existing single batched
configuration lookup during health-record submission, preserving the earlier
query-count improvement. Referencing another user's private model returns 403
and rolls back the submission claim and any writes.

Comment `update` is the existing vote-toggle API, not a content-edit API. Its voter
list is now calculated from the stored list and authenticated identity under a row
lock. A request cannot replace the list with arbitrary user IDs. New comments
cannot seed their own voter list either.

## JWT configuration and rollout

The signing secret is no longer embedded in source. Configure **APP_JWT_SECRET**
with a private random value of at least 32 UTF-8 bytes on every backend instance.
For example, generate a value locally with `openssl rand -hex 32`, then configure
it through your normal secret-management workflow. Do not commit or log it.
The JVM property `app.jwt.secret` is also accepted; the tests use a fresh random
value through that property and restore it afterward. There is no fallback key.
The application fails startup if a valid secret is absent.

JWTs must have an HS256 signature and expiration. Invalid signatures, modified
payloads, unsigned tokens, expired tokens and unknown roles are rejected. This
release changes the signing key and requires users to log in again. All replicas
must use the same configured secret. Existing JWT roles remain a signed snapshot
until expiration (seven days); immediate per-account token revocation or role
refresh is not implemented by this change.

All six Element UI upload widgets now attach the JWT explicitly, because their
requests bypass Axios. Public media reads remain supported for image URLs.
Deploy these frontend changes together with the backend upload restriction.
Docker Compose passes the secret from `.env`; `.env.example` documents the
required variable. For ECS, supply it using a Secrets Manager-backed task secret.
No deployed secrets, database contents, or cloud services were changed.

No new database migration is required for this access-control change. The earlier
idempotency feature still requires its documented `health_submission` migration.

## Verification

Run all backend tests on Java 8:

```sh
mvn -f backend/pom.xml test
```

The suite contains 41 tests: 21 access-control tests, 13 submission reliability/API
tests and 7 query-count tests. The new access tests exercise real HTTP dispatch,
JWT verification, service code, Spring transaction advice and MyBatis SQL against
an isolated database; permissions are not verified using mocked mapper results.
The ordinary-user rejection matrix includes 17 admin-only routes. Positive
controls cover administrator operations and legitimate owner actions.

Run the access-control and reliability suites on disposable local MySQL:

```sh
python3 backend/scripts/test_reliability_mysql.py \
  --mysql-bin /opt/homebrew/opt/mysql/bin \
  --tests HealthSubmissionReliabilityTest,AccessControlTest
```

GitHub Actions is configured to run those two suites against MySQL 8.0 as well as
the default H2 tests. Local MySQL uses the installed server version; the local
validation used MySQL 9.2.0 and Java 8. The GitHub workflow was not run remotely.

These tests verify the documented access policy, not a comprehensive security
assessment of password storage, file-content safety, infrastructure configuration,
or every possible authentication threat.

## Resume wording

After including this implementation in the project, the following statement has
code and test evidence behind it:

```latex
\resumeItem{Engineered 20+ RESTful APIs with Spring Boot for health records, content management, and messaging, enforcing JWT authentication, role-based access control, and record-level ownership checks.}
```
