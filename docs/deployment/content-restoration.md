# Public content restored, 2026-09-27

The free-hosting migration initially created empty tables. Public content was
subsequently restored to Aiven from the repository's `personal_health.sql`
and article/cover correction scripts using `sql/restore-public-content.sql`.

Verified through the live Vercel API: 23 articles, 8 categories and 7 global
health models, including 3 featured articles. The existing new user account
was preserved. No old account passwords, personal health records, favorites,
comments or messages were imported. This is restoration of saved repository
content, not recovery of the suspended AWS database's latest contents.

The restore script requires empty content tables and is not a repeatable
migration. Do not rerun it over populated tables. A pre-restoration dump of
the three content tables was saved locally in `/tmp/healthpals-before-content-restore.sql`.
Global models have no legacy user owner. Their ranges come from the old
project configuration and have not been clinically validated.
