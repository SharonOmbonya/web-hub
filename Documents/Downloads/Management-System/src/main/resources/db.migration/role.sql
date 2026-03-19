CREATE TABLE IF NOT EXISTS role
(
    role_id       BIGINT NOT NULL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL UNIQUE,  -- e.g., "ADMIN", "HR_MANAGER", "DEPARTMENT_HEAD", "EMPLOYEE"
    description   VARCHAR(255) NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NULL
);
