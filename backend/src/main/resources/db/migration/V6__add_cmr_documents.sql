CREATE TABLE `cmr_documents` (
    id          BIGINT AUTO_INCREMENT NOT NULL,
    number      VARCHAR(14) UNIQUE NOT NULL,
    status      ENUM('GENERATED', 'SIGNED', 'CANCELLED') NOT NULL,
    file_url    VARCHAR(500) NOT NULL,
    signed_at   DATETIME NULL,
    signed_by   VARCHAR(255) NULL,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NULL DEFAULT NULL,
    shipment_id BIGINT UNIQUE NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_cmr_documents_shipment` (`shipment_id`),
    KEY `uk_cmr_documents_number` (number),
    CONSTRAINT `fk_cmr_documents_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;