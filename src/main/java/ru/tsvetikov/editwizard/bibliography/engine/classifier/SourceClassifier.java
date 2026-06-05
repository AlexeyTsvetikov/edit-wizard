package ru.tsvetikov.editwizard.bibliography.engine.classifier;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;

import java.util.List;

@Component
public class SourceClassifier {

    private static final List<String> MONTHS = List.of(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря",
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    );

    public SourceType classify(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            return SourceType.UNKNOWN;
        }

        String line = rawLine.trim();

        if (isArchive(line)) return SourceType.ARCHIVE_DOCUMENT;

        if (isPatent(line)) return SourceType.PATENT;

        if (isGost(line) && line.contains("//")) return SourceType.GOST_FROM_COLLECTION;

        if (isGost(line)) return SourceType.GOST;

        if (isRules(line)) return SourceType.RULES;
        if (isLegal(line)) return SourceType.LEGAL;

        if (isElectronicLocal(line)) return SourceType.ELECTRONIC_LOCAL;
        if (isElectronicCourse(line)) return SourceType.ELECTRONIC_COURSE;
        if (isElectronicWithDOI(line)) return SourceType.ELECTRONIC_ARTICLE_DOI;
        if (isElectronicNewspaper(line)) return SourceType.ELECTRONIC_NEWSPAPER;
        if (isElectronicJournal(line)) return SourceType.ELECTRONIC_JOURNAL;
        if (isElectronicPortal(line)) return SourceType.ELECTRONIC_PORTAL;
        if (isElectronicBook(line)) return SourceType.ELECTRONIC_BOOK;
        if (isWebsite(line)) return SourceType.ELECTRONIC_WEBSITE;

        if (isStatistical(line)) return SourceType.STATISTICAL_COLLECTION;

        if (isForeign(line)) return SourceType.FOREIGN;

        if (isAbstract(line)) return SourceType.ABSTRACT;
        if (isDissertation(line)) return SourceType.DISSERTATION;
        if (isUnderEditor(line)) return SourceType.BOOK_UNDER_EDITOR;


        if (hasArticleMarkers(line)) {
            if (isEncyclopedia(line)) return SourceType.ARTICLE_ENCYCLOPEDIA;
            if (isChapter(line)) return SourceType.CHAPTER_BOOK;
            if (isCollection(line)) return SourceType.ARTICLE_COLLECTION;
            if (isNewspaper(line)) return SourceType.NEWSPAPER_ARTICLE;
            if (hasTitleFirst(line)) return SourceType.ARTICLE_JOURNAL_TITLE_FIRST;
            return SourceType.ARTICLE_JOURNAL_AUTHORS_FIRST;
        }

        if (hasTitleFirst(line)) return SourceType.BOOK_TITLE_FIRST;
        if (isMultivolume(line)) return SourceType.MULTIVOLUME;

        if (hasAuthors(line)) return SourceType.BOOK_AUTHORS_FIRST;

        return SourceType.UNKNOWN;
    }

    private boolean isArchive(String line) {
        return line.startsWith("ГАРФ")
               || (line.contains("Ф.") && line.contains("Оп.") && line.contains("Д.") && line.contains("Л."));
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
        return line.contains("URL:") && isNewspaper(line);
    }

    private boolean isElectronicJournal(String line) {
        return line.contains("URL:") && (line.contains("журнал") || line.contains("Журнал") || line.contains("электрон. журнал"));
    }

    private boolean isElectronicPortal(String line) {
        return line.contains("интернет-портал") || line.contains("портал");
    }

    private boolean isWebsite(String line) {
        return line.contains("URL:")
               && !line.contains(" // ")
               && !line.contains("статья")
               && !line.contains("газета");
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
        return line.contains("учебник")
               || line.contains("учеб. пособие")
               || line.contains("монография")
               || line.contains("сборник");
    }

    private boolean isCollection(String line) {
        return line.contains("конф.")
               || line.contains("материалы")
               || line.contains("сборник")
               || line.contains("межвуз");
    }

    private boolean isNewspaper(String line) {
        if (line.contains("газета") || line.contains("газ.") || line.contains("Газета")) return true;
        for (String month : MONTHS) {
            if (line.contains(month)) return true;
        }
        return false;
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
        return !line.matches("^[А-ЯЁ][а-яё]+\\s+[А-ЯЁ]\\..*")
               && line.contains(" / ");
    }

    private boolean hasAuthors(String line) {
        return line.matches("^[А-ЯЁа-яё][а-яё]+\\s+[А-ЯЁ]\\.?\\s?[А-ЯЁ]\\..*");
    }
}