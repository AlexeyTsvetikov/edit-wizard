package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GostFromCollectionParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.GOST_FROM_COLLECTION;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // ГОСТ номер — до ". "
        int numberEnd = line.indexOf(". ", pos);
        pos = TokenExtractor.extractToken(line, pos, numberEnd, "GOST_NUMBER", "", tokens);
        if (pos < line.length() && line.charAt(pos) == '.') pos += 2;

        // Название — до " // "
        int titleEnd = line.indexOf(" // ", pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.startsWith(" // ", pos)) pos += 4;

        // Название сборника — до ". — "
        int collectionEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, collectionEnd, "COLLECTION", ". — ", tokens);

        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.GOST_FROM_COLLECTION, rawLine, tokens);
    }
}
