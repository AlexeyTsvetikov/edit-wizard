package ru.tsvetikov.editwizard.bibliography.engine.classifier;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;

@Component
public class SourceClassifier {

    public SourceType classify(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            return SourceType.UNKNOWN;
        }

        String line = rawLine.trim();

        // Порядок важен: от наиболее специфичных к общим

        // Архивные документы (уникальная структура)
        if (isArchive(line)) return SourceType.ARCHIVE_DOCUMENT;

        // Патенты
        if (isPatent(line)) return SourceType.PATENT;

        // ГОСТ из сборника (содержит //)
        if (isGost(line) && line.contains("//")) return SourceType.GOST_FROM_COLLECTION;

        // ГОСТ отдельный
        if (isGost(line)) return SourceType.GOST;

        // Правила
        if (isRules(line)) return SourceType.RULES;

        // Электронные ресурсы (до книг и статей, потому что могут содержать те же маркеры)
        if (isElectronicCourse(line)) return SourceType.ELECTRONIC_COURSE;
        if (isElectronicLocal(line)) return SourceType.ELECTRONIC_LOCAL;
        if (isElectronicWithDOI(line)) return SourceType.ELECTRONIC_ARTICLE_DOI;
        if (isElectronicBook(line)) return SourceType.ELECTRONIC_BOOK;
        if (isElectronicNewspaper(line)) return SourceType.ELECTRONIC_NEWSPAPER;
        if (isElectronicJournal(line)) return SourceType.ELECTRONIC_JOURNAL;
        if (isElectronicPortal(line)) return SourceType.ELECTRONIC_PORTAL;
        if (isWebsite(line)) return SourceType.ELECTRONIC_WEBSITE;

        // Законодательные материалы
        if (isLegal(line)) return SourceType.LEGAL;

        // Статистика
        if (isStatistical(line)) return SourceType.STATISTICAL_COLLECTION;

        // Иностранные источники (латиница в начале)
        if (isForeign(line)) return SourceType.FOREIGN;

        // Диссертации и авторефераты
        if (isAbstract(line)) return SourceType.ABSTRACT;
        if (isDissertation(line)) return SourceType.DISSERTATION;

        // Статьи (есть //)
        if (hasArticleMarkers(line)) {
            if (isEncyclopedia(line)) return SourceType.ARTICLE_ENCYCLOPEDIA;
            if (isChapter(line)) return SourceType.CHAPTER_BOOK;
            if (isCollection(line)) return SourceType.ARTICLE_COLLECTION;
            if (isNewspaper(line)) return SourceType.NEWSPAPER_ARTICLE;
            if (hasTitleFirst(line)) return SourceType.ARTICLE_JOURNAL_TITLE_FIRST;
            return SourceType.ARTICLE_JOURNAL_AUTHORS_FIRST;
        }

        // Книги
        if (isMultivolume(line)) return SourceType.MULTIVOLUME;
        if (isUnderEditor(line)) return SourceType.BOOK_UNDER_EDITOR;
        if (hasTitleFirst(line)) return SourceType.BOOK_TITLE_FIRST;
        if (hasAuthors(line)) return SourceType.BOOK_AUTHORS_FIRST;

        return SourceType.UNKNOWN;
    }

    // ==================== Маркеры ====================

    private boolean isArchive(String line) {
        return line.matches("^[А-ЯЁ]+\\..*Ф\\.\\s*\\d+.*Оп\\.\\s*\\d+.*Д\\.\\s*\\d+.*Л\\.\\s*\\d+");
    }

    private boolean isPatent(String line) {
        return line.startsWith("Патент");
    }

    private boolean isGost(String line) {
        return line.matches("^ГОСТ.*");
    }

    private boolean isRules(String line) {
        return line.contains("правила") || line.contains("ПОТ");
    }

    private boolean isElectronicCourse(String line) {
        return line.contains("Moodle") || line.contains("дистанционный");
    }

    private boolean isElectronicLocal(String line) {
        return line.contains("CD-ROM") || line.contains("электрон. опт. диск");
    }

    private boolean isElectronicWithDOI(String line) {
        return line.contains("DOI:") || line.contains("doi:");
    }

    private boolean isElectronicBook(String line) {
        return line.contains("ЭБС") || line.contains("e.lanbook") || line.contains("URL:") && (line.contains("учеб.")
                                                                                               || line.contains("монография")
                                                                                               || line.contains("изд-во")
                                                                                               || line.contains("Издательство"));
    }

    private boolean isElectronicNewspaper(String line) {
        return line.contains("URL:") && (line.contains("газета") || line.contains("Газета") || line.contains("газ."));
    }

    private boolean isElectronicJournal(String line) {
        return line.contains("URL:") && (line.contains("журнал") || line.contains("Журнал") || line.contains("электрон. журнал"));
    }

    private boolean isElectronicPortal(String line) {
        return line.contains("интернет-портал") || line.contains("портал");
    }

    private boolean isWebsite(String line) {
        return line.contains("URL:") && !line.contains("//") && !line.contains("статья");
    }

    private boolean isLegal(String line) {
        return line.contains("федер. закон")
               || line.contains("постановление")
               || line.contains("указ")
               || line.contains("приказ")
               || line.contains("распоряжение")
               || line.contains("кодекс");
    }

    private boolean isStatistical(String line) {
        return line.contains("стат. сб.") || line.contains("статистический");
    }

    private boolean isForeign(String line) {
        return line.matches("^[A-Z].*");
    }

    private boolean isAbstract(String line) {
        return line.contains("автореф.");
    }

    private boolean isDissertation(String line) {
        return line.contains("дис.");
    }

    private boolean isEncyclopedia(String line) {
        return line.contains("энциклопедия");
    }

    private boolean isChapter(String line) {
        return line.contains("учебник") && !line.contains("//");
    }

    private boolean isCollection(String line) {
        return line.contains("конф.") || line.contains("материалы");
    }

    private boolean isNewspaper(String line) {
        return line.contains("газета") || line.contains("газ.") || line.matches(".*\\d{1,2}\\s+(января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря).*");
    }

    private boolean isMultivolume(String line) {
        return line.contains("в 2 т.") || line.contains("в 3 т.") || line.contains("в 4 т.")
               || line.contains("в 5 т.") || line.contains("в 12 т.") || line.contains("Т.");
    }

    private boolean isUnderEditor(String line) {
        return line.contains("под ред.") || line.contains("сост.") || line.contains("редкол.");
    }

    private boolean hasArticleMarkers(String line) {
        return line.contains("//") && !line.startsWith("ГОСТ");
    }

    private boolean hasTitleFirst(String line) {
        return line.startsWith("Исследования по") || line.startsWith("Экономическая оценка")
               || !line.matches("^[А-ЯЁ][а-яё]+\\s+[а-яё]+.*//.*") && line.contains("/");
    }

    private boolean hasAuthors(String line) {
        return line.matches("^[А-ЯЁа-яё][а-яё]+\\s+[А-ЯЁ]\\.?\\s?[А-ЯЁ]\\..*");
    }
}