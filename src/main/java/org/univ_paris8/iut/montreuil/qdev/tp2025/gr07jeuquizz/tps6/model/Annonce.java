package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "Annonce")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 64)
    @Column(nullable = false, length = 64)
    private String title;

    @NotBlank
    @Size(min = 10, max = 256)
    @Column(nullable = false, length = 256)
    private String description;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String adress;

    @NotBlank
    @Email
    @Column(nullable = false, length = 64)
    private String mail;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private Date date = new Date();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private User author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = new Date();
        }
        if (this.status == null) {
            this.status = AnnonceStatus.DRAFT;
        }
    }

    public void publish() {
        if (this.status == AnnonceStatus.DRAFT) {
            this.status = AnnonceStatus.PUBLISHED;
        } else {
            throw new IllegalStateException("Seules les annonces en brouillon peuvent être publiées");
        }
    }

    public void archive() {
        if (this.status == AnnonceStatus.PUBLISHED) {
            this.status = AnnonceStatus.ARCHIVED;
        } else {
            throw new IllegalStateException("Seules les annonces publiées peuvent être archivées");
        }
    }

    public boolean isPublished() {
        return this.status == AnnonceStatus.PUBLISHED;
    }

    public Annonce(String title, String description, String adress, String mail) {
        this.title = title;
        this.description = description;
        this.adress = adress;
        this.mail = mail;
        this.date = new Date();
        this.status = AnnonceStatus.DRAFT;
    }
}
