INSERT INTO `roles` (name, description, is_editable, created_at)
VALUES
    ('ADMIN', 'Full system access. Manages users, roles, settings and critical operations.', 0 ,NOW()),
    ('MANAGER', 'Oversees daily operations, assigns jobs and monitors overall progress.', 0 ,NOW()),
    ('EMPLOYEE', 'Handles assigned tasks and updates job or customer information.', 0 ,NOW()),
    ('DRIVER', 'Responsible for transportation and delivery operations.', 0, NOW())
    ON DUPLICATE KEY UPDATE `name` = `name`;