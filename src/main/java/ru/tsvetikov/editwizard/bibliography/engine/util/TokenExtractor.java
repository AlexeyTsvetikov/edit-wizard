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
        if (titleEnd > pos) {
            tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
            pos = titleEnd + 3;
        }

        int blockEnd = findBlockEnd(line, pos);
        if (blockEnd > pos) {
            tokens.put(tokenCode, new ParsedToken(tokenCode, line.substring(pos, blockEnd).trim(), pos, blockEnd));
            pos = skipDelimiter(line, blockEnd, ". — ");
        }

        return pos;
    }

    public static int extractYear(String line, int from, Map<String, ParsedToken> tokens) {
        int yearEnd = findBlockEnd(line, from);
        if (yearEnd > from) {
            String value = line.substring(from, yearEnd).trim();
            if (value.matches("\\d{4}")) {
                tokens.put("YEAR", new ParsedToken("YEAR", value, from, yearEnd));
                return skipDelimiter(line, yearEnd, ". — ");
            }
        }
        return from;
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
            if (value.startsWith(". — ")) {
                value = value.substring(4);
                pos += 4;
            }
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

        if (pos < line.length()) {
            String value = line.substring(pos).trim();
            tokens.put("PAGES", new ParsedToken("PAGES", value, pos, line.length()));
        } else {
            // Создаём пустой токен, чтобы правила могли найти ошибку
            tokens.put("PAGES", new ParsedToken("PAGES", "", pos, pos));
        }
    }

    public static int extractAuthorsAndTitle(String line, Map<String, ParsedToken> tokens) {
        int pos = extractAuthors(line, tokens);

        int titleEnd = line.indexOf(" // ", pos);
        if (titleEnd > pos) {
            tokens.put("TITLE", new ParsedToken("TITLE", line.substring(pos, titleEnd).trim(), pos, titleEnd));
            pos = titleEnd + 4;
        }

        return pos;
    }

    public static int extractCity(String line, int from, Map<String, ParsedToken> tokens) {
        int cityEnd = line.indexOf(",", from);
        if (cityEnd > from) {
            tokens.put("CITY", new ParsedToken("CITY", line.substring(from, cityEnd).trim(), from, cityEnd));
            return skipDelimiter(line, cityEnd, ", ");
        }
        return from;
    }

    public static int extractAuthors(String line, Map<String, ParsedToken> tokens) {
        int pos = 0;
        int authorsEnd = findAuthorsEnd(line, pos);
        if (authorsEnd > pos) {
            tokens.put("AUTHORS", new ParsedToken("AUTHORS", line.substring(pos, authorsEnd).trim(), pos, authorsEnd));
            pos = skipDelimiter(line, authorsEnd, ". ");
        }
        return pos;
    }

    public static int extractToken(String line, int from, int end, String code, String delimiter, Map<String, ParsedToken> tokens) {
        if (end > from) {
            tokens.put(code, new ParsedToken(code, line.substring(from, end).trim(), from, end));
            return delimiter.isEmpty() ? end : skipDelimiter(line, end, delimiter);
        }
        return from;
    }

    public static void extractPages(String line, int from, Map<String, ParsedToken> tokens) {
        if (from < line.length()) {
            tokens.put("PAGES", new ParsedToken("PAGES", line.substring(from).trim(), from, line.length()));
        }
    }

    public static int extractVolumeInfo(String line, int from, Map<String, ParsedToken> tokens) {
        int pos = from;

        // Пропускаем ": " если есть
        if (pos < line.length() && line.charAt(pos) == ':') pos += 2;

        // "в N т." — до "Т. X"
        int volumeInfoEnd = line.indexOf("Т. ", pos);
        if (volumeInfoEnd < 0) volumeInfoEnd = line.indexOf("т. ", pos);
        if (volumeInfoEnd > pos) {
            tokens.put("VOLUME_INFO", new ParsedToken("VOLUME_INFO", line.substring(pos, volumeInfoEnd).trim(), pos, volumeInfoEnd));
            pos = volumeInfoEnd;
        }

        // "Т. X" — до ". — "
        int volumeEnd = findBlockEnd(line, pos);
        pos = extractToken(line, pos, volumeEnd, "VOLUME", ". — ", tokens);

        return pos;
    }

    public static int extractResponsibility(String line, int from, Map<String, ParsedToken> tokens) {
        int pos = from;
        if (pos < line.length() && line.startsWith(" / ", pos)) {
            pos += 3;
            int end = findBlockEnd(line, pos);
            if (end < 0) {
                end = line.indexOf(" // ", pos);
            }
            if (end < 0) {
                end = line.length();
            }
            if (end > pos) {
                String value = line.substring(pos, end).trim();
                tokens.put("RESPONSIBILITY", new ParsedToken("RESPONSIBILITY", value, pos, end));
                pos = end;
            }
        }
        return pos;
    }
}