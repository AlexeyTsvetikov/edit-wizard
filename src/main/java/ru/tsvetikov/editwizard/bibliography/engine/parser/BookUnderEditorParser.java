package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BookUnderEditorParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) { return type == SourceType.BOOK_UNDER_EDITOR; }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractTitleAndBlockAfterSlash(line, tokens, "EDITOR");
        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.BOOK_UNDER_EDITOR, rawLine, tokens);
    }
}