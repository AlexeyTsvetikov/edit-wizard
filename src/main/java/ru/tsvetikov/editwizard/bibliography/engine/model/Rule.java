package ru.tsvetikov.editwizard.bibliography.engine.model;

/**
 * @param targetToken   null = вся строка
 * @param searchPattern регулярка
 */
public record Rule(
        String code,
        String targetToken,
        String searchPattern,
        String message,
        String expectedView
) {}
