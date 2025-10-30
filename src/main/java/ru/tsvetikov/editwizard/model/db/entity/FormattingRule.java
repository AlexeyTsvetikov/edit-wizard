package ru.tsvetikov.editwizard.model.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "formatting_rules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"file_type_id", "gost_standard_id", "element_id"})
})
public class FormattingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_type_id")
    @JsonBackReference(value = "rule-filetype")
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gost_standard_id")
    @JsonBackReference(value = "rule-gost")
    private GostStandard gostStandard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id")
    @JsonBackReference(value = "rule-element")
    private FormattingElement formattingElement;

    @Column(name = "rules_json", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode rulesJson;
}