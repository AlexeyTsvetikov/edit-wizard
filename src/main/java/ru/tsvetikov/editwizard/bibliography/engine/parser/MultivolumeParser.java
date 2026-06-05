package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class MultivolumeParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.MULTIVOLUME;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractAuthors(line, tokens);

        // Заглавие — до ": в N т."
        int titleEnd = line.indexOf(": в ", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(": В ", pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.charAt(pos) == ':') pos += 2;

        pos = TokenExtractor.extractVolumeInfo(line, pos, tokens);
        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.MULTIVOLUME, rawLine, tokens);
    }
}