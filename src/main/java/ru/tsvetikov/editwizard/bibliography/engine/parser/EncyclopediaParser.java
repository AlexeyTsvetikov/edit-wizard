package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EncyclopediaParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ARTICLE_ENCYCLOPEDIA;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractAuthors(line, tokens);

        // Название статьи — до " // "
        int articleEnd = line.indexOf(" // ", pos);
        pos = TokenExtractor.extractToken(line, pos, articleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.startsWith(" // ", pos)) pos += 4;

        // Название энциклопедии — до ": в"
        int encEnd = line.indexOf(": в", pos);
        if (encEnd < 0) encEnd = line.indexOf(": В", pos);
        pos = TokenExtractor.extractToken(line, pos, encEnd, "ENCYCLOPEDIA", "", tokens);

        pos = TokenExtractor.extractVolumeInfo(line, pos, tokens);
        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.ARTICLE_ENCYCLOPEDIA, rawLine, tokens);
    }
}
