package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ChapterBookParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.CHAPTER_BOOK;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractAuthors(line, tokens);

        // Название главы — до " // "
        int chapterEnd = line.indexOf(" // ", pos);
        pos = TokenExtractor.extractToken(line, pos, chapterEnd, "CHAPTER", "", tokens);
        if (pos < line.length() && line.startsWith(" // ", pos)) pos += 4;

        // Название книги — до ". — "
        int bookEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, bookEnd, "BOOK_TITLE", ". — ", tokens);

        TokenExtractor.extractBookTail(line, pos, tokens);
        return new ParsedRecord(SourceType.CHAPTER_BOOK, rawLine, tokens);
    }
}
