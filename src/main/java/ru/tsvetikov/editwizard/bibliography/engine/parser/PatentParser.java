package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PatentParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.PATENT;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // Патент — до ". "
        int patentEnd = line.indexOf(". ", pos);
        if (patentEnd > pos) {
            tokens.put("PATENT_NUMBER", new ParsedToken("PATENT_NUMBER", line.substring(pos, patentEnd).trim(), pos, patentEnd));
            pos = TokenExtractor.skipDelimiter(line, patentEnd, ". ");
        }

        // Название — до " / " или ". — "
        int titleEnd = line.indexOf(" / ", pos);
        if (titleEnd < 0) titleEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.startsWith(" / ", pos)) pos += 3;

        // Авторы/патентообладатель — до ". — " или конца перед страницами
        int infoEnd = TokenExtractor.findBlockEnd(line, pos);
        if (infoEnd < 0) infoEnd = line.lastIndexOf(". —", pos);
        if (infoEnd > pos) {
            tokens.put("PATENT_INFO", new ParsedToken("PATENT_INFO", line.substring(pos, infoEnd).trim(), pos, infoEnd));
            pos = TokenExtractor.skipDelimiter(line, infoEnd, ". — ");
        }

        // Страницы
        TokenExtractor.extractPages(line, pos, tokens);
        return new ParsedRecord(SourceType.PATENT, rawLine, tokens);
    }
}
