-- ========================================
-- SCRIPT D'INITIALISATION - BookHub
-- Base de données: BOOK_HUB
-- ========================================

-- 1. VÉRIFIER ET CRÉER LA TABLE USER (si n'existe pas)
-- Structure basée sur la DB fournie
CREATE TABLE user (
    id INT IDENTITY(1,1) PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    roles NVARCHAR(MAX) NOT NULL DEFAULT N'["ROLE_USER"]',
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    is_active TINYINT NOT NULL DEFAULT 1
);

-- 2. CRÉER LES AUTRES TABLES (si n'existent pas)
CREATE TABLE category (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE book (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    description NVARCHAR(MAX) DEFAULT NULL,
    cover_image VARCHAR(255) DEFAULT NULL,
    is_available TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT GETDATE(),
    category_id INT NOT NULL,
    CONSTRAINT fk_book_category
        FOREIGN KEY (category_id) REFERENCES category(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE
);

CREATE TABLE loan (
    id INT IDENTITY(1,1) PRIMARY KEY,
    loan_date DATETIME NOT NULL DEFAULT GETDATE(),
    due_date DATETIME NOT NULL,
    returned_at DATETIME DEFAULT NULL,
    status VARCHAR(50) NOT NULL,
    is_late TINYINT NOT NULL DEFAULT 0,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    CONSTRAINT fk_loan_user
        FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    CONSTRAINT fk_loan_book
        FOREIGN KEY (book_id) REFERENCES book(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE
);

CREATE TABLE reservation (
    id INT IDENTITY(1,1) PRIMARY KEY,
    reservation_date DATETIME NOT NULL DEFAULT GETDATE(),
    status VARCHAR(50) NOT NULL,
    queue_position INT NOT NULL,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_book
        FOREIGN KEY (book_id) REFERENCES book(id)
        ON DELETE NO ACTION
        ON UPDATE CASCADE
);

CREATE TABLE review (
    id INT IDENTITY(1,1) PRIMARY KEY,
    rating INT NOT NULL,
    comment NVARCHAR(MAX) DEFAULT NULL,
    review_date DATETIME NOT NULL DEFAULT GETDATE(),
    is_moderated TINYINT NOT NULL DEFAULT 0,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id) REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_review_book
        FOREIGN KEY (book_id) REFERENCES book(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_review_user_book UNIQUE (user_id, book_id),
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
);

-- ========================================
-- 3. INSÉRER DES DONNÉES DE TEST
-- ========================================

-- 3.1 Catégories (Données de test)
INSERT INTO category (name) VALUES
('Science-Fiction'),
('Fantasy'),
('Mystère'),
('Romance'),
('Non-Fiction');

-- 3.2 Livres (Données de test)
INSERT INTO book (title, author, isbn, description, cover_image, is_available, category_id)
VALUES
('Dune', 'Frank Herbert', '978-0-441-17271-9', 'Une épopée de science-fiction dans l''univers de Dune', NULL, 1, 1),
('Le Seigneur des Anneaux', 'J.R.R. Tolkien', '978-0-544-92917-0', 'Une quête épique en Terre du Milieu', NULL, 1, 2),
('Agatha Smith', 'Agatha Christie', '978-0-062-07343-5', 'Un mystère criminel palpitant', NULL, 1, 3),
('Orgueil et Préjugés', 'Jane Austen', '978-0-432-12345-6', 'Une histoire d''amour et de société', NULL, 1, 4),
('Sapiens', 'Yuval Noah Harari', '978-0-062-31657-1', 'L''histoire de l''humanité', NULL, 1, 5);

-- ========================================
-- 4. RESET IDENTITY (SqlServer) - Si besoin
-- ========================================
DBCC CHECKIDENT (user, RESEED, 0);
DBCC CHECKIDENT (category, RESEED, 0);
DBCC CHECKIDENT (book, RESEED, 0);
DBCC CHECKIDENT (loan, RESEED, 0);
DBCC CHECKIDENT (reservation, RESEED, 0);
DBCC CHECKIDENT (review, RESEED, 0);

-- ========================================
-- 5. VÉRIFICATIONS
-- ========================================

-- Vérifier les catégories
SELECT * FROM category;

-- Vérifier les livres
SELECT b.id, b.title, b.author, b.isbn, c.name as category 
FROM book b 
JOIN category c ON b.category_id = c.id;

-- Vérifier les utilisateurs (vides initialement)
SELECT * FROM user;

-- ========================================
-- 6. INFORMATIONS DE TEST
-- ========================================
/*
APRÈS avoir exécuté ce script:

1. Aller à: http://localhost:4200/register

2. Créer un compte avec:
   - Nom: Dupont
   - Prénom: Jean
   - Email: jean@example.com
   - Mot de passe: Password123

3. Se connecter avec ces identifiants

4. Vous verrez les 5 livres créés ci-dessus

5. Pour les tests ultérieurs:
   - Modifier les données des livres
   - Créer des emprunts
   - Faire des réservations
   - Laisser des avis

NOTA BENE:
- Les mots de passe seront hashés avec BCrypt en backend
- Les rôles par défaut sont ["ROLE_USER"]
- is_active = 1 signifie le compte est actif
*/

-- ========================================
-- 7. NETTOYAGE (Si besoin de tout réinitialiser)
-- ========================================
/*
-- Supprimer les données (garder la structure)
DELETE FROM review;
DELETE FROM reservation;
DELETE FROM loan;
DELETE FROM book;
DELETE FROM category;
DELETE FROM user;

-- Ou si besoin de supprimer les tables:
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS loan;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS category;
*/