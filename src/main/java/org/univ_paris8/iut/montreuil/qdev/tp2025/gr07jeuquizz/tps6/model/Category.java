package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category", uniqueConstraints = @UniqueConstraint(columnNames = "label"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String label;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Annonce> annonces = new ArrayList<>();

    public Category(String label) {
        this.label = label;
    }
}
