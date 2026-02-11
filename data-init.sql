-- ============================================================================
-- Script d'initialisation - Données de Test pour TP2
-- ============================================================================
-- Ce script insère les données initiales nécessaires pour tester l'application
-- Exécutez ce script APRÈS que Hibernate ait créé les tables (hbm2ddl.auto=create)

-- Insérer les utilisateurs de test
INSERT INTO users (username, email, password, created_at) VALUES
('admin', 'admin@iut.fr', 'admin123', NOW()),
('user1', 'user1@example.com', 'password1', NOW()),
('user2', 'user2@example.com', 'password2', NOW()),
('alice', 'alice@test.com', 'alice123', NOW());

-- Insérer les catégories
INSERT INTO category (label) VALUES
('Électronique'),
('Vêtements'),
('Immobilier'),
('Automobile'),
('Livres'),
('Sports'),
('Emploi'),
('Services');

-- Insérer quelques annonces de démonstration (PUBLISHED)
INSERT INTO "Annonce" (title, description, adress, mail, date, status, author_id, category_id) VALUES
('iPhone 13 - Excellent état', 'Vends iPhone 13 128GB en excellent état, batterie 90%', '75001 Paris', 'user1@example.com', NOW() - INTERVAL '5 days', 'PUBLISHED', 2, 1),
('Studio meublé - Bastille', 'Joli studio 25m² près de la Bastille, balcon vue cour', '75004 Paris', 'admin@iut.fr', NOW() - INTERVAL '3 days', 'PUBLISHED', 1, 3),
('Cours de Python particuliers', 'Ingénieur expérimenté propose cours de Python/Django', 'Saint-Denis', 'alice@test.com', NOW() - INTERVAL '1 day', 'PUBLISHED', 4, 7),
('Chaise IKEA MARKUS - 50€', 'Chaise gaming IKEA en bon état, peu utilisée', '75011 Paris', 'user2@example.com', NOW(), 'PUBLISHED', 3, 2);

-- Insérer des annonces en DRAFT (brouillon)
INSERT INTO "Annonce" (title, description, adress, mail, date, status, author_id, category_id) VALUES
('MacBook Pro 15" - À vendre', 'À rédiger...', '75009 Paris', 'user1@example.com', NOW(), 'DRAFT', 2, 1),
('Appartement 2 pièces - Belleville', 'À déterminer les détails...', '75020 Paris', 'admin@iut.fr', NOW(), 'DRAFT', 1, 3);

-- Insérer des annonces ARCHIVED
INSERT INTO "Annonce" (title, description, adress, mail, date, status, author_id, category_id) VALUES
('Ancien modèle Android - Obsolète', 'Téléphone trop ancien', '75010 Paris', 'alice@test.com', NOW() - INTERVAL '30 days', 'ARCHIVED', 4, 1);

-- ============================================================================
-- Afficher les données insérées
-- ============================================================================
SELECT 'Utilisateurs:' AS "Status";
SELECT id, username, email FROM users;

SELECT 'Catégories:' AS "Status";
SELECT id, label FROM category;

SELECT 'Annonces par statut:' AS "Status";
SELECT COUNT(*), status FROM "Annonce" GROUP BY status;
