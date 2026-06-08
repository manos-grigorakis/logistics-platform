ALTER TABLE `cmr_documents`
    DROP COLUMN signed_at,
    DROP COLUMN signed_by,
    ADD COLUMN sender_signed    BOOLEAN DEFAULT FALSE,
    ADD COLUMN carrier_signed   BOOLEAN DEFAULT FALSE,
    ADD COLUMN consignee_signed BOOLEAN DEFAULT FALSE;