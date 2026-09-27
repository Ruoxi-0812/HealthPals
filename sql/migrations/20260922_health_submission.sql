-- Apply before deploying the backend that requires Idempotency-Key.
-- Retain rows for as long as old requests must remain safe to replay.
CREATE TABLE IF NOT EXISTS health_submission (
    user_id INT NOT NULL,
    request_key VARCHAR(128) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    payload_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, request_key)
) ENGINE=InnoDB;
