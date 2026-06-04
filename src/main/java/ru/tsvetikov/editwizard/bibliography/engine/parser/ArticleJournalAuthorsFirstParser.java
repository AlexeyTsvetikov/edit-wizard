package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ArticleJournalAuthorsFirstParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ARTICLE_JOURNAL_AUTHORS_FIRST;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // 1. Авторы — до ". "
        int authorsEnd = TokenExtractor.findAuthorsEnd(line, pos);
        if (authorsEnd > pos) {
            String value = line.substring(pos, authorsEnd).trim();
            tokens.put("AUTHORS", new ParsedToken("AUTHORS", value, pos, authorsEnd));
            pos = TokenExtractor.skipDelimiter(line, authorsEnd, ". ");
        }

        // 2. Заглавие статьи — до " // "
        int titleEnd = line.indexOf(" // ", pos);
        if (titleEnd > pos) {
            String value = line.substring(pos, titleEnd).trim();
            tokens.put("TITLE", new ParsedToken("TITLE", value, pos, titleEnd));
            pos = titleEnd + 4; // пропускаем " // "
        }

        // 3. Название журнала — до ". — "
        int journalEnd = TokenExtractor.findBlockEnd(line, pos);
        if (journalEnd > pos) {
            String value = line.substring(pos, journalEnd).trim();
            tokens.put("JOURNAL", new ParsedToken("JOURNAL", value, pos, journalEnd));
            pos = TokenExtractor.skipDelimiter(line, journalEnd, ". — ");
        }

        // 4. Год — до ". — "
        int yearEnd = TokenExtractor.findBlockEnd(line, pos);
        if (yearEnd > pos) {
            String value = line.substring(pos, yearEnd).trim();
            tokens.put("YEAR", new ParsedToken("YEAR", value, pos, yearEnd));
            pos = TokenExtractor.skipDelimiter(line, yearEnd, ". — ");
        }

        // 5. Номер выпуска — до ". — "
        int issueEnd = TokenExtractor.findBlockEnd(line, pos);
        if (issueEnd > pos) {
            String value = line.substring(pos, issueEnd).trim();
            tokens.put("ISSUE", new ParsedToken("ISSUE", value, pos, issueEnd));
            pos = TokenExtractor.skipDelimiter(line, issueEnd, ". — ");
        }

        // 6. Страницы — до конца строки
        if (pos < line.length()) {
            String value = line.substring(pos).trim();
            tokens.put("PAGES", new ParsedToken("PAGES", value, pos, line.length()));
        }

        return new ParsedRecord(SourceType.ARTICLE_JOURNAL_AUTHORS_FIRST, rawLine, tokens);
    }
}