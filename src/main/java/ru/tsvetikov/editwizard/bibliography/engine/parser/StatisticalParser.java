package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class StatisticalParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.STATISTICAL_COLLECTION;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // Название — до " / " или ". — "
        int titleEnd = line.indexOf(" / ", pos);
        if (titleEnd > pos) {
            tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
            pos = titleEnd + 3;
        } else {
            titleEnd = TokenExtractor.findBlockEnd(line, pos);
            if (titleEnd < 0) titleEnd = line.indexOf(". —", pos);
            pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", ". — ", tokens);
            TokenExtractor.extractBookTail(line, pos, tokens);
            return new ParsedRecord(SourceType.STATISTICAL_COLLECTION, rawLine, tokens);
        }

        // Редколлегия — до ";"
        int editorEnd = line.indexOf(";", pos);
        if (editorEnd > pos) {
            tokens.put("EDITOR", new ParsedToken("EDITOR", line.substring(pos, editorEnd).trim(), pos, editorEnd));
            pos = TokenExtractor.skipDelimiter(line, editorEnd, "; ");
        }

        // Издательство после ";" — до ". — "
        int publisherEnd = TokenExtractor.findBlockEnd(line, pos);
        if (publisherEnd < 0) publisherEnd = line.indexOf(". —", pos);
        if (publisherEnd > pos) {
            tokens.put("PUBLISHER", new ParsedToken("PUBLISHER", line.substring(pos, publisherEnd).trim(), pos, publisherEnd));
            pos = TokenExtractor.skipDelimiter(line, publisherEnd, ". — ");
        }

        // Город, год, страницы
        pos = TokenExtractor.extractCity(line, pos, tokens);
        pos = TokenExtractor.extractYear(line, pos, tokens);
        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.STATISTICAL_COLLECTION, rawLine, tokens);
    }
}
