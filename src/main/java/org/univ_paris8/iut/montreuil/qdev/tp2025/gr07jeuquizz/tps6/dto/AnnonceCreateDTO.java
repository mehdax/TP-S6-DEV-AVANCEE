package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto;

import javax.validation.constraints.*;

/**
 * DTO de création d'une annonce (requête POST).
 * Toutes les annotations Bean Validation sont présentes pour une validation
 * automatique.
 */
public class AnnonceCreateDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 64, message = "Le titre doit contenir entre 3 et 64 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 256, message = "La description doit contenir entre 10 et 256 caractères")
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 64, message = "L'adresse ne peut pas dépasser 64 caractères")
    private String adress;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String mail;

    @NotNull(message = "La catégorie est obligatoire")
    @Positive(message = "L'identifiant de catégorie doit être positif")
    private Long categoryId;

    // Constructeurs
    public AnnonceCreateDTO() {
    }

    // Getters & Setters
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
