package ru.tsvetikov.editwizard.bibliography.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bibliography_rules")
public class BibliographyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "source_types", length = 200)
    private String sourceTypes;

    @Column(name = "example_text", nullable = false, columnDefinition = "TEXT")
    private String exampleText;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_token", length = 50)
    private String targetToken;

    @Column(name = "search_pattern", length = 500)
    private String searchPattern;

    @Column(name = "expected_view", columnDefinition = "TEXT")
    private String expectedView;
}