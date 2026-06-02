package ru.tsvetikov.editwizard.bibliography.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bibliography_validation_lines")
public class BibliographyValidationLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private BibliographyValidationSession session;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "raw_text", nullable = false, columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @Column(name = "type_detected", nullable = false)
    private Boolean typeDetected;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BibliographyLineError> errors = new ArrayList<>();

    public void addError(BibliographyLineError error) {
        this.errors.add(error);
        error.setLine(this);
    }
}

