package ru.tsvetikov.editwizard.model.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsvetikov.editwizard.model.db.entity.FormattingRule;

public interface FormattingRuleRepository extends JpaRepository<FormattingRule, Long> {
}
