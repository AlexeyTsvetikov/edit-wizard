package ru.tsvetikov.editwizard.bibliography.engine.parser;

import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;

public interface BibliographyParser {

    boolean canParse(SourceType type);

    ParsedRecord parse(String rawLine);
}