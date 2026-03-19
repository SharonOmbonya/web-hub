CREATE TABLE IF NOT EXISTS permission_model
(
    permission_id BIGINT PRIMARY KEY,
    permission    VARCHAR(255) NOT NULL UNIQUE,
    description   VARCHAR(255),
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP
);
