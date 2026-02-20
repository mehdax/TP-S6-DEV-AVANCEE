package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil;

import javax.persistence.EntityManager;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println(" Test des DAO...\n");

        
        JPAUtil.executeInTransaction(em -> {

            
            UserDAO userDAO = new UserDAO(em);
            CategoryDAO categoryDAO = new CategoryDAO(em);
            AnnonceDAO annonceDAO = new AnnonceDAO(em);

            
            User user = new User();
            user.setUsername("john_doe");
            user.setEmail("john@example.com");
            user.setPassword("password123");
            userDAO.save(user);
            System.out.println("✅ User créé : " + user.getUsername());

            
            Category category = new Category();
            category.setLabel("Informatique");
            categoryDAO.save(category);
            System.out.println("✅ Category créée : " + category.getLabel());

            
            Annonce annonce = new Annonce();
            annonce.setTitle("Vends PC Gamer");
            annonce.setDescription("PC Gaming RTX 4090, excellent état");
            annonce.setAdress("12 rue de Paris");
            annonce.setMail("john@example.com");
            annonce.setAuthor(user);
            annonce.setCategory(category);
            annonceDAO.save(annonce);
            System.out.println("✅ Annonce créée : " + annonce.getTitle());
        });

        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            UserDAO userDAO = new UserDAO(em);
            AnnonceDAO annonceDAO = new AnnonceDAO(em);

            System.out.println("\n Vérification :");
            System.out.println("Nombre d'utilisateurs : " + userDAO.count());
            System.out.println("Nombre d'annonces : " + annonceDAO.count());

            
            userDAO.findByUsername("john_doe").ifPresent(u ->
                    System.out.println("User trouvé : " + u.getEmail())
            );

        } finally {
            em.close();
        }

        
        JPAUtil.shutdown();
        System.out.println("\n✅ Test terminé !");
    }
}

