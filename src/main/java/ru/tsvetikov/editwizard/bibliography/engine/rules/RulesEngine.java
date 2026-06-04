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
        Pattern pattern = Pattern.compile(rule.searchPattern());
        Matcher matcher = pattern.matcher(rawLine);

        while (matcher.find()) {
            errors.add(new ValidationError(
                    rule.code(),
                    rule.message(),
                    rule.expectedView(),
                    matcher.start(),
                    matcher.end(),
                    rawLine.substring(matcher.start(), matcher.end())
            ));
        }

        return errors;
    }
}