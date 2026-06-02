package ru.tsvetikov.editwizard.bibliography.engine;

import org.junit.jupiter.api.Test;
import ru.tsvetikov.editwizard.bibliography.engine.classifier.SourceClassifier;
import ru.tsvetikov.editwizard.bibliography.engine.highlight.HtmlHighlighter;
import ru.tsvetikov.editwizard.bibliography.engine.model.Rule;
import ru.tsvetikov.editwizard.bibliography.engine.model.SourceType;
import ru.tsvetikov.editwizard.bibliography.engine.parser.BookAuthorsFirstParser;
import ru.tsvetikov.editwizard.bibliography.engine.rules.RulesEngine;
import ru.tsvetikov.editwizard.core.dto.ValidationResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BibliographyEngineTest {

    private final SourceClassifier classifier = new SourceClassifier();
    private final BookAuthorsFirstParser bookParser = new BookAuthorsFirstParser();
    private final RulesEngine rulesEngine = new RulesEngine();
    private final HtmlHighlighter highlighter = new HtmlHighlighter();

    private final BibliographyEngine engine = new BibliographyEngine(
            classifier,
            List.of(bookParser),
            rulesEngine,
            highlighter
    );

    @Test
    void shouldDetectBookSingleAuthorWithNoErrors() {
        String rawLine = "Платонова С. И. Введение в философию: учеб. пособие. — М.: РИОР: Инфра-М, 2018. — 207 с.";

        // Правила: проверка инициалов без пробела
        Rule rule = new Rule(
                "AUTHOR_INITIALS_SPACING",
                "AUTHORS",
                "[А-ЯЁ]\\.[А-ЯЁ]\\.",   // "И.И." без пробела
                "Инициалы должны быть разделены пробелом",
                "И. И."
        );

        ValidationResult result = engine.process(rawLine, 1, List.of(rule));

        assertEquals(SourceType.BOOK_AUTHORS_FIRST.name(), result.sourceType());
        assertTrue(result.typeDetected());
        assertTrue(result.errors().isEmpty()); // "С. И." — с пробелом, ошибки нет
        System.out.println("HTML: " + result.highlightedHtml());
    }

    @Test
    void shouldDetectErrorInInitialsWithoutSpace() {
        // Намеренная ошибка: "С.И." вместо "С. И."
        String rawLine = "Платонова С.И. Введение в философию: учеб. пособие. — М.: РИОР: Инфра-М, 2018. — 207 с.";

        Rule rule = new Rule(
                "AUTHOR_INITIALS_SPACING",
                "AUTHORS",
                "[А-ЯЁ]\\.[А-ЯЁ]\\.",
                "Инициалы должны быть разделены пробелом",
                "И. И."
        );

        ValidationResult result = engine.process(rawLine, 1, List.of(rule));

        assertFalse(result.errors().isEmpty());
        assertEquals(1, result.errors().size());
        assertEquals("С.И.", result.errors().getFirst().fragment());
        assertTrue(result.highlightedHtml().contains("error-highlight"));
        System.out.println("HTML: " + result.highlightedHtml());
    }

    @Test
    void shouldReturnUnknownForGarbage() {
        String rawLine = "какая-то непонятная строка без признаков источника";

        ValidationResult result = engine.process(rawLine, 1, List.of());

        assertEquals("UNKNOWN", result.sourceType());
        assertFalse(result.typeDetected());
        assertTrue(result.highlightedHtml().contains("unknown-type"));
        System.out.println("HTML: " + result.highlightedHtml());
    }
}