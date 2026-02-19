package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * DTO pour l'entité Annonce.
 * Utilise le Pattern Builder pour faciliter le mapping Entity ↔ DTO.
 *
 * Le DTO sert à :
 * - Découpler la couche REST de la couche modèle (entité JPA)
 * - Contrôler les données exposées via l'API
 * - Éviter les problèmes de sérialisation liés aux relations lazy (User,
 * Category)
 *
 * Bean Validation (Exercice 3) :
 * Les annotations de validation sont appliquées sur les champs du DTO.
 * Combinées avec @Valid dans le Resource, Jersey valide automatiquement
 * les données entrantes et déclenche une ConstraintViolationException
 * interceptée par le ConstraintViolationExceptionMapper.
 */
public class AnnonceDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 64, message = "Le titre doit contenir entre 3 et 64 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 256, message = "La description doit contenir entre 10 et 256 caractères")
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 64, message = "L'adresse ne doit pas dépasser 64 caractères")
    private String adress;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String mail;

    private Date date;
    private String status;

    @NotNull(message = "L'identifiant de l'auteur est obligatoire")
    private Long authorId;

    private String authorUsername;

    @NotNull(message = "L'identifiant de la catégorie est obligatoire")
    private Long categoryId;

    private String categoryLabel;

    // Constructeur vide requis pour la désérialisation JSON (Jackson)
    public AnnonceDTO() {
    }

    // Constructeur privé utilisé par le Builder
    private AnnonceDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.adress = builder.adress;
        this.mail = builder.mail;
        this.date = builder.date;
        this.status = builder.status;
        this.authorId = builder.authorId;
        this.authorUsername = builder.authorUsername;
        this.categoryId = builder.categoryId;
        this.categoryLabel = builder.categoryLabel;
    }

    // ===== Pattern Builder =====

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String adress;
        private String mail;
        private Date date;
        private String status;
        private Long authorId;
        private String authorUsername;
        private Long categoryId;
        private String categoryLabel;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder adress(String adress) {
            this.adress = adress;
            return this;
        }

        public Builder mail(String mail) {
            this.mail = mail;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder authorId(Long authorId) {
            this.authorId = authorId;
            return this;
        }

        public Builder authorUsername(String authorUsername) {
            this.authorUsername = authorUsername;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder categoryLabel(String categoryLabel) {
            this.categoryLabel = categoryLabel;
            return this;
        }

        public AnnonceDTO build() {
            return new AnnonceDTO(this);
        }
    }

    // ===== Getters & Setters =====

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }
}
