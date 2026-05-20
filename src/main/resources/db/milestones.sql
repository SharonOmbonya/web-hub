CREATE TABLE IF NOT EXISTS milestones (
    id BIGSERIAL PRIMARY KEY,

    project_id BIGINT NOT NULL,

    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    due_date DATE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_milestone_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE
);
