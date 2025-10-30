package ru.tsvetikov.editwizard.model.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsvetikov.editwizard.model.db.entity.FileType;

public interface FileTypeRepository  extends JpaRepository<FileType, Long> {
}
