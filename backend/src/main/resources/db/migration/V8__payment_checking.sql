-- Invoices
CREATE TABLE `invoices`
(
    id                      BIGINT AUTO_INCREMENT,
    customer_id             BIGINT              NOT NULL,
    external_invoice_number VARCHAR(100) UNIQUE NOT NULL,
    status                  ENUM ('PAID', 'DISPUTED', 'OUTSTANDING', 'PARTIALLY_PAID'),
    total_amount            DECIMAL(10, 2)      NOT NULL,
    remaining_amount        DECIMAL(10, 2)      NOT NULL,
    created_at              DATETIME            NOT NULL,
    updated_at              DATETIME,
    PRIMARY KEY (`id`),
    KEY `idx_invoices_customer` (`customer_id`),
    KEY `uk_invoices_external_invoice_number` (`external_invoice_number`),
    CONSTRAINT `fk_invoice_payments_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_uca1400_ai_ci;

-- Bank Transactions
CREATE TABLE `bank_transactions`
(
    id          BIGINT AUTO_INCREMENT,
    bank_name   VARCHAR(80)    NOT NULL,
    sender_name VARCHAR(80)    NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255)   NULL,
    issue_date  DATE           NOT NULL,
    created_at  DATETIME       NOT NULL,
    updated_at  DATETIME,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_uca1400_ai_ci;

-- Invoice Payments (PIVOT)
CREATE TABLE `invoice_payments`
(
    invoice_id          BIGINT         NOT NULL,
    bank_transaction_id BIGINT         NOT NULL,
    amount              DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (`invoice_id`, `bank_transaction_id`),
    KEY `idx_invoice_payments_invoice` (`invoice_id`),
    KEY `idx_invoice_payments_bank_transaction` (`bank_transaction_id`),
    CONSTRAINT `fk_invoice_payments_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`),
    CONSTRAINT `fk_invoice_payments_bank_transaction` FOREIGN KEY (`bank_transaction_id`) REFERENCES `bank_transactions` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_uca1400_ai_ci;
