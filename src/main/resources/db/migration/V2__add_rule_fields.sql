ALTER TABLE bibliography_rules
    ADD COLUMN target_token VARCHAR(50),
    ADD COLUMN search_pattern VARCHAR(500),
    ADD COLUMN expected_view TEXT;