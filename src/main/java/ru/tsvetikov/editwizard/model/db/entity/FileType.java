package ru.tsvetikov.editwizard.model.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "file_types")
public class FileType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "fileType", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "file-filetype")
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "fileType", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "rule-filetype")
    private List<FormattingRule> formattingRules = new ArrayList<>();
}
