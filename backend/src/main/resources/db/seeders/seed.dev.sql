SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- Roles
-- =============================================================================
INSERT INTO roles (name, description, is_editable, created_at)
VALUES ('ADMIN', 'Full system access. Manages users, roles, settings and critical operations.', FALSE, NOW()),
       ('MANAGER', 'Oversees daily operations, assigns jobs and monitors overall progress.', FALSE, NOW()),
       ('EMPLOYEE', 'Handles assigned tasks and updates job or customer information.', FALSE, NOW()),
       ('DRIVER', 'Responsible for transportation and delivery operations.', FALSE, NOW());

-- =============================================================================
-- Users (password: admin)
-- =============================================================================
INSERT INTO users(role_id, first_name, last_name, email, password, phone, status, enabled, created_at)
VALUES (1, 'Manos', 'Grigorakis', 'manos@logistics.gr', '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu',
        NULL, 'ACTIVE', 1, NOW()),
       (2, 'Maria', 'Papadopoulou', 'maria.papadopoulou@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 694 111 2233', 'ACTIVE', 1, NOW()),
       (3, 'Nikos', 'Karalis', 'nikos.karalis@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 698 334 5566', 'ACTIVE', 1, NOW()),
       (3, 'Eleni', 'Mitsou', 'eleni.mitsou@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 697 222 7788', 'ACTIVE', 1, NOW()),
       (3, 'Giorgos', 'Dimitriou', 'giorgos.dimitriou@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 693 556 9900', 'ACTIVE', 1, NOW()),
       (4, 'Dimitris', 'Loukas', 'dimitris.loukas@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 695 888 7776', 'ACTIVE', 1, NOW()),
       (4, 'Kostas', 'Stavridis', 'kostas.stavridis@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 694 222 1100', 'ACTIVE', 0, NOW()),
       (4, 'Sofia', 'Papageorgiou', 'sofia.papageorgiou@logistics.gr', NULL, NULL, 'INVITED', 0, NOW()),
       (4, 'Anna', 'Kotsou', 'anna.kotsou@logistics.gr', '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu',
        '+30 694 889 0011', 'SUSPENDED', 0, NOW()),
       (4, 'Petros', 'Iliadis', 'petros.iliadis@logistics.gr',
        '$2a$10$TSnN.FwDiW/EEXk4ZZQm.On7lnZFCbDSMoHvHj3A1DZhkq3X7eswu', '+30 690 123 4567', 'SUSPENDED', 1, NOW());

-- =============================================================================
-- Customers
-- =============================================================================
INSERT INTO customers (company_name, tin, first_name, last_name, email, customer_type, phone, location, created_at)
VALUES ('Αλφα Μεταφορές Α.Ε.', '094123456', 'Παναγιώτης', 'Αλεξίου', 'info@alfa-metafores.gr', 'COMPANY',
        '+30 210 123 4567', 'Αθήνα, Αττική', NOW()),
       ('Βήτα Logistics Ε.Π.Ε.', '094234567', 'Σταύρος', 'Βλάχος', 'contact@vita-logistics.gr', 'COMPANY',
        '+30 2310 987 654', 'Θεσσαλονίκη, Κ. Μακεδονία', NOW()),
       ('Γάμμα Trade Solutions', '094345678', 'Ειρήνη', 'Γεωργίου', 'eirini@gamma-trade.gr', 'COMPANY',
        '+30 2610 456 789', 'Πάτρα, Αχαΐα', NOW()),
       ('Δέλτα Import Export Α.Ε.', '094456789', 'Θανάσης', 'Δημητρίου', 'info@delta-ie.gr', 'COMPANY',
        '+30 2410 333 111', 'Λάρισα, Θεσσαλία', NOW()),
       ('Έψιλον Φορτηγά Μ.Ε.Π.Ε.', '094567890', 'Κώστας', 'Εμμανουήλ', 'kostas@epsilon-trucks.gr', 'COMPANY',
        '+30 2810 222 333', 'Ηράκλειο, Κρήτη', NOW()),
       ('Ζήτα Distributions', '094678901', 'Μαρία', 'Ζαχαρίου', 'maria@zita-dist.gr', 'COMPANY', '+30 210 876 5432',
        'Πειραιάς, Αττική', NOW()),
       ('Ήτα Cargo Α.Ε.', '094789012', 'Δημήτρης', 'Ηλιόπουλος', 'info@ita-cargo.gr', 'COMPANY', '+30 2310 111 222',
        'Θεσσαλονίκη, Κ. Μακεδονία', NOW()),
       ('Θήτα Fresh Foods', '094890123', 'Σοφία', 'Θεοδώρου', 'sofia@thita-fresh.gr', 'COMPANY', '+30 2610 789 456',
        'Πάτρα, Αχαΐα', NOW()),
       ('Ιώτα Χτιστή', '094901234', 'Ιωάννης', 'Ιωάννου', NULL, 'INDIVIDUAL', '+30 694 901 2345', 'Τρίκαλα, Θεσσαλία',
        NOW()),
       ('Κάππα Engineering', '095012345', 'Νίκος', 'Καρπούζης', 'nikos@kappa-eng.gr', 'COMPANY', '+30 210 543 2100',
        'Αθήνα, Αττική', NOW());

