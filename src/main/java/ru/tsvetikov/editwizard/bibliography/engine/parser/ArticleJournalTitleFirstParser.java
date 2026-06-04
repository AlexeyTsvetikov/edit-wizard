package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ArticleJournalTitleFirstParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ARTICLE_JOURNAL_TITLE_FIRST;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // 1. Заглавие — до " / "
        int titleEnd = line.indexOf(" / ", pos);
        if (titleEnd > pos) {
            tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
            pos = titleEnd + 3;
        }

        // 2. Авторы — от " / " до " // "
        int authorsEnd = line.indexOf(" // ", pos);
        if (authorsEnd > pos) {
            tokens.put("AUTHORS", new ParsedToken("AUTHORS", line.substring(pos, authorsEnd).trim(), pos, authorsEnd));
            pos = authorsEnd + 4;
        }

        // 3. Журнал — до ". — "
        int journalEnd = TokenExtractor.findBlockEnd(line, pos);
        if (journalEnd > pos) {
            tokens.put("JOURNAL", new ParsedToken("JOURNAL", line.substring(pos, journalEnd).trim(), pos, journalEnd));
            pos = TokenExtractor.skipDelimiter(line, journalEnd, ". — ");
        }

        // 4. Год
        int yearEnd = TokenExtractor.findBlockEnd(line, pos);
        if (yearEnd > pos) {
            tokens.put("YEAR", new ParsedToken("YEAR", line.substring(pos, yearEnd).trim(), pos, yearEnd));
            pos = TokenExtractor.skipDelimiter(line, yearEnd, ". — ");
        }

        // 5. Номер
        int issueEnd = TokenExtractor.findBlockEnd(line, pos);
        if (issueEnd > pos) {
            tokens.put("ISSUE", new ParsedToken("ISSUE", line.substring(pos, issueEnd).trim(), pos, issueEnd));
            pos = TokenExtractor.skipDelimiter(line, issueEnd, ". — ");
        }

        // 6. Страницы
        if (pos < line.length()) {
            tokens.put("PAGES", new ParsedToken("PAGES", line.substring(pos).trim(), pos, line.length()));
        }

        return new ParsedRecord(SourceType.ARTICLE_JOURNAL_TITLE_FIRST, rawLine, tokens);
    }
}
