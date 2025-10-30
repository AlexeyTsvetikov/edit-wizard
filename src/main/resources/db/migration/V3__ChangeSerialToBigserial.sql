-- Меняем все INTEGER/SERIAL на BIGINT/BIGSERIAL для совместимости с Long в Java

-- Основные таблицы - меняем ID
ALTER TABLE users ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE users_id_seq AS BIGINT;

ALTER TABLE file_types ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE file_types_id_seq AS BIGINT;

ALTER TABLE gost_standards ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE gost_standards_id_seq AS BIGINT;

ALTER TABLE formatting_elements ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE formatting_elements_id_seq AS BIGINT;

ALTER TABLE formatting_rules ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE formatting_rules_id_seq AS BIGINT;

ALTER TABLE files ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE files_id_seq AS BIGINT;

ALTER TABLE processing_tasks ALTER COLUMN id SET DATA TYPE BIGINT;
ALTER SEQUENCE processing_tasks_id_seq AS BIGINT;

-- Внешние ключи в files
ALTER TABLE files ALTER COLUMN user_id SET DATA TYPE BIGINT;
ALTER TABLE files ALTER COLUMN file_type_id SET DATA TYPE BIGINT;
ALTER TABLE files ALTER COLUMN gost_standard_id SET DATA TYPE BIGINT;

-- Внешние ключи в formatting_rules
ALTER TABLE formatting_rules ALTER COLUMN file_type_id SET DATA TYPE BIGINT;
ALTER TABLE formatting_rules ALTER COLUMN gost_standard_id SET DATA TYPE BIGINT;
ALTER TABLE formatting_rules ALTER COLUMN element_id SET DATA TYPE BIGINT;

-- Внешние ключи в processing_tasks
ALTER TABLE processing_tasks ALTER COLUMN file_id SET DATA TYPE BIGINT;

-- Проверяем, что все последовательности тоже BIGINT
ALTER SEQUENCE IF EXISTS users_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS file_types_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS gost_standards_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS formatting_elements_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS formatting_rules_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS files_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS processing_tasks_id_seq AS BIGINT;