CREATE TABLE IF NOT EXISTS project_assignments (
    id BIGSERIAL PRIMARY KEY,

    project_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,

    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_assignment_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_assignment_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);
