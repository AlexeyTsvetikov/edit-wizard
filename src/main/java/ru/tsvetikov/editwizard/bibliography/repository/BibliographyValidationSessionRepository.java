package ru.tsvetikov.editwizard.bibliography.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tsvetikov.editwizard.bibliography.model.BibliographyValidationSession;

@Repository
public interface BibliographyValidationSessionRepository extends JpaRepository<BibliographyValidationSession, Long> {
}
