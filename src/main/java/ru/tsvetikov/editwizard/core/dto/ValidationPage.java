package ru.tsvetikov.editwizard.core.dto;

import java.util.List;

public record ValidationPage(
        String originalText,
        List<ValidationResult> results,
        int totalLines,
        int linesWithErrors,
        int linesWithUnknownType,
        int totalErrors
) {}
