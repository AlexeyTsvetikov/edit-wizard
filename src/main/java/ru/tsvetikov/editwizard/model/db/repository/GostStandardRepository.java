package ru.tsvetikov.editwizard.model.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsvetikov.editwizard.model.db.entity.GostStandard;

public interface GostStandardRepository extends JpaRepository<GostStandard, Long> {
}
