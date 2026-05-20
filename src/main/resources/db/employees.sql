CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,

    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(150),

    position VARCHAR(100),
    department VARCHAR(100),
    phone VARCHAR(20),
    salary DECIMAL(12,2),

    status VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
