package ru.tsvetikov.editwizard.bibliography.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.classifier.SourceClassifier;
import ru.tsvetikov.editwizard.bibliography.engine.highlight.HtmlHighlighter;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.Rule;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.parser.BibliographyParser;
import ru.tsvetikov.editwizard.bibliography.engine.rules.RulesEngine;
import ru.tsvetikov.editwizard.core.dto.ValidationResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BibliographyEngine {

    private final SourceClassifier classifier;
    private final List<BibliographyParser> parsers;
    private final RulesEngine rulesEngine;
    private final HtmlHighlighter highlighter;

    public ValidationResult process(String rawLine, int lineNumber, List<Rule> rules) {
        if (rawLine == null || rawLine.isBlank()) {
            return emptyResult(lineNumber, rawLine);
        }

        SourceType type = classifier.classify(rawLine);
        System.out.println(">>> Тип: " + type);
        if (type == SourceType.UNKNOWN) {
            return new ValidationResult(
                    lineNumber,
                    rawLine,
                    "UNKNOWN",
                    false,
                    List.of(),
                    highlighter.highlightUnknown(rawLine)
            );
        }

        BibliographyParser parser = findParser(type);
        if (parser == null) {
            return new ValidationResult(
                    lineNumber,
                    rawLine,
                    type.name(),
                    true,
                    List.of(),
                    highlighter.highlight(rawLine, List.of())
            );
        }

        ParsedRecord record = parser.parse(rawLine);

        var errors = rulesEngine.apply(record, rules);

        String html = errors.isEmpty()
                ? highlighter.highlight(rawLine, List.of())
                : highlighter.highlight(rawLine, errors);

        return new ValidationResult(
                lineNumber,
                rawLine,
                type.name(),
                true,
                errors,
                html
        );
    }

    private BibliographyParser findParser(SourceType type) {
        return parsers.stream()
                .filter(p -> p.canParse(type))
                .findFirst()
                .orElse(null);
    }

    private ValidationResult emptyResult(int lineNumber, String rawLine) {
        return new ValidationResult(
                lineNumber,
                rawLine != null ? rawLine : "",
                "EMPTY",
                false,
                List.of(),
                rawLine != null ? rawLine : ""
        );
    }
}