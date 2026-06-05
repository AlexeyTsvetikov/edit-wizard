package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class NewspaperParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.NEWSPAPER_ARTICLE;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractAuthorsAndTitle(line, tokens);

        int newspaperEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, newspaperEnd, "NEWSPAPER", ". — ", tokens);

        pos = TokenExtractor.extractYear(line, pos, tokens);

        int dateEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, dateEnd, "DATE", ". — ", tokens);

        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.NEWSPAPER_ARTICLE, rawLine, tokens);
    }
}