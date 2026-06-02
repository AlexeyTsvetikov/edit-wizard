package ru.tsvetikov.editwizard.bibliography.engine.model;

public record ParsedToken(String code, String value, int start, int end) {

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" (%d–%d)", code, value, start, end);
    }
}
