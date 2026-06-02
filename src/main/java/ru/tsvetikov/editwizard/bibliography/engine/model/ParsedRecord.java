package ru.tsvetikov.editwizard.bibliography.engine.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record ParsedRecord(SourceType sourceType, String rawLine, Map<String, ParsedToken> tokens) {

    public ParsedRecord {
        tokens = Collections.unmodifiableMap(new LinkedHashMap<>(tokens));
    }

    public ParsedToken getToken(String code) {
        return tokens.get(code);
    }

    @Override
    public String toString() {
        return String.format("ParsedRecord[%s] %d tokens: %s", sourceType, tokens.size(), tokens.keySet());
    }
}