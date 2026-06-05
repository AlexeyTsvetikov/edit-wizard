package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AbstractParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ABSTRACT;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        // Авторы
        int pos = TokenExtractor.extractAuthors(line, tokens);

        // Заглавие — до ": автореф."
        int titleEnd = line.indexOf(": автореф.", pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.charAt(pos) == ':') pos += 2;

        // Степень
        int degreeEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, degreeEnd, "DEGREE", ". — ", tokens);

        // Город
        pos = TokenExtractor.extractCity(line, pos, tokens);

        // Год
        pos = TokenExtractor.extractYear(line, pos, tokens);

        // Страницы
        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.ABSTRACT, rawLine, tokens);
    }
}
