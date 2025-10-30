package ru.tsvetikov.editwizard.model.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.tsvetikov.editwizard.model.enums.ProcessingTaskStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "processing_tasks")
public class ProcessingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "file_id")
    @JsonBackReference(value = "task-file")
    private File file;

    @Column(name = "attempt_number")
    private Integer attemptNumber = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProcessingTaskStatus status = ProcessingTaskStatus.PENDING;

    @Column(name = "worker_id")
    private String workerId;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}