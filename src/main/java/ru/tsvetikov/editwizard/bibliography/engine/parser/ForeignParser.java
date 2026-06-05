package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ForeignParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.FOREIGN;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // Пробуем извлечь авторов (может начинаться с названия)
        if (hasAuthors(line)) {
            pos = extractForeignAuthors(line, tokens);
        }

        // Заглавие — до " // " или " / " или ". — "
        int titleEnd = line.indexOf(" // ", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(" / ", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(". —", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(" —", pos);

        if (titleEnd > pos) {
            tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
            pos = titleEnd;
        }

        // Остаток строки
        if (pos < line.length()) {
            tokens.put("INFO", new ParsedToken("INFO", line.substring(pos).trim(), pos, line.length()));
        }

        return new ParsedRecord(SourceType.FOREIGN, rawLine, tokens);
    }

    private boolean hasAuthors(String line) {
        return line.matches("^[A-Z][a-z]+\\s+[A-Z]\\..*");
    }

    private int extractForeignAuthors(String line, Map<String, ParsedToken> tokens) {
        // Упрощённо: берём до первой точки с пробелом
        int dot = line.indexOf(". ");
        if (dot > 0) {
            tokens.put("AUTHORS", new ParsedToken("AUTHORS", line.substring(0, dot + 1).trim(), 0, dot + 1));
            return dot + 2;
        }
        return 0;
    }
}
