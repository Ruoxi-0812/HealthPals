# Authentication storage and request budgets

Password registration, administrator password resets, and verified password changes now store salted BCrypt (cost 10). The existing frontend sends a double-MD5 credential; this release wraps that credential in BCrypt to retain compatibility. It does not remove client-side MD5. The client digest is still password-equivalent: require HTTPS, do not log it, and do not treat it as a public hash.

Existing 32-character lowercase hexadecimal database values are accepted only after successful verification. A conditional UPDATE upgrades them on successful login. Concurrent password changes cannot be overwritten by migration. Users need not reset their passwords. Old database dumps still contain legacy credentials until users sign in; migration cannot undo prior credential exposure.

Profile updates cannot change passwords. Use the verified password-change endpoint or the existing administrator reset route. User-list responses omit stored password values. Blocked users cannot use Google sign-in; Google credentials must have a verified email.

Authentication requests (password/Google login, registration, password changes) share a fixed one-minute budget: 20 per socket peer and 120 process-wide. Rejections return HTTP 429 with Retry-After. Health checks are excluded. Counters are atomic, bounded, reset on process restart, and not shared between replicas. Forwarded headers are not trusted. When hosted behind a shared reverse proxy, users may share a peer budget; validate the deployment's peer-address behavior before tuning. A multi-instance deployment needs a shared store or trusted edge rate limiter.

## Deployment and rollback

- Confirm production user.user_pwd is at least VARCHAR(60); the repository schema uses VARCHAR(100).
- Deploy the backend first; existing clients remain compatible. Updated frontend adds explicit 429 feedback.
- Verify old-account login, subsequent login, registration, and password changes in staging.
- After the first upgrade, do not roll back to a backend that only compares MD5 strings. Keep BCrypt verification in any rollback build. Do not replace upgraded values with old database snapshots.
- This change does not revoke previously issued JWTs when a password changes or a user is blocked. Session revocation is separate follow-up work.

BCrypt implementation: https://central.sonatype.com/artifact/org.mindrot/jbcrypt/0.4
