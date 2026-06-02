package ru.tsvetikov.editwizard.bibliography.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsvetikov.editwizard.bibliography.model.BibliographyValidationLine;

@Repository
public interface BibliographyValidationLineRepository extends JpaRepository<BibliographyValidationLine, Long> {
}
