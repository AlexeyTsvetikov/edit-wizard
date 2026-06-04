package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BookAuthorsFirstParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) { return type == SourceType.BOOK_AUTHORS_FIRST; }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        int authorsEnd = TokenExtractor.findAuthorsEnd(line, pos);
        tokens.put("AUTHORS", new ParsedToken("AUTHORS", line.substring(pos, authorsEnd).trim(), pos, authorsEnd));
        pos = TokenExtractor.skipDelimiter(line, authorsEnd, ". ");

        int titleEnd = line.indexOf(". —", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(" —", pos);
        tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
        pos = TokenExtractor.skipDelimiter(line, titleEnd, ". — ");

        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.BOOK_AUTHORS_FIRST, rawLine, tokens);
    }
}
