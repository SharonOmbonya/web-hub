CREATE TABLE IF NOT EXISTS users
(
    user_id       BIGINT NOT NULL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    role_id       BIGINT NOT NULL,  -- Foreign key to roles table
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NULL,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
