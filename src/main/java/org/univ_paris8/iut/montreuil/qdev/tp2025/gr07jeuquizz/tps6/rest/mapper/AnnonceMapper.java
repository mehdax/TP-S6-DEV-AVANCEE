package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.AnnonceDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper Entity ↔ DTO pour Annonce.
 * Utilise le Pattern Builder de AnnonceDTO.
 */
public class AnnonceMapper {

    /**
     * Convertit une entité Annonce en AnnonceDTO (Entity → DTO)
     */
    public static AnnonceDTO toDTO(Annonce annonce) {
        if (annonce == null)
            return null;

        AnnonceDTO.Builder builder = new AnnonceDTO.Builder()
                .id((long) annonce.getId())
                .title(annonce.getTitle())
                .description(annonce.getDescription())
                .adress(annonce.getAdress())
                .mail(annonce.getMail())
                .date(annonce.getDate())
                .status(annonce.getStatus() != null ? annonce.getStatus().name() : null);

        // Relations : on aplatit pour éviter les problèmes de lazy loading
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
     * Convertit une liste d'entités en liste de DTOs
     */
    public static List<AnnonceDTO> toDTOList(List<Annonce> annonces) {
        return annonces.stream()
                .map(AnnonceMapper::toDTO)
                .collect(Collectors.toList());
    }
}
