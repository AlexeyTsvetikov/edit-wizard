package ru.tsvetikov.editwizard.bibliography.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tsvetikov.editwizard.bibliography.engine.BibliographyEngine;
import ru.tsvetikov.editwizard.bibliography.engine.model.Rule;
import ru.tsvetikov.editwizard.bibliography.model.BibliographyRule;
import ru.tsvetikov.editwizard.bibliography.repository.BibliographyRuleRepository;
import ru.tsvetikov.editwizard.core.dto.ValidationPage;
import ru.tsvetikov.editwizard.core.dto.ValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BibliographyService {

    private final BibliographyEngine engine;
    private final BibliographyRuleRepository ruleRepository;


    public ValidationPage validate(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return emptyPage();
        }

        String[] lines = rawText.split("\\n");
        List<String> nonEmptyLines = Arrays.stream(lines)
                .filter(line -> !line.isBlank())
                .toList();

        List<BibliographyRule> allRules = ruleRepository.findAll();
        List<Rule> domainRules = allRules.stream()
                .map(this::toDomainRule)
                .toList();

        List<ValidationResult> results = new ArrayList<>();
        int errorCount = 0;
        int unknownCount = 0;

        for (int i = 0; i < nonEmptyLines.size(); i++) {
            String line = nonEmptyLines.get(i);
            ValidationResult result = engine.process(line, i + 1, domainRules);
            results.add(result);
            if (result.hasErrors()) errorCount++;
            if (!result.typeDetected()) unknownCount++;
        }

        return new ValidationPage(
                rawText,
                results,
                nonEmptyLines.size(),
                errorCount,
                unknownCount,
                results.stream().mapToInt(r -> r.errors().size()).sum()
        );
    }

    private Rule toDomainRule(BibliographyRule entity) {
        return new Rule(
                entity.getCode(),
                entity.getTargetToken(),
                entity.getSearchPattern(),
                entity.getName(),           // message — пока из name, потом можно отдельное поле
                entity.getExpectedView()
        );
    }

    private ValidationPage emptyPage() {
        return new ValidationPage("", List.of(), 0, 0, 0, 0);
    }
}