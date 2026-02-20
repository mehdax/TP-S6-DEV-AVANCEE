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
@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private final AnnonceService annonceService = new AnnonceService();

    @Context
    private ContainerRequestContext requestContext;
    @GET
    public Response getAllAnnonces(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int pageSize) {

        List<Annonce> annonces = annonceService.getAllPaginated(page, pageSize);
        List<AnnonceDTO> dtos = AnnonceMapper.toDTOList(annonces);
        long total = annonceService.countAll();

        
        Map<String, Object> response = new HashMap<>();
        response.put("content", dtos);
        response.put("page", page);
        response.put("size", pageSize);
        response.put("totalElements", total);
        response.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return Response.ok(response).build();
    }
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
    @POST
    @Secured
    public Response createAnnonce(@Valid AnnonceDTO dto) {
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
    @PUT
    @Path("/{id}")
    @Secured
    public Response updateAnnonce(@PathParam("id") Long id, @Valid AnnonceDTO dto) {
        
        Optional<Annonce> existing = annonceService.getAnnonceById(id);
        if (!existing.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        Annonce annonce = existing.get();

        
        Long currentUserId = (Long) requestContext.getProperty("userId");
        if (annonce.getAuthor() != null && !annonce.getAuthor().getId().equals(currentUserId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("Seul l'auteur peut modifier cette annonce"))
                    .build();
        }

        
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
    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteAnnonce(@PathParam("id") Long id) {
        
        Optional<Annonce> existing = annonceService.getAnnonceById(id);
        if (!existing.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse("Annonce introuvable avec l'id : " + id))
                    .build();
        }

        Annonce annonce = existing.get();

        
        Long currentUserId = (Long) requestContext.getProperty("userId");
        if (annonce.getAuthor() != null && !annonce.getAuthor().getId().equals(currentUserId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("Seul l'auteur peut supprimer cette annonce"))
                    .build();
        }

        
        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorResponse("L'annonce doit être archivée avant d'être supprimée. Statut actuel : "
                            + annonce.getStatus()))
                    .build();
        }

        try {
            annonceService.deleteAnnonce(id);
            return Response.noContent().build(); 

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la suppression de l'annonce"))
                    .build();
        }
    }
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
                
                return Response.status(Response.Status.CONFLICT)
                        .entity(errorResponse(e.getCause().getMessage()))
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Erreur lors de la publication"))
                    .build();
        }
    }
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
    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

