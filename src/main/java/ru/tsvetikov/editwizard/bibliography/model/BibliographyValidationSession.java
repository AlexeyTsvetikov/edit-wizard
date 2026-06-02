package ru.tsvetikov.editwizard.bibliography.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tsvetikov.editwizard.security.model.SecurityUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bibliography_validation_sessions")
public class BibliographyValidationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SecurityUser user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "total_lines", nullable = false)
    private Integer totalLines = 0;

    @Column(name = "error_count", nullable = false)
    private Integer errorCount = 0;

    @Column(name = "unknown_count", nullable = false)
    private Integer unknownCount = 0;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BibliographyValidationLine> lines = new ArrayList<>();

    public void addLine(BibliographyValidationLine line) {
        this.lines.add(line);
        line.setSession(this);
    }
}
