package ru.tsvetikov.editwizard.bibliography.engine.util;

import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;

import java.util.Map;


public class TokenExtractor {

    public static boolean isEdition(String line, int from) {
        return line.length() > from && line.substring(from).matches("^\\d+-е\\s+изд.*");
    }

    public static int findBlockEnd(String line, int from) {
        int dash = line.indexOf(". —", from);
        return dash >= 0 ? dash : line.indexOf(" —", from);
    }

    public static int findYearEnd(String line, int from) {
        int dash = line.indexOf(" —", from);
        if (dash >= 0) {
            int dot = line.lastIndexOf(".", dash);
            if (dot >= from) return dot;
        }
        int dot = line.indexOf(".", from);
        return dot >= 0 ? dot : line.length();
    }

    public static int skipDelimiter(String line, int from, String delimiter) {
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

    public static int findAuthorsEnd(String line, int from) {
        int dot = line.indexOf(".", from);
        if (dot < 0) return from;
        int end = line.indexOf(". ", dot);
        if (end < 0) {
            end = dot;
        }
        return end + 1;
    }

    public static int findTitleBeforeSlash(String line, int from) {
        return line.indexOf(" / ", from);
    }

    public static int extractTitleAndBlockAfterSlash(String line, Map<String, ParsedToken> tokens, String tokenCode) {
        int pos = 0;

        int titleEnd = findTitleBeforeSlash(line, pos);
        tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
        pos = titleEnd + 3;

        int blockEnd = findBlockEnd(line, pos);
        tokens.put(tokenCode, new ParsedToken(tokenCode, line.substring(pos, blockEnd).trim(), pos, blockEnd));
        pos = skipDelimiter(line, blockEnd, ". — ");

        return pos;
    }

    public static void extractBookTail(String line, int from, Map<String, ParsedToken> tokens) {
        int pos = from;

        // Издание (опционально)
        if (pos < line.length() && isEdition(line, pos)) {
            int editionEnd = findBlockEnd(line, pos);
            String value = line.substring(pos, editionEnd).trim();
            tokens.put("EDITION", new ParsedToken("EDITION", value, pos, editionEnd));
            pos = skipDelimiter(line, editionEnd, ". — ");
        }

        // Город
        int cityEnd = line.indexOf(":", pos);
        if (cityEnd > pos) {
            String value = line.substring(pos, cityEnd).trim();
            tokens.put("CITY", new ParsedToken("CITY", value, pos, cityEnd));
            pos = skipDelimiter(line, cityEnd, ": ");
        }

        // Издательство
        int publisherEnd = line.indexOf(",", pos);
        if (publisherEnd > pos) {
            String value = line.substring(pos, publisherEnd).trim();
            tokens.put("PUBLISHER", new ParsedToken("PUBLISHER", value, pos, publisherEnd));
            pos = skipDelimiter(line, publisherEnd, ", ");
        }

        // Год
        int yearEnd = findYearEnd(line, pos);
        if (yearEnd > pos) {
            String value = line.substring(pos, yearEnd).trim();
            if (value.matches("\\d{2,4}")) {
                tokens.put("YEAR", new ParsedToken("YEAR", value, pos, yearEnd));
                pos = skipDelimiter(line, yearEnd, ". — ");
            }
        }

        // Страницы
        if (pos < line.length()) {
            String value = line.substring(pos).trim();
            tokens.put("PAGES", new ParsedToken("PAGES", value, pos, line.length()));
        }
    }
}