-- =============================================================================
-- Quotes
-- =============================================================================
INSERT INTO quotes (customer_id, user_id, number, issue_date, origin, destination,
                    tax_rate_percentage, net_price, vat_amount, gross_price,
                    notes, quote_status, validity_days, expiration_date, created_at)
VALUES
    -- 1: CONVERTED → will become Shipment 1 (DELIVERED)
    (1, 1, 'Q-2025-0001', '2025-11-10', 'Αθήνα', 'Θεσσαλονίκη',
     24, 1200.00, 288.00, 1488.00,
     'Urgent delivery', 'CONVERTED', 14, '2025-11-24', '2025-11-10 09:00:00'),

    -- 2: CONVERTED → will become Shipment 2 (DELIVERED)
    (2, 1, 'Q-2025-0002', '2025-11-20', 'Θεσσαλονίκη', 'Πάτρα',
     24, 950.00, 228.00, 1178.00,
     NULL, 'CONVERTED', 14, '2025-12-04', '2025-11-20 10:30:00'),

    -- 3: CONVERTED → will become Shipment 3 (DISPATCHED)
    (3, 2, 'Q-2025-0003', '2025-12-01', 'Πάτρα', 'Ηράκλειο',
     24, 1800.00, 432.00, 2232.00,
     'Fragile items', 'CONVERTED', 14, '2025-12-15', '2025-12-01 08:00:00'),

    -- 4: CONVERTED → will become Shipment 4 (PENDING)
    (4, 2, 'Q-2026-0001', '2026-01-05', 'Λάρισα', 'Αθήνα',
     24, 600.00, 144.00, 744.00,
     NULL, 'CONVERTED', 30, '2026-02-04', '2026-01-05 11:00:00'),

    -- 5: ACCEPTED (no shipment yet)
    (5, 1, 'Q-2026-0002', '2026-02-10', 'Αθήνα', 'Λάρισα',
     24, 750.00, 180.00, 930.00,
     'Customer confirmed verbally', 'ACCEPTED', 14, '2026-02-24', '2026-02-10 09:15:00'),

    -- 6: SENT
    (6, 3, 'Q-2026-0003', '2026-03-01', 'Πειραιάς', 'Θεσσαλονίκη',
     24, 1100.00, 264.00, 1364.00,
     NULL, 'SENT', 14, '2026-03-15', '2026-03-01 14:00:00'),

    -- 7: SENT
    (7, 3, 'Q-2026-0004', '2026-03-15', 'Θεσσαλονίκη', 'Σόφια, Βουλγαρία',
     24, 2200.00, 528.00, 2728.00,
     'International shipment', 'SENT', 21, '2026-04-05', '2026-03-15 10:00:00'),

    -- 8: DRAFT
    (8, 2, 'Q-2026-0005', '2026-04-01', 'Πάτρα', 'Αθήνα',
     24, 500.00, 120.00, 620.00,
     'Waiting for final item list', 'DRAFT', 14, '2026-04-15', '2026-04-01 16:00:00'),

    -- 9: REJECTED
    (9, 1, 'Q-2025-0004', '2025-12-10', 'Αθήνα', 'Ιωάννινα',
     24, 400.00, 96.00, 496.00,
     NULL, 'REJECTED', 14, '2025-12-24', '2025-12-10 09:00:00'),

    -- 10: CANCELLED
    (10, 3, 'Q-2025-0005', '2025-12-20', 'Αθήνα', 'Τρίκαλα',
     24, 350.00, 84.00, 434.00,
     'Customer cancelled', 'CANCELLED', 14, '2026-01-03', '2025-12-20 11:00:00'),

    -- 11: EXPIRED
    (1, 2, 'Q-2025-0006', '2025-11-01', 'Αθήνα', 'Πάτρα',
     24, 800.00, 192.00, 992.00,
     NULL, 'EXPIRED', 14, '2025-11-15', '2025-11-01 08:00:00');

