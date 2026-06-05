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

        // Заглавие — до " / "
        int titleEnd = line.indexOf(" / ", pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
        if (pos < line.length() && line.startsWith(" / ", pos)) pos += 3;

        // Авторы — от " / " до " // "
        int authorsEnd = line.indexOf(" // ", pos);
        pos = TokenExtractor.extractToken(line, pos, authorsEnd, "AUTHORS", "", tokens);
        if (pos < line.length() && line.startsWith(" // ", pos)) pos += 4;

        // Журнал
        int journalEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, journalEnd, "JOURNAL", ". — ", tokens);

        // Год
        pos = TokenExtractor.extractYear(line, pos, tokens);

        // Номер
        int issueEnd = TokenExtractor.findBlockEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, issueEnd, "ISSUE", ". — ", tokens);

        // Страницы
        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.ARTICLE_JOURNAL_TITLE_FIRST, rawLine, tokens);
    }
}