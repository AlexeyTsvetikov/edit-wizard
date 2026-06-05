package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GostParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.GOST;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // ГОСТ номер — до ". "
        int numberEnd = line.indexOf(". ", pos);
        if (numberEnd > pos) {
            tokens.put("GOST_NUMBER", new ParsedToken("GOST_NUMBER", line.substring(pos, numberEnd).trim(), pos, numberEnd));
            pos = TokenExtractor.skipDelimiter(line, numberEnd, ". ");
        }

        // Заглавие — до ". — "
        int titleEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", ". — ", tokens);

        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.GOST, rawLine, tokens);
    }
}
