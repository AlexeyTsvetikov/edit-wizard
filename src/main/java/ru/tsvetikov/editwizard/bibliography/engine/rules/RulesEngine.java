package ru.tsvetikov.editwizard.bibliography.engine.rules;

import org.springframework.stereotype.Component;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedRecord;
import ru.tsvetikov.editwizard.bibliography.engine.model.ParsedToken;
import ru.tsvetikov.editwizard.bibliography.engine.model.Rule;
import ru.tsvetikov.editwizard.core.dto.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RulesEngine {

    public List<ValidationError> apply(ParsedRecord record, List<Rule> rules) {
        List<ValidationError> errors = new ArrayList<>();
        String sourceType = record.sourceType().name();

        for (Rule rule : rules) {
            if (rule.code().equals("CITY_NOT_ABBREVIATED")) {
                System.out.println(">>> CITY rule: targetToken=" + rule.targetToken() + " hasToken=" + (record.getToken("CITY") != null));
                if (record.getToken("CITY") != null) {
                    System.out.println(">>> CITY value: '" + record.getToken("CITY").value() + "'");
                }
            }
            if (rule.sourceTypes() != null && !rule.sourceTypes().isBlank()) {
                if (!List.of(rule.sourceTypes().split(",")).contains(sourceType)) {
                    continue;
                }
            }

            if (rule.targetToken() == null) {
                errors.addAll(applyToLine(rule, record.rawLine()));
            } else {
                ParsedToken token = record.getToken(rule.targetToken());
                if (token != null) {
                    errors.addAll(applyToToken(rule, token));
                }
                // Для правил авторов проверяем также RESPONSIBILITY
                if ("AUTHORS".equals(rule.targetToken())) {
                    ParsedToken respToken = record.getToken("RESPONSIBILITY");
                    if (respToken != null) {
                        errors.addAll(applyToToken(rule, respToken));
                    }
                }
            }
        }
        return errors;
    }

    private List<ValidationError> applyToToken(Rule rule, ParsedToken token) {
        List<ValidationError> errors = new ArrayList<>();
        Pattern pattern = Pattern.compile(rule.searchPattern());
        Matcher matcher = pattern.matcher(token.value());

        while (matcher.find()) {
            int globalStart = token.start() + matcher.start();
            int globalEnd = token.start() + matcher.end();

            errors.add(new ValidationError(
                    rule.code(),
                    rule.message(),
                    rule.expectedView(),
                    globalStart,
                    globalEnd,
                    token.value().substring(matcher.start(), matcher.end())
            ));
        }

        return errors;
    }

    private List<ValidationError> applyToLine(Rule rule, String rawLine) {
        List<ValidationError> errors = new ArrayList<>();

        // Для правила SINGLE_SLASH исключаем URL из проверки
        String checkLine = rawLine;
        if (rule.code().equals("SINGLE_SLASH_INSTEAD_OF_DOUBLE")) {
            int urlIndex = checkLine.indexOf("URL:");
            if (urlIndex >= 0) {
                checkLine = checkLine.substring(0, urlIndex);
            }
            checkLine = checkLine.replaceAll("(?<=[А-ЯЁ][а-яё]?)/", ".");
        }

        Pattern pattern = Pattern.compile(rule.searchPattern());
        Matcher matcher = pattern.matcher(checkLine);

        while (matcher.find()) {
            errors.add(new ValidationError(
                    rule.code(),
                    rule.message(),
                    rule.expectedView(),
                    matcher.start(),
                    matcher.end(),
                    checkLine.substring(matcher.start(), matcher.end())
            ));
        }
        return errors;
    }
}