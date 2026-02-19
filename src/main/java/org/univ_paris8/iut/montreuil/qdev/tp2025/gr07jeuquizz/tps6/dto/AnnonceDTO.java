package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * DTO de lecture d'une annonce (réponse API).
 * Pattern Builder pour faciliter la construction depuis une entité.
 */
public class AnnonceDTO {

    private Long id;
    private String title;
    private String description;
    private String adress;
    private String mail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    private String status;
    private Long authorId;
    private String authorUsername;
    private Long categoryId;
    private String categoryLabel;
    private Long version;

    // Constructeur privé – utiliser le Builder
    private AnnonceDTO() {
    }

    // ========================
    // Builder
    // ========================
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AnnonceDTO dto = new AnnonceDTO();

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder title(String title) {
            dto.title = title;
            return this;
        }

        public Builder description(String description) {
            dto.description = description;
            return this;
        }

        public Builder adress(String adress) {
            dto.adress = adress;
            return this;
        }

        public Builder mail(String mail) {
            dto.mail = mail;
            return this;
        }

        public Builder date(Date date) {
            dto.date = date;
            return this;
        }

        public Builder status(String status) {
            dto.status = status;
            return this;
        }

        public Builder authorId(Long authorId) {
            dto.authorId = authorId;
            return this;
        }

        public Builder authorUsername(String authorUsername) {
            dto.authorUsername = authorUsername;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            dto.categoryId = categoryId;
            return this;
        }

        public Builder categoryLabel(String categoryLabel) {
            dto.categoryLabel = categoryLabel;
            return this;
        }

        public Builder version(Long version) {
            dto.version = version;
            return this;
        }

        public AnnonceDTO build() {
            return dto;
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAdress() {
        return adress;
    }

    public String getMail() {
        return mail;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public Long getVersion() {
        return version;
    }
}
