package ru.tsvetikov.editwizard.bibliography.engine.highlight;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.core.dto.ValidationError;
import java.util.Comparator;
import java.util.List;

@Component
public class HtmlHighlighter {

    private static final String ERROR_TEMPLATE = "<span class='error-highlight' title='%s'>%s</span>";
    private static final String UNKNOWN_TEMPLATE = "<span class='unknown-type'>%s</span>";

    public String highlight(String rawLine, List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return escapeHtml(rawLine);
        }

        // Сортируем от конца к началу, чтобы вставка не сбивала индексы
        List<ValidationError> sorted = errors.stream()
                .sorted(Comparator.comparingInt(ValidationError::charStart).reversed())
                .toList();

        StringBuilder sb = new StringBuilder(escapeHtml(rawLine));

        for (ValidationError error : sorted) {
            String fragment = escapeHtml(rawLine.substring(error.charStart(), error.charEnd()));
            String tooltip = error.message();
            if (error.expectedView() != null) {
                tooltip += " | Ожидалось: " + error.expectedView();
            }
            String replacement = ERROR_TEMPLATE.formatted(tooltip, fragment);
            sb.replace(error.charStart(), error.charEnd(), replacement);
        }

        return sb.toString();
    }

    /**
     * Подсветить строку с неопределённым типом — вся строка жёлтым.
     */
    public String highlightUnknown(String rawLine) {
        return UNKNOWN_TEMPLATE.formatted(escapeHtml(rawLine));
    }

    private String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
