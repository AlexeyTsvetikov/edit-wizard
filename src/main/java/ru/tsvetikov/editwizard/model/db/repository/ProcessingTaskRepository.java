package ru.tsvetikov.editwizard.model.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsvetikov.editwizard.model.db.entity.ProcessingTask;

public interface ProcessingTaskRepository extends JpaRepository<ProcessingTask, Long> {
}
