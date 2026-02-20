package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category",
        uniqueConstraints = @UniqueConstraint(columnNames = "label"))
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String label;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Annonce> annonces = new ArrayList<>();
    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Long getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Long.valueOf(id);
    }

    public List<Annonce> getAnnonces() {
        return annonces;
    }
}
