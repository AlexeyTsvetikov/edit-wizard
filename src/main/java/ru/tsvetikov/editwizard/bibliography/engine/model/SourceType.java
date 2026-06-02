package ru.tsvetikov.editwizard.bibliography.engine.model;

public enum SourceType {

    // Книжные издания
    BOOK_AUTHORS_FIRST,
    BOOK_TITLE_FIRST,
    BOOK_UNDER_EDITOR,
    MULTIVOLUME,

    // Статьи
    ARTICLE_JOURNAL_AUTHORS_FIRST,
    ARTICLE_JOURNAL_TITLE_FIRST,
    ARTICLE_COLLECTION,
    ARTICLE_ENCYCLOPEDIA,
    CHAPTER_BOOK,

    // Газеты
    NEWSPAPER_ARTICLE,

    // Диссертации
    DISSERTATION,
    ABSTRACT,

    // Законодательные
    LEGAL,

    // Нормативно-технические
    GOST,
    GOST_FROM_COLLECTION,
    PATENT,
    RULES,

    // Статистика и архивы
    STATISTICAL_COLLECTION,
    ARCHIVE_DOCUMENT,

    // Электронные
    ELECTRONIC_LOCAL,
    ELECTRONIC_WEBSITE,
    ELECTRONIC_NEWSPAPER,
    ELECTRONIC_JOURNAL,
    ELECTRONIC_PORTAL,
    ELECTRONIC_BOOK,
    ELECTRONIC_COURSE,
    ELECTRONIC_ARTICLE_DOI,

    // Иностранные
    FOREIGN,

    UNKNOWN
}