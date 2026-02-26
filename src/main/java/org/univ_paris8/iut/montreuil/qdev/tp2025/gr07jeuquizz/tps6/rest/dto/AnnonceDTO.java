package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
