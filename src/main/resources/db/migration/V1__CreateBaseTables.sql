-- 🗄️ ОСНОВНЫЕ ТАБЛИЦЫ

-- 1. users - пользователи
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW()
);

-- 2. file_types - типы файлов
CREATE TABLE file_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- 3. gost_standards - стандарты ГОСТ
CREATE TABLE gost_standards (
    id SERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- 4. formatting_elements - элементы форматирования
CREATE TABLE formatting_elements (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- 5. formatting_rules - правила форматирования
CREATE TABLE formatting_rules (
    id SERIAL PRIMARY KEY,
    file_type_id INT REFERENCES file_types(id),
    gost_standard_id INT REFERENCES gost_standards(id),
    element_id INT REFERENCES formatting_elements(id),
    rules_json JSONB NOT NULL,
    UNIQUE(file_type_id, gost_standard_id, element_id)
);

-- 6. files - файлы пользователей
CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    file_type_id INT REFERENCES file_types(id),
    gost_standard_id INT REFERENCES gost_standards(id),
    original_filename VARCHAR(500) NOT NULL,
    input_file_key VARCHAR(500),
    output_file_key VARCHAR(500),
    current_status VARCHAR(50) DEFAULT 'UPLOADED'
        CHECK (current_status IN ('UPLOADED', 'PROCESSING', 'COMPLETED', 'FAILED')),
    created_at TIMESTAMP DEFAULT NOW()
);

-- 7. processing_tasks - задачи обработки
CREATE TABLE processing_tasks (
    id SERIAL PRIMARY KEY,
    file_id INT REFERENCES files(id) ON DELETE CASCADE,
    attempt_number INT DEFAULT 1,
    status VARCHAR(50) DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    worker_id VARCHAR(100),
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 📊 ИНДЕКСЫ ДЛЯ ПРОИЗВОДИТЕЛЬНОСТИ
CREATE INDEX idx_files_user_status ON files(user_id, current_status);
CREATE INDEX idx_files_created ON files(created_at);
CREATE INDEX idx_rules_lookup ON formatting_rules(file_type_id, gost_standard_id);
CREATE INDEX idx_tasks_status ON processing_tasks(status);
CREATE INDEX idx_tasks_pending ON processing_tasks(created_at) WHERE status = 'PENDING';
CREATE INDEX idx_tasks_worker ON processing_tasks(worker_id) WHERE status = 'PROCESSING';