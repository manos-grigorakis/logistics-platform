CREATE TABLE supplier_payments
(
    id            BIGINT AUTO_INCREMENT                                                      NOT NULL,
    number        VARCHAR(14)                                                                NOT NULL,
    title         VARCHAR(50)                                                                NOT NULL,
    `description` TEXT                                                                       NULL,
    total_amount  DECIMAL(19, 2)                                                             NOT NULL,
    paid_amount   DECIMAL(19, 2)                                         DEFAULT 0.00        NOT NULL,
    status        ENUM ('PENDING', 'PAID', 'PARTIALLY_PAID', 'CANCELED') DEFAULT ('PENDING') NOT NULL,
    type          ENUM ('FUEL', 'INSURANCE', 'SERVICE', 'OTHER')                             NOT NULL,
    supplier_id   BIGINT                                                                     NOT NULL,
    created_at    datetime                                                                   NOT NULL,
    updated_at    datetime                                                                   NULL,
    CONSTRAINT pk_supplier_payments PRIMARY KEY (id)
);

CREATE TABLE suppliers
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    company_name VARCHAR(100)          NOT NULL,
    email        VARCHAR(320)          NULL,
    created_at   datetime              NOT NULL,
    updated_at   datetime              NULL,
    is_deleted   boolean DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_suppliers PRIMARY KEY (id)
);

CREATE INDEX idx_supplier_payments_supplier_id
    ON supplier_payments (supplier_id);

ALTER TABLE supplier_payments
    ADD CONSTRAINT uc_supplier_payments_number UNIQUE (number);

ALTER TABLE suppliers
    ADD CONSTRAINT uc_suppliers_company_name UNIQUE (company_name);

ALTER TABLE supplier_payments
    ADD CONSTRAINT fk_supplier_payments_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id);