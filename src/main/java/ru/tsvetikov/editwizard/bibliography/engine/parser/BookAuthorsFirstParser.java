package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BookAuthorsFirstParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.BOOK_AUTHORS_FIRST;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // 1. Авторы: от начала до точки с пробелом после инициалов
        int authorsEnd = findAuthorsEnd(line, pos);
        if (authorsEnd > pos) {
            String value = line.substring(pos, authorsEnd).trim();
            tokens.put("AUTHORS", new ParsedToken("AUTHORS", value, pos, authorsEnd));
            pos = skipDelimiter(line, authorsEnd, ". ");
        }

        // 2. Заглавие: от текущей позиции до " . — " перед городом
        int titleEnd = findTitleEnd(line, pos);
        if (titleEnd > pos) {
            String value = line.substring(pos, titleEnd).trim();
            tokens.put("TITLE", new ParsedToken("TITLE", value, pos, titleEnd));
            pos = skipDelimiter(line, titleEnd, ". — ");
        }

        // 3. Сведения об издании (опционально): "2-е изд., испр. и доп. — "
        if (pos < line.length() && isEdition(line, pos)) {
            int editionEnd = findEditionEnd(line, pos);
            String value = line.substring(pos, editionEnd).trim();
            tokens.put("EDITION", new ParsedToken("EDITION", value, pos, editionEnd));
            pos = skipDelimiter(line, editionEnd, ". — ");
        }

        // 4. Город: до двоеточия
        int cityEnd = line.indexOf(":", pos);
        if (cityEnd > pos) {
            String value = line.substring(pos, cityEnd).trim();
            tokens.put("CITY", new ParsedToken("CITY", value, pos, cityEnd));
            pos = skipDelimiter(line, cityEnd, ": ");
        }

        // 5. Издательство: до запятой перед годом
        int publisherEnd = line.indexOf(",", pos);
        if (publisherEnd > pos) {
            String value = line.substring(pos, publisherEnd).trim();
            tokens.put("PUBLISHER", new ParsedToken("PUBLISHER", value, pos, publisherEnd));
            pos = skipDelimiter(line, publisherEnd, ", ");
        }

        // 6. Год: до точки
        int yearEnd = findYearEnd(line, pos);
        String yearValue = line.substring(pos, Math.min(yearEnd, line.length())).trim();
        // Проверяем, что это действительно год (цифры) — иначе пропускаем
        if (yearValue.matches("\\d{2,4}") && yearEnd > pos) {
            tokens.put("YEAR", new ParsedToken("YEAR", yearValue, pos, yearEnd));
            pos = skipDelimiter(line, yearEnd, ". — ");
        } else if (yearEnd > pos) {
            // Не год — пропускаем, это уже страницы
            pos = yearEnd;
        }

        // 7. Страницы: до конца строки
        if (pos < line.length()) {
            String value = line.substring(pos).trim();
            tokens.put("PAGES", new ParsedToken("PAGES", value, pos, line.length()));
        }

        return new ParsedRecord(SourceType.BOOK_AUTHORS_FIRST, rawLine, tokens);
    }

    // ==================== Вспомогательные методы ====================

    /** Найти конец блока авторов: после последнего инициала перед точкой */
    private int findAuthorsEnd(String line, int from) {
        // Ищем паттерн: Фамилия И. О. (точка после второго инициала)
        int dot = line.indexOf(".", from);
        if (dot < 0) return from;
        // Пропускаем возможные пробелы после точки — ищем точку с пробелом дальше
        int end = line.indexOf(". ", dot);
        if (end < 0) {
            // Если ". " нет, возможно конец строки — берём до следующей большой буквы (начало названия)
            end = dot;
        }
        return end + 1; // включаем последнюю точку
    }

    /** Конец заглавия: перед " . — " или " : " перед городом */
    private int findTitleEnd(String line, int from) {
        int dash = line.indexOf(". —", from);
        if (dash >= 0) return dash;
        // Если нет ". —", ищем " — " (без точки, ошибка редактора)
        dash = line.indexOf(" —", from);
        return dash >= 0 ? dash : line.length();
    }

    /** Проверка, что в текущей позиции — сведения об издании */
    private boolean isEdition(String line, int from) {
        return line.substring(from).matches("^\\d+-е\\s+изд.*");
    }

    /** Конец сведений об издании */
    private int findEditionEnd(String line, int from) {
        int dash = line.indexOf(". —", from);
        return dash >= 0 ? dash : line.indexOf(" —", from);
    }

    /** Пропустить разделитель и вернуть новую позицию */
    private int skipDelimiter(String line, int from, String delimiter) {
        int newPos = from;
        for (int i = 0; i < delimiter.length() && newPos < line.length(); i++) {
            if (line.charAt(newPos) == delimiter.charAt(i)) {
                newPos++;
            } else if (delimiter.charAt(i) == ' ' && line.charAt(newPos) == ' ') {
                newPos++;
            } else {
                break;
            }
        }
        return newPos;
    }

    /** Найти конец года — точка перед " — " или конец строки */
    private int findYearEnd(String line, int from) {
        int dash = line.indexOf(" —", from);
        if (dash >= 0) {
            int dot = line.lastIndexOf(".", dash);
            if (dot >= from) return dot;
        }
        int dot = line.indexOf(".", from);
        return dot >= 0 ? dot : line.length();
    }
}
