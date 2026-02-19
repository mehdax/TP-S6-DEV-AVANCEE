package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.AnnonceCreateDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;

/**
 * Mapper statique pour la conversion Annonce ↔ DTO.
 * Centralise toute la logique de transformation pour éviter la duplication.
 */
public class AnnonceMapper {

    private AnnonceMapper() {
    }

    /**
     * Convertit une entité Annonce en AnnonceDTO (lecture API).
     * Utilise le Pattern Builder de AnnonceDTO.
     */
    public static AnnonceDTO toDTO(Annonce annonce) {
        if (annonce == null)
            return null;

        AnnonceDTO.Builder builder = AnnonceDTO.builder()
                .id((long) annonce.getId())
                .title(annonce.getTitle())
                .description(annonce.getDescription())
                .adress(annonce.getAdress())
                .mail(annonce.getMail())
                .date(annonce.getDate())
                .status(annonce.getStatus() != null ? annonce.getStatus().name() : null);

        // Relations optionnelles (peuvent ne pas être chargées)
        if (annonce.getAuthor() != null) {
            builder.authorId(annonce.getAuthor().getId())
                    .authorUsername(annonce.getAuthor().getUsername());
        }

        if (annonce.getCategory() != null) {
            builder.categoryId(annonce.getCategory().getId())
                    .categoryLabel(annonce.getCategory().getLabel());
        }

        return builder.build();
    }

    /**
     * Convertit un DTO de création en entité Annonce.
     * L'auteur et la catégorie doivent être fournis séparément (récupérés depuis la
     * BDD).
     */
    public static Annonce toEntity(AnnonceCreateDTO dto, User author, Category category) {
        if (dto == null)
            return null;

        Annonce annonce = new Annonce();
        annonce.setTitle(dto.getTitle());
        annonce.setDescription(dto.getDescription());
        annonce.setAdress(dto.getAdress());
        annonce.setMail(dto.getMail());
        annonce.setAuthor(author);
        annonce.setCategory(category);

        return annonce;
    }
}
