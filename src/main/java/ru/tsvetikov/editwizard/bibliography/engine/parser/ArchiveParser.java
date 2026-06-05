package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ArchiveParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ARCHIVE_DOCUMENT;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // ГАРФ — до ". — "
        int archiveEnd = line.indexOf(". —", pos);
        if (archiveEnd < 0) archiveEnd = line.indexOf(" —", pos);
        if (archiveEnd > pos) {
            tokens.put("ARCHIVE", new ParsedToken("ARCHIVE", line.substring(pos, archiveEnd).trim(), pos, archiveEnd));
            pos = TokenExtractor.skipDelimiter(line, archiveEnd, ". — ");
        }

        // Фонд — до ". — "
        int fondEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, fondEnd, "FOND", ". — ", tokens);

        // Опись — до ". — "
        int opisEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, opisEnd, "OPIS", ". — ", tokens);

        // Дело — до ". — "
        int deloEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, deloEnd, "DELO", ". — ", tokens);

        // Листы
        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.ARCHIVE_DOCUMENT, rawLine, tokens);
    }
}