-- =============================================================================
-- Quote Items
-- =============================================================================
INSERT INTO quote_items (quote_id, name, description, quantity, unit, price, created_at)
VALUES (1, 'Road Transport', 'Athens to Thessaloniki full load', 1, 'PALLET', 800.00, NOW()),
       (1, 'Loading Fee', NULL, 4, 'HOUR', 100.00, NOW()),
       (2, 'Partial Load', 'Thessaloniki to Patras', 1, 'PALLET', 700.00, NOW()),
       (2, 'Fuel Surcharge', NULL, 1, 'PIECE', 250.00, NOW()),
       (3, 'Ferry + Road', 'Patras to Heraklion via ferry', 1, 'PALLET', 1200.00, NOW()),
       (3, 'Packing Materials', 'Fragile item packing', 10, 'PIECE', 60.00, NOW()),
       (4, 'Road Transport', 'Larisa to Athens', 1, 'PALLET', 600.00, NOW()),
       (5, 'Road Transport', 'Athens to Larisa', 1, 'PALLET', 600.00, NOW()),
       (5, 'Express Handling', NULL, 3, 'HOUR', 50.00, NOW()),
       (6, 'Full Truck Load', 'Piraeus to Thessaloniki', 1, 'PALLET', 900.00, NOW()),
       (6, 'Port Handling Fee', NULL, 1, 'PIECE', 200.00, NOW()),
       (7, 'International Road', 'Thessaloniki to Sofia', 1, 'PALLET', 1800.00, NOW()),
       (7, 'Customs Documentation', NULL, 1, 'PIECE', 400.00, NOW()),
       (8, 'Road Transport', 'Patras to Athens', 1, 'PALLET', 500.00, NOW()),
       (9, 'Road Transport', 'Athens to Ioannina', 1, 'PALLET', 400.00, NOW()),
       (10, 'Road Transport', 'Athens to Trikala', 1, 'PALLET', 350.00, NOW()),
       (11, 'Road Transport', 'Athens to Patras', 1, 'PALLET', 800.00, NOW());

-- =============================================================================
-- Vehicles
-- =============================================================================
INSERT INTO vehicles (brand, plate, type, created_at)
VALUES ('Mercedes-Benz Actros', 'ΑΑΑ-1234', 'TRUCK', NOW()),
       ('Volvo FH16', 'ΒΒΒ-5678', 'TRUCK', NOW()),
       ('MAN TGX', 'ΓΓΓ-9012', 'TRUCK', NOW()),
       ('Schmitz Cargobull', 'ΔΔΔ-3456', 'TRAILER', NOW()),
       ('Krone Profi Liner', 'ΕΕΕ-7890', 'TRAILER', NOW()),
       ('Kögel Cargo', 'ΖΖΖ-2345', 'TRAILER', NOW());

-- =============================================================================
-- Shipments
-- =============================================================================
INSERT INTO shipments (quote_id, driver_id, created_by_user_id, truck_id, trailer_id,
                       number, status, pickup, notes, created_at)
VALUES
    -- Shipment 1: DELIVERED — fully assigned
    (1, 6, 1, 1, 4, 'TO-2025-00001', 'DELIVERED',
     '2025-11-12 07:00:00', 'Delivered on time', '2025-11-11 17:00:00'),

    -- Shipment 2: DELIVERED — fully assigned
    (2, 6, 1, 2, 5, 'TO-2025-00002', 'DELIVERED',
     '2025-11-22 06:30:00', NULL, '2025-11-21 16:00:00'),

    -- Shipment 3: DISPATCHED — fully assigned, in transit
    (3, 6, 2, 3, 6, 'TO-2025-00003', 'DISPATCHED',
     '2026-03-10 08:00:00', 'Ferry boarding at 06:00', '2025-12-02 09:00:00'),

    -- Shipment 4: PENDING — no driver/truck/trailer yet
    (4, NULL, 2, NULL, NULL, 'TO-2026-00001', 'PENDING',
     '2026-06-15 08:00:00', NULL, '2026-01-06 10:00:00');

