package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "Annonce")
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
    private Date date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    /** Gestion de la concurrence optimiste (Exercice 7) */
    @Version
    @Column(name = "version")
    private Long version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Annonce(int id, String title, String description, String adress, String mail, Timestamp date) {
    }

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
    }

    public Annonce() {

    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", adress='" + adress + '\'' +
                ", mail='" + mail + '\'' +
                ", date=" + date +
                '}';
    }

    public void setStatus(AnnonceStatus status) {
        this.status = status;
    }

    public AnnonceStatus getStatus() {
        return status;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getAuthor() {
        return author;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
