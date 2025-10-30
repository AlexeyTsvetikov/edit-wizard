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
@Table(name = "gost_standards")
public class GostStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 100)
    private String code;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "gostStandard", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "file-gost")
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "gostStandard", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "rule-gost")
    private List<FormattingRule> formattingRules = new ArrayList<>();

}
