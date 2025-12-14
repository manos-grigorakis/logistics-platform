INSERT INTO roles (name, description, is_editable, created_at)
VALUES
    ('ADMIN', 'Full system access. Manages users, roles, settings and critical operations.', 0 ,NOW()),
    ('MANAGER', 'Oversees daily operations, assigns jobs and monitors overall progress.', 0 ,NOW()),
    ('EMPLOYEE', 'Handles assigned tasks and updates job or customer information.', 0 ,NOW());

-- Encrypt Password for Initial Setup: https://www.bcryptcalculator.com/
-- INSERT INTO users(role_id, first_name, last_name, email, password, phone, status, enabled, created_at)
-- VALUES
--     (1, '<first-name>', '<last-name>', '<email>', '<password>', NULL, 'ACTIVE',1,  NOW());