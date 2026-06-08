package ru.tsvetikov.editwizard.bibliography.engine.highlight;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.core.dto.ValidationError;

import java.util.ArrayList;
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

        // Объединяем пересекающиеся ошибки
        List<ValidationError> merged = mergeOverlapping(errors, rawLine);

        // Сортируем от конца к началу
        List<ValidationError> sorted = merged.stream()
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

    private List<ValidationError> mergeOverlapping(List<ValidationError> errors, String rawLine) {
        if (errors.size() <= 1) return new ArrayList<>(errors);

        List<ValidationError> sorted = errors.stream()
                .sorted(Comparator.comparingInt(ValidationError::charStart))
                .toList();

        List<ValidationError> merged = new ArrayList<>();
        ValidationError current = sorted.getFirst();

        for (int i = 1; i < sorted.size(); i++) {
            ValidationError next = sorted.get(i);
            if (next.charStart() <= current.charEnd()) {
                int newStart = Math.min(current.charStart(), next.charStart());
                int newEnd = Math.max(current.charEnd(), next.charEnd());
                String combinedMessage = current.message();
                if (!current.message().equals(next.message())) {
                    combinedMessage += "; " + next.message();
                }
                String combinedExpected = current.expectedView() != null ? current.expectedView() : next.expectedView();
                current = new ValidationError(
                        current.ruleCode(),
                        combinedMessage,
                        combinedExpected,
                        newStart,
                        newEnd,
                        rawLine.substring(newStart, newEnd)
                );
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

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
