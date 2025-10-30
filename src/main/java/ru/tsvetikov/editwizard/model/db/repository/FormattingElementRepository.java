package ru.tsvetikov.editwizard.model.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsvetikov.editwizard.model.db.entity.FormattingElement;

public interface FormattingElementRepository extends JpaRepository<FormattingElement, Long> {
}
