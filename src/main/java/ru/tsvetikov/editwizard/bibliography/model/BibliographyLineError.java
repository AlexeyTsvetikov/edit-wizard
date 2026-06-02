package ru.tsvetikov.editwizard.bibliography.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bibliography_line_errors")
public class BibliographyLineError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private BibliographyValidationLine line;

    @Column(name = "rule_code", length = 50)
    private String ruleCode;

    @Column(name = "error_index", nullable = false)
    private Integer errorIndex;

    @Column(name = "error_length", nullable = false)
    private Integer errorLength;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "expected_view", columnDefinition = "TEXT")
    private String expectedView;
}
