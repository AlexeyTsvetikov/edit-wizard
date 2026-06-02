package ru.tsvetikov.editwizard.core.dto;

public record ValidationError(
        String ruleCode,
        String message,
        String expectedView,
        int charStart,
        int charEnd,
        String fragment
) {}
