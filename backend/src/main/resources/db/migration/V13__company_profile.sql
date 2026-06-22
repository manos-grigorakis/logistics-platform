CREATE TABLE company_profile
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    name                  VARCHAR(100)          NOT NULL,
    tin                   VARCHAR(9)            NOT NULL,
    logo_url              VARCHAR(500)          NULL,
    vat_percentage        INT                   NOT NULL,
    representative        VARCHAR(150)          NOT NULL,
    street                VARCHAR(120)          NOT NULL,
    street_number         VARCHAR(10)           NOT NULL,
    postal_code           VARCHAR(10)           NOT NULL,
    region                VARCHAR(100)          NOT NULL,
    country               VARCHAR(100)          NOT NULL,
    brand_primary_color   VARCHAR(7)            NOT NULL,
    brand_secondary_color VARCHAR(7)            NOT NULL,
    created_at            datetime              NOT NULL,
    updated_at            datetime              NULL,
    CONSTRAINT pk_company_profile PRIMARY KEY (id)
);

ALTER TABLE company_profile
    ADD CONSTRAINT uc_company_profile_name UNIQUE (name);

ALTER TABLE company_profile
    ADD CONSTRAINT uc_company_profile_tin UNIQUE (tin);