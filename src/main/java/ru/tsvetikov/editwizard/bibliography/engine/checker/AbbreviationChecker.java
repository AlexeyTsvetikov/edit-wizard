package ru.tsvetikov.editwizard.bibliography.engine.checker;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.core.dto.ValidationError;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AbbreviationChecker {

    private static final Map<String, String> ABBREVIATIONS = new LinkedHashMap<>();

    static {
        ABBREVIATIONS.put("под редакцией", "под ред.");
        ABBREVIATIONS.put("составитель", "сост.");
        ABBREVIATIONS.put("перевод с", "пер. с");
        ABBREVIATIONS.put("выпуск", "вып.");
        ABBREVIATIONS.put("избранные сочинения", "избр. соч.");
        ABBREVIATIONS.put("межвузовский сборник научных трудов", "межвуз. сб. науч. тр.");
        ABBREVIATIONS.put("полное собрание сочинений", "полн. собр. соч.");
        ABBREVIATIONS.put("сборник научных трудов", "сб. науч. тр.");
        ABBREVIATIONS.put("сборник трудов", "сб. тр.");
        ABBREVIATIONS.put("собрание сочинений", "собр. соч.");
        ABBREVIATIONS.put("страница", "с.");
        ABBREVIATIONS.put("том", "т.");
        ABBREVIATIONS.put("миллион", "млн");
        ABBREVIATIONS.put("миллиард", "млрд");
        ABBREVIATIONS.put("тысяча", "тыс.");
        ABBREVIATIONS.put("рублей", "руб.");
        ABBREVIATIONS.put("долларов", "долл.");
    }

    public List<ValidationError> check(ParsedRecord record) {
        List<ValidationError> errors = new ArrayList<>();
        for (String tokenCode : List.of("TITLE", "EDITOR", "RESPONSIBILITY", "ISSUE", "VOLUME"))  {
            ParsedToken token = record.getToken(tokenCode);
            if (token != null) {
                errors.addAll(checkToken(token));
            }
        }
        return errors;
    }

    private List<ValidationError> checkToken(ParsedToken token) {
        List<ValidationError> errors = new ArrayList<>();
        String text = token.value().toLowerCase();

        for (Map.Entry<String, String> entry : ABBREVIATIONS.entrySet()) {
            String fullForm = entry.getKey();
            String abbreviation = entry.getValue();

            int index = text.indexOf(fullForm);
            if (index >= 0) {
                int start = token.start() + index;
                int end = start + fullForm.length();
                errors.add(new ValidationError(
                        "FULL_FORM_INSTEAD_OF_ABBREVIATED",
                        "Используйте сокращение \"" + abbreviation + "\" вместо \"" + fullForm + "\"",
                        abbreviation,
                        start,
                        end,
                        token.value().substring(index, index + fullForm.length())
                ));
            }
        }
        return errors;
    }
}
