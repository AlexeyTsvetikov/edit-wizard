package ru.tsvetikov.editwizard.bibliography.engine.model;

import lombok.Getter;

@Getter
public enum SourceType {
    BOOK_AUTHORS_FIRST("Книга (1–3 автора)"),
    BOOK_TITLE_FIRST("Книга (4+ авторов)"),
    BOOK_UNDER_EDITOR("Книга под редакцией"),
    MULTIVOLUME("Многотомное издание"),
    ARTICLE_JOURNAL_AUTHORS_FIRST("Статья из журнала (1–3 автора)"),
    ARTICLE_JOURNAL_TITLE_FIRST("Статья из журнала (4+ авторов)"),
    ARTICLE_COLLECTION("Статья из сборника"),
    ARTICLE_ENCYCLOPEDIA("Статья из энциклопедии"),
    CHAPTER_BOOK("Глава из книги"),
    NEWSPAPER_ARTICLE("Статья из газеты"),
    DISSERTATION("Диссертация"),
    ABSTRACT("Автореферат"),
    LEGAL("Законодательный документ"),
    GOST("ГОСТ"),
    GOST_FROM_COLLECTION("ГОСТ из сборника"),
    PATENT("Патент"),
    RULES("Правила"),
    STATISTICAL_COLLECTION("Статистический сборник"),
    ARCHIVE_DOCUMENT("Архивный документ"),
    ELECTRONIC_LOCAL("Электронный ресурс (CD-ROM)"),
    ELECTRONIC_WEBSITE("Сайт"),
    ELECTRONIC_NEWSPAPER("Электронная газета"),
    ELECTRONIC_JOURNAL("Электронный журнал"),
    ELECTRONIC_PORTAL("Интернет-портал"),
    ELECTRONIC_BOOK("Книга из ЭБС"),
    ELECTRONIC_COURSE("Дистанционный курс"),
    ELECTRONIC_ARTICLE_DOI("Статья с DOI"),
    FOREIGN("Иностранный источник"),
    UNKNOWN("Не определён");

    private final String displayName;

    SourceType(String displayName) {
        this.displayName = displayName;
    }
}