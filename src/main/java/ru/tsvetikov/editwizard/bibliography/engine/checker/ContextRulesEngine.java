package ru.tsvetikov.editwizard.bibliography.engine.checker;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.core.dto.ValidationError;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContextRulesEngine {
    private static final String DELIMITER_COLON_SPACE = ": ";
    private static final String DELIMITER_COMMA_SPACE = ", ";

    public List<ValidationError> validate(ParsedRecord record) {
        List<ValidationError> errors = new ArrayList<>();
        String line = record.rawLine();
        var tokens = record.tokens();
        String type = record.sourceType().name();

        // Проверка " // " перед журналом — только для статей
        if (type.startsWith("ARTICLE_") && tokens.containsKey("JOURNAL")) {
            int journalStart = tokens.get("JOURNAL").start();
            // Проверяем, что перед журналом есть " // "
            if (journalStart >= 4 && !line.startsWith(" // ", journalStart - 4)) {
                errors.add(new ValidationError("CONTEXT_DELIMITER",
                        "Название журнала должно отделяться \" // \" (пробел, две косые черты, пробел)",
                        " // ", journalStart - 4, journalStart,
                        line.substring(Math.max(0, journalStart - 4), journalStart)));
            }
        }
        // Проверка " : " после города
        if (tokens.containsKey("CITY") && tokens.containsKey("PUBLISHER")) {
            checkDelimiter(line, tokens.get("CITY").end(), tokens.get("PUBLISHER").start(),
                    DELIMITER_COLON_SPACE, "После города должно быть двоеточие и пробел", errors);
        }

        // Проверка " , " перед годом
        if (tokens.containsKey("PUBLISHER") && tokens.containsKey("YEAR") && !tokens.containsKey("CITY")) {
            checkDelimiter(line, tokens.get("PUBLISHER").end(), tokens.get("YEAR").start(),
                    DELIMITER_COMMA_SPACE, "Перед годом должна быть запятая и пробел", errors);
        }

        return errors;
    }

    private void checkDelimiter(String line, int end, int start, String expected,
                                String message, List<ValidationError> errors) {
        if (end < 0 || start < 0 || start <= end) return;
        String actual = line.substring(end, start);
        if (!actual.equals(expected)) {
            errors.add(new ValidationError(
                    "CONTEXT_DELIMITER",
                    message,
                    expected,
                    end,
                    start,
                    actual
            ));
        }
    }
}
