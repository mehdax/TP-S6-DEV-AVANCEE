package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security.Secured;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Ressource REST pour la gestion des annonces.
 *
 * Sécurité (Exercice 6) :
 * - Les endpoints de modification (POST, PUT, DELETE, PATCH, publish)
 * sont protégés par @Secured → nécessitent un token Bearer.
 * - Les endpoints de lecture (GET liste, GET détail) restent publics.
 *
 * Règles métier (Exercice 7) :
 * - Seul l'auteur peut modifier/supprimer son annonce (403)
 * - Une annonce PUBLISHED ne peut plus être modifiée (409)
 * - Archivage obligatoire avant suppression (409)
 */
@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private final AnnonceService annonceService = new AnnonceService();

    @Context
    private ContainerRequestContext requestContext;

    // ========== GET /api/annonces – Liste paginée ==========

    /**
     * Récupère la liste paginée des annonces.
     *
     * @param page     numéro de page (défaut 0)
     * @param pageSize taille de page (défaut 10)
     * @return 200 OK avec la liste paginée + métadonnées de pagination
     */
    @GET
    public Response getAllAnnonces(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int pageSize) {

        List<Annonce> annonces = annonceService.getAllPaginated(page, pageSize);
        List<AnnonceDTO> dtos = AnnonceMapper.toDTOList(annonces);
        long total = annonceService.countAll();

        // Réponse avec métadonnées de pagination
        Map<String, Object> response = new HashMap<>();
        response.put("content", dtos);
        response.put("page", page);
        response.put("size", pageSize);
        response.put("totalElements", total);
        response.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return Response.ok(response).build();
    }

    // ========== GET /api/annonces/{id} – Détail ==========

    /**
     * Récupère le détail d'une annonce par son ID.
     *
     * @param id identifiant de l'annonce
     * @return 200 OK avec le DTO, ou 404 Not Found
     */
    @GET
    @Path("/{id}")
    public Response getAnnonceById(@PathParam("id") Long id) {
        Optional<Annonce> annonceOpt = annonceService.getAnnonceById(id);

        if (!annonceOpt.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        AnnonceDTO dto = AnnonceMapper.toDTO(annonceOpt.get());
        return Response.ok(dto).build();
    }

    // ========== POST /api/annonces – Création ==========

    /**
     * Crée une nouvelle annonce.
     *
     * @param dto les données de l'annonce à créer
     * @return 201 Created avec le DTO créé + header Location, ou 400 Bad Request
     */
    @POST
    @Secured
    public Response createAnnonce(@Valid AnnonceDTO dto) {
        // La validation est automatiquement gérée par Bean Validation (@Valid)
        // En cas d'erreur, ConstraintViolationExceptionMapper retourne une 400

        try {
            Annonce created = annonceService.createAnnonce(
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getAdress(),
                    dto.getMail(),
                    dto.getAuthorId(),
                    dto.getCategoryId());

            AnnonceDTO createdDTO = AnnonceMapper.toDTO(created);
            return Response.status(Response.Status.CREATED)
                    .entity(createdDTO)
                    .location(URI.create("/api/annonces/" + created.getId()))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la création de l'annonce"))
                    .build();
        }
    }

    // ========== PUT /api/annonces/{id} – Mise à jour complète ==========

    /**
     * Met à jour complètement une annonce existante.
     *
     * @param id  identifiant de l'annonce
     * @param dto les nouvelles données
     * @return 200 OK avec le DTO mis à jour, 404 Not Found, ou 400 Bad Request
     */
    @PUT
    @Path("/{id}")
    @Secured
    public Response updateAnnonce(@PathParam("id") Long id, @Valid AnnonceDTO dto) {
        // Vérifier que l'annonce existe
        Optional<Annonce> existing = annonceService.getAnnonceById(id);
        if (!existing.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        Annonce annonce = existing.get();

        // Exercice 7 : Seul l'auteur peut modifier son annonce
        Long currentUserId = (Long) requestContext.getProperty("userId");
        if (annonce.getAuthor() != null && !annonce.getAuthor().getId().equals(currentUserId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("Seul l'auteur peut modifier cette annonce"))
                    .build();
        }

        // Exercice 7 : Une annonce PUBLISHED ne peut plus être modifiée
        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorResponse("Une annonce publiée ne peut plus être modifiée"))
                    .build();
        }

        try {
            annonceService.updateAnnonce(
                    id,
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getAdress(),
                    dto.getMail(),
                    dto.getCategoryId());

            // Récupérer l'annonce mise à jour pour la réponse
            Annonce updated = annonceService.getAnnonceById(id).get();
            AnnonceDTO updatedDTO = AnnonceMapper.toDTO(updated);
            return Response.ok(updatedDTO).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la mise à jour de l'annonce"))
                    .build();
        }
    }

    // ========== DELETE /api/annonces/{id} – Suppression ==========

    /**
     * Supprime une annonce par son ID.
     *
     * @param id identifiant de l'annonce
     * @return 204 No Content si succès, ou 404 Not Found
     */
    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteAnnonce(@PathParam("id") Long id) {
        // Vérifier que l'annonce existe
        Optional<Annonce> existing = annonceService.getAnnonceById(id);
        if (!existing.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        Annonce annonce = existing.get();

        // Exercice 7 : Seul l'auteur peut supprimer son annonce
        Long currentUserId = (Long) requestContext.getProperty("userId");
        if (annonce.getAuthor() != null && !annonce.getAuthor().getId().equals(currentUserId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("Seul l'auteur peut supprimer cette annonce"))
                    .build();
        }

        // Exercice 7 : Archivage obligatoire avant suppression
        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorResponse("L'annonce doit être archivée avant d'être supprimée. Statut actuel : "
                            + annonce.getStatus()))
                    .build();
        }

        try {
            annonceService.deleteAnnonce(id);
            return Response.noContent().build(); // 204

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la suppression de l'annonce"))
                    .build();
        }
    }

    // ========== PATCH /api/annonces/{id} – Mise à jour partielle (Bonus)
    // ==========

    /**
     * Met à jour partiellement une annonce.
     * Seuls les champs non-null dans le DTO sont mis à jour.
     *
     * Résultat attendu : permet de modifier un ou plusieurs champs
     * sans avoir à envoyer l'intégralité de la ressource (contrairement à PUT).
     * Utile quand on veut changer uniquement le titre ou la description par
     * exemple.
     *
     * @param id  identifiant de l'annonce
     * @param dto les champs à mettre à jour (seuls les non-null seront appliqués)
     * @return 200 OK avec le DTO mis à jour, ou 404 Not Found
     */
    @PATCH
    @Path("/{id}")
    @Secured
    public Response patchAnnonce(@PathParam("id") Long id, AnnonceDTO dto) {
        Optional<Annonce> existingOpt = annonceService.getAnnonceById(id);
        if (!existingOpt.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        Annonce existing = existingOpt.get();

        try {
            // Mise à jour partielle : on ne modifie que les champs fournis (non-null)
            String title = dto.getTitle() != null ? dto.getTitle() : existing.getTitle();
            String description = dto.getDescription() != null ? dto.getDescription() : existing.getDescription();
            String adress = dto.getAdress() != null ? dto.getAdress() : existing.getAdress();
            String mail = dto.getMail() != null ? dto.getMail() : existing.getMail();
            Long categoryId = dto.getCategoryId() != null ? dto.getCategoryId() : existing.getCategory().getId();

            annonceService.updateAnnonce(id, title, description, adress, mail, categoryId);

            Annonce updated = annonceService.getAnnonceById(id).get();
            AnnonceDTO updatedDTO = AnnonceMapper.toDTO(updated);
            return Response.ok(updatedDTO).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la mise à jour partielle"))
                    .build();
        }
    }

    // ========== GET /api/annonces/user/{userId}/drafts – Annonces brouillon d'un
    // utilisateur ==========

    /**
     * Récupère les annonces en brouillon (DRAFT) d'un utilisateur.
     * L'utilisateur peut ainsi voir ses annonces non publiées et décider de les
     * publier.
     *
     * @param userId identifiant de l'utilisateur
     * @return 200 OK avec la liste des annonces DRAFT
     */
    @GET
    @Path("/user/{userId}/drafts")
    @Secured
    public Response getUserDraftAnnonces(@PathParam("userId") Long userId) {
        try {
            List<Annonce> drafts = annonceService.getUserAnnoncesByStatus(userId, AnnonceStatus.DRAFT);
            List<AnnonceDTO> dtos = AnnonceMapper.toDTOList(drafts);
            return Response.ok(dtos).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la récupération des brouillons"))
                    .build();
        }
    }

    // ========== PUT /api/annonces/{id}/publish – Publier une annonce ==========

    /**
     * Publie une annonce (passe son statut de DRAFT à PUBLISHED).
     * Seules les annonces en brouillon peuvent être publiées.
     *
     * @param id identifiant de l'annonce
     * @return 200 OK avec le DTO mis à jour, 404 si introuvable, 409 si conflit
     *         métier
     */
    @PUT
    @Path("/{id}/publish")
    @Secured
    public Response publishAnnonce(@PathParam("id") Long id) {
        Optional<Annonce> existing = annonceService.getAnnonceById(id);
        if (!existing.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        try {
            annonceService.publishAnnonce(id);

            Annonce published = annonceService.getAnnonceById(id).get();
            AnnonceDTO dto = AnnonceMapper.toDTO(published);
            return Response.ok(dto).build();

        } catch (RuntimeException e) {
            if (e.getCause() instanceof IllegalStateException) {
                // 409 Conflict : l'annonce n'est pas en DRAFT
                return Response.status(Response.Status.CONFLICT)
                        .entity(errorResponse(e.getCause().getMessage()))
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la publication"))
                    .build();
        }
    }

    // ========== PUT /api/annonces/{id}/archive – Archiver une annonce ==========

    /**
     * Archive une annonce (passe son statut de PUBLISHED à ARCHIVED).
     * Exercice 7 : L'archivage est obligatoire avant la suppression.
     *
     * @param id identifiant de l'annonce
     * @return 200 OK avec le DTO mis à jour, 404 si introuvable, 409 si conflit
     *         métier
     */
    @PUT
    @Path("/{id}/archive")
    @Secured
    public Response archiveAnnonce(@PathParam("id") Long id) {
        Optional<Annonce> existing = annonceService.getAnnonceById(id);
        if (!existing.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        try {
            annonceService.archiveAnnonce(id);

            Annonce archived = annonceService.getAnnonceById(id).get();
            AnnonceDTO dto = AnnonceMapper.toDTO(archived);
            return Response.ok(dto).build();

        } catch (RuntimeException e) {
            if (e.getCause() instanceof IllegalStateException) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(errorResponse(e.getCause().getMessage()))
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de l'archivage"))
                    .build();
        }
    }

    // ========== Utilitaires ==========

    /**
     * Construit une réponse d'erreur JSON normalisée.
     */
    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
