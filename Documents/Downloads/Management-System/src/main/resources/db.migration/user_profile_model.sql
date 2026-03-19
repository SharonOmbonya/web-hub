CREATE TABLE IF NOT EXISTS user_profiles
(
    profile_id    BIGINT NOT NULL PRIMARY KEY,
    user_id       BIGINT NOT NULL,  -- Foreign key to the users table
    role          VARCHAR(255) NOT NULL,  -- Role of the user (Admin, HR, Employee, etc.)
    department    VARCHAR(255) NULL,
    job_title     VARCHAR(255) NULL,
    start_date    TIMESTAMP NULL,
    salary        DECIMAL(10, 2) NULL,
    reporting_manager VARCHAR(255) NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
