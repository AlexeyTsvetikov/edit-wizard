package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BookAuthorsFirstParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.BOOK_AUTHORS_FIRST;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        int authorsEnd = findAuthorsEnd(line, pos);
        if (authorsEnd > pos) {
            String value = line.substring(pos, authorsEnd).trim();
            tokens.put("AUTHORS", new ParsedToken("AUTHORS", value, pos, authorsEnd));
            pos = skipDelimiter(line, authorsEnd, ". ");
        }

        int titleEnd = findTitleEnd(line, pos);
        if (titleEnd > pos) {
            String value = line.substring(pos, titleEnd).trim();
            tokens.put("TITLE", new ParsedToken("TITLE", value, pos, titleEnd));
            pos = skipDelimiter(line, titleEnd, ". — ");
        }

        if (pos < line.length() && isEdition(line, pos)) {
            int editionEnd = findEditionEnd(line, pos);
            String value = line.substring(pos, editionEnd).trim();
            tokens.put("EDITION", new ParsedToken("EDITION", value, pos, editionEnd));
            pos = skipDelimiter(line, editionEnd, ". — ");
        }

        int cityEnd = line.indexOf(":", pos);
        if (cityEnd > pos) {
            String value = line.substring(pos, cityEnd).trim();
            tokens.put("CITY", new ParsedToken("CITY", value, pos, cityEnd));
            pos = skipDelimiter(line, cityEnd, ": ");
        }

        int publisherEnd = line.indexOf(",", pos);
        if (publisherEnd > pos) {
            String value = line.substring(pos, publisherEnd).trim();
            tokens.put("PUBLISHER", new ParsedToken("PUBLISHER", value, pos, publisherEnd));
            pos = skipDelimiter(line, publisherEnd, ", ");
        }

        int yearEnd = findYearEnd(line, pos);
        String yearValue = line.substring(pos, Math.min(yearEnd, line.length())).trim();
        if (yearValue.matches("\\d{2,4}") && yearEnd > pos) {
            tokens.put("YEAR", new ParsedToken("YEAR", yearValue, pos, yearEnd));
            pos = skipDelimiter(line, yearEnd, ". — ");
        } else if (yearEnd > pos) {
            pos = yearEnd;
        }

        if (pos < line.length()) {
            String value = line.substring(pos).trim();
            tokens.put("PAGES", new ParsedToken("PAGES", value, pos, line.length()));
        }

        return new ParsedRecord(SourceType.BOOK_AUTHORS_FIRST, rawLine, tokens);
    }

    private int findAuthorsEnd(String line, int from) {
        int dot = line.indexOf(".", from);
        if (dot < 0) return from;
        int end = line.indexOf(". ", dot);
        if (end < 0) {
            end = dot;
        }
        return end + 1;
    }

    private int findTitleEnd(String line, int from) {
        int dash = line.indexOf(". —", from);
        if (dash >= 0) return dash;
        dash = line.indexOf(" —", from);
        return dash >= 0 ? dash : line.length();
    }

    private boolean isEdition(String line, int from) {
        return line.substring(from).matches("^\\d+-е\\s+изд.*");
    }

    private int findEditionEnd(String line, int from) {
        int dash = line.indexOf(". —", from);
        return dash >= 0 ? dash : line.indexOf(" —", from);
    }

    private int skipDelimiter(String line, int from, String delimiter) {
        int newPos = from;
        for (int i = 0; i < delimiter.length() && newPos < line.length(); i++) {
            if (line.charAt(newPos) == delimiter.charAt(i)) {
                newPos++;
            } else if (delimiter.charAt(i) == ' ' && line.charAt(newPos) == ' ') {
                newPos++;
            } else {
                break;
            }
        }
        return newPos;
    }

    private int findYearEnd(String line, int from) {
        int dash = line.indexOf(" —", from);
        if (dash >= 0) {
            int dot = line.lastIndexOf(".", dash);
            if (dot >= from) return dot;
        }
        int dot = line.indexOf(".", from);
        return dot >= 0 ? dot : line.length();
    }
}
