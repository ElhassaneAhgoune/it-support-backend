CREATE TABLE refresh_tokens
(
    id         RAW(16) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

