CREATE TABLE IF NOT EXISTS timesheets (
    id BIGSERIAL PRIMARY KEY,

    project_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,

    work_date DATE NOT NULL,
    hours DOUBLE PRECISION NOT NULL,

    description TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_timesheet_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_timesheet_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);
