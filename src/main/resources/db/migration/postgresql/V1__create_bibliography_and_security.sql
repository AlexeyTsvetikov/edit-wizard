
CREATE TABLE IF NOT EXISTS security_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS bibliography_rules (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    source_types VARCHAR(200),
    example_text TEXT NOT NULL,
    description TEXT,
    target_token VARCHAR(50),
    search_pattern VARCHAR(500),
    expected_view TEXT
);

CREATE TABLE IF NOT EXISTS bibliography_validation_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES security_users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_lines INT DEFAULT 0 NOT NULL,
    error_count INT DEFAULT 0 NOT NULL,
    unknown_count INT DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS bibliography_validation_lines (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES bibliography_validation_sessions(id) ON DELETE CASCADE,
    line_number INT NOT NULL,
    raw_text TEXT NOT NULL,
    source_type VARCHAR(50),
    type_detected BOOLEAN NOT NULL,
    is_valid BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS bibliography_line_errors (
    id BIGSERIAL PRIMARY KEY,
    line_id BIGINT NOT NULL REFERENCES bibliography_validation_lines(id) ON DELETE CASCADE,
    rule_code VARCHAR(50) REFERENCES bibliography_rules(code),
    error_index INT NOT NULL,
    error_length INT NOT NULL,
    error_message VARCHAR(255) NOT NULL,
    expected_view TEXT
);