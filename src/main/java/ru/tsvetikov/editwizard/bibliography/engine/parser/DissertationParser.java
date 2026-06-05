package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DissertationParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.DISSERTATION;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractAuthors(line, tokens);

        int titleEnd = line.indexOf(": дис. …", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(": дис.", pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.charAt(pos) == ':') pos += 2;

        int degreeEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, degreeEnd, "DEGREE", ". — ", tokens);

        pos = TokenExtractor.extractCity(line, pos, tokens);
        pos = TokenExtractor.extractYear(line, pos, tokens);
        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.DISSERTATION, rawLine, tokens);
    }
}