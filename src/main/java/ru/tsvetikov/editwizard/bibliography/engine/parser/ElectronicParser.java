package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ElectronicParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.ELECTRONIC_WEBSITE
               || type == SourceType.ELECTRONIC_NEWSPAPER
               || type == SourceType.ELECTRONIC_JOURNAL
               || type == SourceType.ELECTRONIC_PORTAL
               || type == SourceType.ELECTRONIC_BOOK
               || type == SourceType.ELECTRONIC_COURSE
               || type == SourceType.ELECTRONIC_ARTICLE_DOI
               || type == SourceType.ELECTRONIC_LOCAL;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // Пробуем извлечь авторов (может не быть)
        if (hasAuthors(line)) {
            pos = TokenExtractor.extractAuthors(line, tokens);

            // Заглавие — до " // " или до " — " или до " . "
            int titleEnd = line.indexOf(" // ", pos);
            if (titleEnd < 0) titleEnd = line.indexOf(". —", pos);
            if (titleEnd < 0) titleEnd = line.indexOf(" — ", pos);
            if (titleEnd > pos) {
                pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);
                if (pos < line.length()) {
                    if (line.startsWith(" // ", pos)) pos += 4;
                }
            }
        } else {
            // Без авторов — всё до URL или до конца как название
            int urlStart = line.indexOf("URL:", pos);
            if (urlStart < 0) urlStart = line.indexOf(" — URL:", pos);
            if (urlStart > pos) {
                tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, urlStart).trim(), pos, urlStart));
                pos = urlStart;
            } else {
                tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos).trim(), pos, line.length()));
                pos = line.length();
            }
        }

        // Извлекаем URL и дату обращения
        extractUrlAndAccessDate(line, pos, tokens);

        return new ParsedRecord(determineType(line), rawLine, tokens);
    }

    private boolean hasAuthors(String line) {
        return line.matches("^[А-ЯЁ][а-яё]+\\s+[А-ЯЁ]\\..*");
    }

    private void extractUrlAndAccessDate(String line, int pos, Map<String, ParsedToken> tokens) {
        // URL
        int urlStart = line.indexOf("URL:", pos);
        if (urlStart < 0) urlStart = line.indexOf(" — URL:", pos);
        if (urlStart >= 0) {
            if (line.startsWith(" — ", urlStart - 3)) urlStart -= 3;
            int urlEnd = line.indexOf(" (дата обращения:", urlStart);
            if (urlEnd < 0) urlEnd = line.length();
            if (urlEnd > urlStart) {
                tokens.put("URL", new ParsedToken("URL", line.substring(urlStart, urlEnd).trim(), urlStart, urlEnd));
                pos = urlEnd;
            }

            // Дата обращения
            if (line.substring(pos).startsWith(" (дата обращения:")) {
                int accessEnd = line.indexOf(")", pos);
                if (accessEnd > pos) {
                    tokens.put("ACCESS_DATE", new ParsedToken("ACCESS_DATE", line.substring(pos, accessEnd + 1).trim(), pos, accessEnd + 1));
                }
            }
        }

        // DOI
        int doiStart = line.indexOf("DOI:", pos);
        if (doiStart < 0) doiStart = line.indexOf("doi:", pos);
        if (doiStart > pos) {
            tokens.put("DOI", new ParsedToken("DOI", line.substring(doiStart).trim(), doiStart, line.length()));
        }
    }

    private SourceType determineType(String line) {
        if (line.contains("CD-ROM") || line.contains("электрон. опт. диск")) return SourceType.ELECTRONIC_LOCAL;
        if (line.contains("DOI:") || line.contains("doi:")) return SourceType.ELECTRONIC_ARTICLE_DOI;
        if (line.contains("Moodle") || line.contains("дистанционный")) return SourceType.ELECTRONIC_COURSE;
        if (line.contains("ЭБС") || line.contains("e.lanbook")) return SourceType.ELECTRONIC_BOOK;
        if (line.contains("газета") || line.contains("газ.")) return SourceType.ELECTRONIC_NEWSPAPER;
        if (line.contains("журнал") || line.contains("Журнал")) return SourceType.ELECTRONIC_JOURNAL;
        if (line.contains("портал")) return SourceType.ELECTRONIC_PORTAL;
        return SourceType.ELECTRONIC_WEBSITE;
    }
}