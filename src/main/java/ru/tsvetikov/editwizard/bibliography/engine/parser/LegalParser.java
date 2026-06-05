package ru.tsvetikov.editwizard.bibliography.engine.parser;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.util.TokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LegalParser implements BibliographyParser {

    @Override
    public boolean canParse(SourceType type) {
        return type == SourceType.LEGAL;
    }

    @Override
    public ParsedRecord parse(String rawLine) {
        Map<String, ParsedToken> tokens = new LinkedHashMap<>();
        String line = rawLine.trim();
        int pos = 0;

        // Название документа — до " // " или " : "
        int titleEnd = line.indexOf(" // ", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(": ", pos);
        if (titleEnd < 0) titleEnd = line.indexOf(". —", pos);
        pos = TokenExtractor.extractToken(line, pos, titleEnd, "TITLE", "", tokens);

        // Остаток — либо " // источник" либо ". — газетные данные"
        if (pos < line.length() && line.startsWith(" // ", pos)) {
            pos += 4;
            // Источник — до "URL:" или до конца
            int sourceEnd = line.indexOf("URL:", pos);
            if (sourceEnd < 0) sourceEnd = line.indexOf(" — ", pos);
            if (sourceEnd > pos) {
                tokens.put("SOURCE", new ParsedToken("SOURCE", line.substring(pos, sourceEnd).trim(), pos, sourceEnd));
                pos = sourceEnd;
            }
        }

        // URL и дата обращения
        if (pos < line.length() && line.contains("URL:")) {
            int urlStart = line.indexOf("URL:", pos);
            if (urlStart > pos) {
                pos = urlStart;
            }
            int urlEnd = line.indexOf(" (дата обращения:", pos);
            if (urlEnd > pos) {
                tokens.put("URL", new ParsedToken("URL", line.substring(pos, urlEnd).trim(), pos, urlEnd));
                pos = urlEnd;
            }
            int accessEnd = line.indexOf(")", pos);
            if (accessEnd > pos && line.substring(pos).startsWith(" (дата обращения:")) {
                tokens.put("ACCESS_DATE", new ParsedToken("ACCESS_DATE", line.substring(pos, accessEnd + 1).trim(), pos, accessEnd + 1));
            }
        }

        return new ParsedRecord(SourceType.LEGAL, rawLine, tokens);
    }
}
