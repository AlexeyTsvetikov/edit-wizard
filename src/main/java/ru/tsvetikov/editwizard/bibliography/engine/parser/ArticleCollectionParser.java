package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ArticleCollectionParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ARTICLE_COLLECTION;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();

        int pos = TokenExtractor.extractAuthorsAndTitle(line, tokens);

        int collectionEnd = findCollectionEnd(line, pos);
        pos = TokenExtractor.extractToken(line, pos, collectionEnd, "COLLECTION", ". — ", tokens);

        pos = TokenExtractor.extractCity(line, pos, tokens);
        pos = TokenExtractor.extractYear(line, pos, tokens);

        if (pos < line.length() && (line.substring(pos).matches("^Т\\.\\s*\\d+.*") || line.substring(pos).matches("^Вып\\.\\s*\\d+.*"))) {
            int volumeEnd = TokenExtractor.findBlockEnd(line, pos);
            pos = TokenExtractor.extractToken(line, pos, volumeEnd, "VOLUME", ". — ", tokens);
        }

        TokenExtractor.extractPages(line, pos, tokens);

        return new ParsedRecord(SourceType.ARTICLE_COLLECTION, rawLine, tokens);
    }

    private int findCollectionEnd(String line, int from) {
        int pos = from;
        while (pos < line.length()) {
            int dash = line.indexOf(". —", pos);
            if (dash < 0) {
                dash = line.indexOf(" —", pos);
            }
            if (dash < 0) return line.length();

            // Проверяем, что после ". — " идёт похожее на город (заглавная буква, точка/двоеточие/запятая)
            int after = dash + 4; // пропускаем ". — "
            if (after < line.length()) {
                String rest = line.substring(after).trim();
                // Город: "М.", "СПб.", "Ижевск", "Н. Новгород" — начинается с заглавной
                if (rest.matches("^[А-ЯA-Z].*")) {
                    return dash;
                }
            }
            pos = dash + 1; // ищем дальше
        }
        return line.length();
    }
}