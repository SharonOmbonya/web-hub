CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,
    client VARCHAR(255),
    budget DOUBLE PRECISION,

    start_date DATE,
    end_date DATE,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
