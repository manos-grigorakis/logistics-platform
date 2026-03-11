CREATE TABLE `shipments_cargo` (
    id          BIGINT AUTO_INCREMENT NOT NULL,
    description VARCHAR(50) NOT NULL,
    unit        ENUM('PALLET', 'BOX', 'PIECE', 'ROLL', 'BAG') NOT NULL,
    quantity    INTEGER NOT NULL,
    weight_kg   DECIMAL(10, 2) NOT NULL,
    volume_m3   DECIMAL (10, 2) NULL,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NULL DEFAULT NULL,
    shipment_id BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_shipments_cargo_shipment` (`shipment_id`),
    CONSTRAINT `fk_shipments_cargo_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;