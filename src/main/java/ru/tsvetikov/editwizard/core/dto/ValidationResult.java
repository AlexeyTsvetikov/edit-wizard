package ru.tsvetikov.editwizard.core.dto;

import java.util.List;

public record ValidationResult(
        int lineNumber,
        String originalLine,
        String sourceType,
        boolean typeDetected,
        List<ValidationError> errors,
        String highlightedHtml
) {
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
