package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.AnnonceNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security.Secured;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ressource JAX-RS pour les annonces (Exercice 2).
 * Toute la logique métier est déléguée à AnnonceService.
 */
@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Annonces", description = "CRUD des annonces")
public class AnnonceResource {

    private static final Logger logger = LoggerFactory.getLogger(AnnonceResource.class);
    private final AnnonceService annonceService = new AnnonceService();

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private UriInfo uriInfo;

    // ===========================================
    // GET /api/annonces – Liste paginée
    // ===========================================
    @GET
    @Operation(summary = "Liste paginée des annonces")
    public Response getAnnonces(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("q") String search) {

        if (pageSize < 1 || pageSize > 100)
            pageSize = 10;
        if (page < 0)
            page = 0;

        List<Annonce> annonces;
        long total;

        if (search != null && !search.isBlank()) {
            annonces = annonceService.searchAnnonces(search);
            total = annonces.size();
            // Pagination manuelle pour la recherche
            int from = page * pageSize;
            int to = Math.min(from + pageSize, annonces.size());
            annonces = from < annonces.size() ? annonces.subList(from, to) : List.of();
        } else {
            annonces = annonceService.getAllAnnonces(page, pageSize);
            total = annonceService.countAllAnnonces();
        }

        List<AnnonceDTO> dtos = annonces.stream()
                .map(AnnonceMapper::toDTO)
                .collect(Collectors.toList());

        PagedResponse<AnnonceDTO> response = new PagedResponse<>(dtos, page, pageSize, total);
        return Response.ok(response).build();
    }

    // ===========================================
    // GET /api/annonces/{id} – Détail
    // ===========================================
    @GET
    @Path("/{id}")
    @Operation(summary = "Détail d'une annonce par ID")
    public Response getAnnonce(
            @Parameter(description = "ID de l'annonce") @PathParam("id") Long id) {

        Annonce annonce = annonceService.getAnnonceById(id)
                .orElseThrow(() -> new AnnonceNotFoundException(id));

        return Response.ok(AnnonceMapper.toDTO(annonce)).build();
    }

    // ===========================================
    // POST /api/annonces – Création
    // ===========================================
    @POST
    @Secured
    @Operation(summary = "Créer une nouvelle annonce (auth requise)")
    public Response createAnnonce(@Valid AnnonceCreateDTO dto) {
        Long authorId = getAuthenticatedUserId();

        Annonce created = annonceService.createAnnonce(
                dto.getTitle(), dto.getDescription(),
                dto.getAdress(), dto.getMail(),
                authorId, dto.getCategoryId());

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(created.getId()))
                .build();

        logger.info("Annonce créée: id={}", created.getId());
        return Response.created(location)
                .entity(AnnonceMapper.toDTO(created))
                .build();
    }

    // ===========================================
    // PUT /api/annonces/{id} – Mise à jour complète
    // ===========================================
    @PUT
    @Path("/{id}")
    @Secured
    @Operation(summary = "Mise à jour complète d'une annonce (auteur requis)")
    public Response updateAnnonce(
            @PathParam("id") Long id,
            @Valid AnnonceUpdateDTO dto) {

        Long requesterId = getAuthenticatedUserId();

        Annonce updated = annonceService.updateAnnonce(
                id, dto.getTitle(), dto.getDescription(),
                dto.getAdress(), dto.getMail(), dto.getCategoryId(), requesterId);

        return Response.ok(AnnonceMapper.toDTO(updated)).build();
    }

    // ===========================================
    // PATCH /api/annonces/{id} – Mise à jour partielle
    // ===========================================
    @PATCH
    @Path("/{id}")
    @Secured
    @Operation(summary = "Mise à jour partielle d'une annonce (seuls les champs fournis sont modifiés)")
    public Response patchAnnonce(
            @PathParam("id") Long id,
            @Valid AnnoncePatchDTO dto) {

        Long requesterId = getAuthenticatedUserId();

        Annonce patched = annonceService.patchAnnonce(
                id,
                dto.getTitle(), dto.getDescription(),
                dto.getAdress(), dto.getMail(),
                dto.getCategoryId(),
                requesterId);

        return Response.ok(AnnonceMapper.toDTO(patched)).build();
    }

    // ===========================================
    // DELETE /api/annonces/{id} – Suppression
    // ===========================================
    @DELETE
    @Path("/{id}")
    @Secured
    @Operation(summary = "Supprimer une annonce (doit être ARCHIVED, auteur requis)")
    public Response deleteAnnonce(@PathParam("id") Long id) {
        Long requesterId = getAuthenticatedUserId();
        annonceService.deleteAnnonce(id, requesterId);
        return Response.noContent().build();
    }

    // ===========================================
    // POST /api/annonces/{id}/publish
    // ===========================================
    @POST
    @Path("/{id}/publish")
    @Secured
    @Operation(summary = "Publier une annonce (DRAFT → PUBLISHED)")
    public Response publishAnnonce(@PathParam("id") Long id) {
        Long requesterId = getAuthenticatedUserId();
        annonceService.publishAnnonce(id, requesterId);
        return Response.ok(annonceService.getAnnonceById(id)
                .map(AnnonceMapper::toDTO).orElseThrow(() -> new AnnonceNotFoundException(id))).build();
    }

    // ===========================================
    // POST /api/annonces/{id}/archive
    // ===========================================
    @POST
    @Path("/{id}/archive")
    @Secured
    @Operation(summary = "Archiver une annonce (PUBLISHED → ARCHIVED)")
    public Response archiveAnnonce(@PathParam("id") Long id) {
        Long requesterId = getAuthenticatedUserId();
        annonceService.archiveAnnonce(id, requesterId);
        return Response.ok(annonceService.getAnnonceById(id)
                .map(AnnonceMapper::toDTO).orElseThrow(() -> new AnnonceNotFoundException(id))).build();
    }

    // ===========================================
    // Helper : récupérer l'userId du token
    // ===========================================
    private Long getAuthenticatedUserId() {
        Object userId = requestContext.getProperty("userId");
        if (userId == null) {
            throw new org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.exception.UnauthorizedException();
        }
        return (Long) userId;
    }
}
