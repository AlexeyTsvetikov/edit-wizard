package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RulesParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.RULES;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // Название — до ": "
        int titleEnd = line.indexOf(": ", pos);
        if (titleEnd > pos) {
            tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
            pos = TokenExtractor.skipDelimiter(line, titleEnd, ": ");
        }

        // Код — до ": утв." или ". — "
        int codeEnd = line.indexOf(": утв.", pos);
        if (codeEnd < 0) codeEnd = line.indexOf(": Утв.", pos);
        if (codeEnd < 0) codeEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, codeEnd, "CODE", "", tokens);
        if (pos < line.length() && line.charAt(pos) == ':') pos += 2;

        // Остаток — до ". — "
        int infoEnd = TokenExtractor.findBlockEnd(line, pos);
        if (infoEnd > pos) {
            tokens.put("INFO", new ParsedToken("INFO", line.substring(pos, infoEnd).trim(), pos, infoEnd));
            pos = TokenExtractor.skipDelimiter(line, infoEnd, ". — ");
        }

        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.RULES, rawLine, tokens);
    }
}
