INSERT INTO roles (name, description, is_editable, created_at)
VALUES
    ('ADMIN', 'Full system access. Manages users, roles, settings and critical operations.', FALSE,NOW()),
    ('MANAGER', 'Oversees daily operations, assigns jobs and monitors overall progress.', FALSE,NOW()),
    ('EMPLOYEE', 'Handles assigned tasks and updates job or customer information.', FALSE,NOW());

-- Password for Users is admin
INSERT INTO users(role_id, first_name, last_name, email, password, phone, status, enabled, created_at)
VALUES
    (1, 'Manos', 'Grigorakis', 'manos@logistics.gr', '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', NULL, 'ACTIVE',1,  NOW()),
    (2, 'Maria',  'Papadopoulou',   'maria.papadopoulou@logistics.gr',   '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 694 111 2233', 'ACTIVE', 1, NOW()),
    (3, 'Nikos',  'Karalis',        'nikos.karalis@logistics.gr',        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 698 334 5566', 'ACTIVE', 1, NOW()),
    (3, 'Eleni',  'Mitsou',         'eleni.mitsou@logistics.gr',         '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 697 222 7788', 'ACTIVE', 1, NOW()),
    (3, 'Giorgos','Dimitriou',      'giorgos.dimitriou@logistics.gr',    '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 693 556 9900', 'ACTIVE', 1, NOW()),
    (3, 'Dimitris','Loukas',        'dimitris.loukas@logistics.gr',       '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 695 888 7776', 'ACTIVE', 1, NOW()),
    (3, 'Kostas', 'Stavridis',      'kostas.stavridis@logistics.gr',      '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 694 222 1100', 'ACTIVE',  0, NOW()),
    (3, 'Sofia',  'Papageorgiou',   'sofia.papageorgiou@logistics.gr',   NULL,                          NULL,           'INVITED',  0, NOW()),
    (3, 'Anna',   'Kotsou',         'anna.kotsou@logistics.gr',          '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 694 889 0011', 'SUSPENDED', 0, NOW()),
    (3, 'Petros', 'Iliadis',        'petros.iliadis@logistics.gr',        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 690 123 4567', 'SUSPENDED', 1, NOW());