CREATE TABLE `reconciliation_reports`
(
    id                 BIGINT AUTO_INCREMENT,
    name               VARCHAR(255) UNIQUE NOT NULL,
    customer_id        BIGINT              NOT NULL,
    file_url           VARCHAR(500),
    from_date          DATE                NOT NULL,
    to_date            DATE                NOT NULL,
    matched_invoices   INTEGER             NOT NULL,
    unmatched_invoices INTEGER             NOT NULL,
    created_at         DATETIME            NOT NULL,
    PRIMARY KEY (`id`),
    KEY                `idx_reconciliation_report_customer` (`customer_id`),
    KEY                `uk_reconciliation_report_name` (`name`),
    CONSTRAINT `fk_reconciliation_report_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
)ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_uca1400_ai_ci;