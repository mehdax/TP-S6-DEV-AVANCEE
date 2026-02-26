package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.AnnonceService;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/annonces")
@RequiredArgsConstructor
public class AnnonceController {

        private final AnnonceService annonceService;
        private final AnnonceMapper annonceMapper;

        @GetMapping
        public ResponseEntity<Map<String, Object>> getAllAnnonces(
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestParam(name = "sortBy", defaultValue = "date") String sortBy,
                        @RequestParam(name = "direction", defaultValue = "desc") String direction,
                        @RequestParam(name = "q", required = false) String q,
                        @RequestParam(name = "status", required = false) AnnonceStatus status,
                        @RequestParam(name = "categoryId", required = false) Long categoryId,
                        @RequestParam(name = "authorId", required = false) Long authorId,
                        @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
                        @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate) {

                Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
                Pageable pageable = PageRequest.of(page, size, sort);

                Page<Annonce> annoncePage = annonceService.searchAnnonces(q, status, categoryId, authorId, fromDate,
                                toDate,
                                pageable);

                List<AnnonceDTO> dtos = annoncePage.getContent().stream()
                                .map(annonceMapper::toDto)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(Map.of(
                                "content", dtos,
                                "page", annoncePage.getNumber(),
                                "size", annoncePage.getSize(),
                                "totalElements", annoncePage.getTotalElements(),
                                "totalPages", annoncePage.getTotalPages()));
        }

        @GetMapping("/{id}")
        public ResponseEntity<AnnonceDTO> getAnnonceById(@PathVariable Long id) {
                return annonceService.getAnnonceById(id)
                                .map(annonceMapper::toDto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<AnnonceDTO> createAnnonce(@Valid @RequestBody AnnonceDTO dto) {
                Annonce created = annonceService.createAnnonce(
                                dto.getTitle(),
                                dto.getDescription(),
                                dto.getAdress(),
                                dto.getMail(),
                                dto.getAuthorId(),
                                dto.getCategoryId());

                AnnonceDTO createdDTO = annonceMapper.toDto(created);
                return ResponseEntity.created(URI.create("/api/annonces/" + created.getId()))
                                .body(createdDTO);
        }

        @PutMapping("/{id}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<AnnonceDTO> updateAnnonce(@PathVariable Long id, @Valid @RequestBody AnnonceDTO dto) {
                annonceService.updateAnnonce(
                                id,
                                dto.getTitle(),
                                dto.getDescription(),
                                dto.getAdress(),
                                dto.getMail(),
                                dto.getCategoryId());

                return annonceService.getAnnonceById(id)
                                .map(annonceMapper::toDto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
                annonceService.deleteAnnonce(id);
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{id}")
        public ResponseEntity<AnnonceDTO> patchAnnonce(@PathVariable Long id, @RequestBody AnnonceDTO dto) {
                Annonce existing = annonceService.getAnnonceById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

                annonceMapper.updateFromDto(dto, existing);
                // Note: Relation re-linking might be needed if they changed categoryId in DTO
                // For now keep it simple to match the instruction of using @MappingTarget

                return ResponseEntity.ok(annonceMapper.toDto(existing));
        }

        @PutMapping("/{id}/publish")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<AnnonceDTO> publishAnnonce(@PathVariable Long id) {
                annonceService.publishAnnonce(id);
                return annonceService.getAnnonceById(id)
                                .map(annonceMapper::toDto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}/archive")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<AnnonceDTO> archiveAnnonce(@PathVariable Long id) {
                annonceService.archiveAnnonce(id);
                return annonceService.getAnnonceById(id)
                                .map(annonceMapper::toDto)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }
}