-- =============================================================================
-- Shipments Cargo
-- =============================================================================
INSERT INTO shipments_cargo (description, unit, quantity, weight_kg, volume_m3, shipment_id, created_at)
VALUES ('Βιομηχανικά εξαρτήματα', 'PALLET', 10, 4500.00, 18.00, 1, NOW()),
       ('Συσκευασμένα εμπορεύματα', 'BOX', 20, 800.00, 4.00, 1, NOW()),
       ('Τρόφιμα ξηρά', 'PALLET', 6, 2400.00, 12.00, 2, NOW()),
       ('Ηλεκτρονικός εξοπλισμός', 'PALLET', 4, 1200.00, 8.00, 3, NOW()),
       ('Ανταλλακτικά αυτοκινήτων', 'BOX', 15, 600.00, 3.00, 3, NOW()),
       ('Δομικά υλικά', 'PALLET', 20, 8000.00, 40.00, 4, NOW());

-- =============================================================================
-- CMR Documents (fake MinIO URL)
-- =============================================================================
INSERT INTO cmr_documents (number, status, file_url, signed_at, signed_by, shipment_id, created_at)
VALUES
    -- CMR 1: SIGNED (Shipment 1 — DELIVERED)
    ('CMR-2025-00001', 'SIGNED',
     'cmr-documents/CMR-2025-00001.pdf',
     '2025-11-13 14:30:00', 'Παναγιώτης Αλεξίου',
     1, '2025-11-12 09:00:00'),

    -- CMR 2: SIGNED (Shipment 2 — DELIVERED)
    ('CMR-2025-00002', 'SIGNED',
     'cmr-documents/CMR-2025-00002.pdf',
     '2025-11-23 16:00:00', 'Σταύρος Βλάχος',
     2, '2025-11-22 08:00:00'),

    -- CMR 3: GENERATED (Shipment 3 — DISPATCHED, not yet signed)
    ('CMR-2025-00003', 'GENERATED',
     'cmr-documents/CMR-2025-00003.pdf',
     NULL, NULL,
     3, '2025-12-02 10:00:00');

-- =============================================================================
-- Invoices
-- =============================================================================
INSERT INTO invoices (customer_id, external_invoice_number, status,
                      total_amount, remaining_amount, invoice_date, created_at)
VALUES (1, 'INV-2025-001', 'PAID', 1488.00, 0.00, '2025-11-15', NOW()),
       (1, 'INV-2025-002', 'OUTSTANDING', 992.00, 992.00, '2025-11-20', NOW()),
       (2, 'INV-2025-003', 'PAID', 1178.00, 0.00, '2025-11-25', NOW()),
       (2, 'INV-2025-004', 'PARTIALLY_PAID', 800.00, 300.00, '2025-12-05', NOW()),
       (3, 'INV-2025-005', 'OUTSTANDING', 2232.00, 2232.00, '2025-12-10', NOW()),
       (4, 'INV-2026-001', 'OUTSTANDING', 744.00, 744.00, '2026-01-10', NOW()),
       (5, 'INV-2026-002', 'DISPUTED', 930.00, 930.00, '2026-02-15', NOW()),
       (6, 'INV-2026-003', 'OUTSTANDING', 1364.00, 1364.00, '2026-03-05', NOW());

-- =============================================================================
-- Bank Transactions
-- =============================================================================
INSERT INTO bank_transactions (bank_name, sender_name, amount, description, issue_date, created_at)
VALUES ('Eurobank', 'ΑΛΦΑ ΜΕΤΑΦΟΡΕΣ ΑΕ', 1488.00, 'INV-2025-001 payment', '2025-11-18', NOW()),
       ('Alpha Bank', 'ΒΗΤΑ LOGISTICS ΕΠΕ', 1178.00, 'INV-2025-003 full settlement', '2025-11-28', NOW()),
       ('Piraeus', 'ΒΗΤΑ LOGISTICS ΕΠΕ', 500.00, 'partial INV-2025-004', '2025-12-08', NOW()),
       ('NBG', 'ΓΑΜΜΑ TRADE SOLUTIONS', 700.00, 'Unmatched payment', '2025-12-12', NOW());

-- =============================================================================
-- INVOICE PAYMENTS
-- =============================================================================
INSERT INTO invoice_payments (invoice_id, bank_transaction_id, amount)
VALUES (1, 1, 1488.00), -- INV-2025-001 fully paid by transaction 1
       (3, 2, 1178.00), -- INV-2025-003 fully paid by transaction 2
       (4, 3, 500.00);
-- INV-2025-004 partially paid by transaction 3
-- Transaction 4 (700.00 from Gamma Trade) has no match → OUTSTANDING

SET FOREIGN_KEY_CHECKS = 1;
