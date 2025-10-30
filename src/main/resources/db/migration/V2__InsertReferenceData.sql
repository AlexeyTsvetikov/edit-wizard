-- 🎯 ТЕСТОВЫЕ ДАННЫЕ ДЛЯ СПРАВОЧНИКОВ

-- ГОСТы
INSERT INTO gost_standards (code, description) VALUES
('ГОСТ 7.32-2017', 'Отчет о научно-исследовательской работе'),
('ГОСТ 2.105-95', 'Единая система конструкторской документации')
ON CONFLICT (code) DO NOTHING;

-- Типы файлов
INSERT INTO file_types (name, description) VALUES
('METHODOLOGY', 'Методическое пособие'),
('THESIS', 'Диссертация'),
('ARTICLE', 'Научная статья')
ON CONFLICT (name) DO NOTHING;

-- Элементы форматирования
INSERT INTO formatting_elements (name, description) VALUES
('HEADING', 'Заголовок'),
('PARAGRAPH', 'Абзац'),
('LIST', 'Список'),
('TABLE', 'Таблица')
ON CONFLICT (name) DO NOTHING;

-- Пример правил форматирования
INSERT INTO formatting_rules (file_type_id, gost_standard_id, element_id, rules_json) VALUES
(
    (SELECT id FROM file_types WHERE name = 'METHODOLOGY'),
    (SELECT id FROM gost_standards WHERE code = 'ГОСТ 7.32-2017'),
    (SELECT id FROM formatting_elements WHERE name = 'HEADING'),
    '{"fontFamily": "Times New Roman", "fontSize": 16, "fontWeight": "BOLD", "alignment": "CENTER"}'
),
(
    (SELECT id FROM file_types WHERE name = 'METHODOLOGY'),
    (SELECT id FROM gost_standards WHERE code = 'ГОСТ 7.32-2017'),
    (SELECT id FROM formatting_elements WHERE name = 'PARAGRAPH'),
    '{"fontFamily": "Times New Roman", "fontSize": 14, "lineSpacing": 1.5, "alignment": "JUSTIFY"}'
)
ON CONFLICT (file_type_id, gost_standard_id, element_id) DO NOTHING;