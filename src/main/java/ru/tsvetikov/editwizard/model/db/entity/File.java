package ru.tsvetikov.editwizard.model.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.tsvetikov.editwizard.model.enums.FileStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-files")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_type_id")
    @JsonBackReference(value = "file-filetype")
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gost_standard_id")
    @JsonBackReference(value = "file-gost")
    private GostStandard gostStandard;

    @Column(name = "original_filename", nullable = false, length = 500)
    private String originalFilename;

    @Column(name = "input_file_key", length = 500)
    private String inputFileKey;

    @Column(name = "output_file_key", length = 500)
    private String outputFileKey;

    @Column(name = "current_status", length = 50)
    @Enumerated(EnumType.STRING)
    private FileStatus currentStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "file", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "task-file")
    private List<ProcessingTask> processingTasks = new ArrayList<>();

}
