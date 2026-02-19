package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * DTO de mise à jour partielle d'une annonce (requête PATCH).
 * Tous les champs sont optionnels (null = pas de modification).
 *
 * Description du résultat attendu pour PATCH /api/annonces/{id} :
 * - Seuls les champs non-null du body sont modifiés
 * - Retourne 200 avec l'annonce mise à jour
 * - Retourne 400 si les données envoyées échouent la validation
 * - Retourne 403 si l'utilisateur n'est pas l'auteur
 * - Retourne 409 si l'annonce est PUBLISHED (non modifiable)
 * - Retourne 404 si l'annonce n'existe pas
 */
public class AnnoncePatchDTO {

    @Size(min = 3, max = 64, message = "Le titre doit contenir entre 3 et 64 caractères")
    private String title;

    @Size(min = 10, max = 256, message = "La description doit contenir entre 10 et 256 caractères")
    private String description;

    @Size(max = 64, message = "L'adresse ne peut pas dépasser 64 caractères")
    private String adress;

    @Email(message = "L'email doit être valide")
    private String mail;

    @Positive(message = "L'identifiant de catégorie doit être positif")
    private Long categoryId;

    public AnnoncePatchDTO() {
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
