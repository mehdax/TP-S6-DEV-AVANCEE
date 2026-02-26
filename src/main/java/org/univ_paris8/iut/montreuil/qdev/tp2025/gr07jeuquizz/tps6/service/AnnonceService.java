package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.repository.specification.AnnonceSpecifications;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Annonce createAnnonce(String title, String description, String adress,
            String mail, Long authorId, Long categoryId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        Annonce annonce = Annonce.builder()
                .title(title)
                .description(description)
                .adress(adress)
                .mail(mail)
                .author(author)
                .category(category)
                .status(AnnonceStatus.DRAFT)
                .build();

        return annonceRepository.save(annonce);
    }

    public void publishAnnonce(Long annonceId) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        annonce.publish();
        annonceRepository.save(annonce);
    }

    public void archiveAnnonce(Long annonceId) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        annonce.archive();
        annonceRepository.save(annonce);
    }

    public void updateAnnonce(Long annonceId, String title, String description,
            String adress, String mail, Long categoryId) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        annonce.setTitle(title);
        annonce.setDescription(description);
        annonce.setAdress(adress);
        annonce.setMail(mail);
        annonce.setCategory(category);

        annonceRepository.save(annonce);
    }

    public void deleteAnnonce(Long annonceId) {
        annonceRepository.deleteById(annonceId);
    }

    @Transactional(readOnly = true)
    public Page<Annonce> getAllPaginated(int page, int pageSize, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return annonceRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return annonceRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<Annonce> searchAnnonces(String q, AnnonceStatus status, Long categoryId, Long authorId,
            Date fromDate, Date toDate, Pageable pageable) {
        Specification<Annonce> spec = Specification.where(AnnonceSpecifications.hasKeyword(q))
                .and(AnnonceSpecifications.hasStatus(status))
                .and(AnnonceSpecifications.hasCategoryId(categoryId))
                .and(AnnonceSpecifications.hasAuthorId(authorId))
                .and(AnnonceSpecifications.isAfter(fromDate))
                .and(AnnonceSpecifications.isBefore(toDate));

        return annonceRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Annonce> getAnnonceById(Long id) {
        return annonceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Annonce> getUserAnnonces(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        // This would be better with a custom query in repository, but for now:
        Specification<Annonce> spec = AnnonceSpecifications.hasAuthorId(userId);
        return annonceRepository.findAll(spec);
    }

    @Transactional(readOnly = true)
    public List<Annonce> getUserAnnoncesByStatus(Long userId, AnnonceStatus status) {
        Specification<Annonce> spec = Specification.where(AnnonceSpecifications.hasAuthorId(userId))
                .and(AnnonceSpecifications.hasStatus(status));
        return annonceRepository.findAll(spec);
    }
}
