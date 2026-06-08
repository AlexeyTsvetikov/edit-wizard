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

        int pos = TokenExtractor.extractAuthors(line, tokens);

        // Ищем конец названия: до " / " (приоритет) или до ". —"
        int slashPos = line.indexOf(" / ", pos);
        int dashPos = line.indexOf(". —", pos);
        if (dashPos < 0) dashPos = line.indexOf(" —", pos);

        int titleEnd;
        if (slashPos > 0 && (dashPos < 0 || slashPos < dashPos)) {
            titleEnd = slashPos;
        } else {
            titleEnd = dashPos;
        }
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);

        // Извлекаем ответственность, если есть " / "
        pos = TokenExtractor.extractResponsibility(line, pos, tokens);

        // Если был " / ", то после него может быть ". — " перед городом
        if (tokens.containsKey("RESPONSIBILITY")) {
            pos = TokenExtractor.skipDelimiter(line, pos, ". — ");
        }

        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.BOOK_AUTHORS_FIRST, rawLine, tokens);
    }
}