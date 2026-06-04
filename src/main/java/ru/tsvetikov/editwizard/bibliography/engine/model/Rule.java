package ru.tsvetikov.editwizard.bibliography.engine.model;

public record Rule(
        String code,
        String targetToken,
        String searchPattern,
        String message,
        String expectedView,
        String sourceTypes
) {}
