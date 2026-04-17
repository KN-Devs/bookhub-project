package fr.bookhub.bo;

import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.CategoriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Utilise H2 en mémoire
class BookRepositoryTest {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private CategoriesRepository categoryRepository;

    private Categories techCategory;

    @BeforeEach
    void setUp() {
        // On crée une catégorie commune pour nos tests
        techCategory = new Categories();
        techCategory.setName("Technologie");
        categoryRepository.save(techCategory);
    }

    @Test
    @DisplayName("Devrait enregistrer un livre avec succès")
    void shouldSaveBook() {
        // Given
        Book book = new Book("Effective Java", "Joshua Bloch", "978-0134685991",
                "Un guide indispensable", null, true, LocalDateTime.now(), techCategory);

        // When
        Book savedBook = booksRepository.save(book);

        // Then
        assertThat(savedBook.getId()).isGreaterThan(0);
        assertThat(savedBook.getTitle()).isEqualTo("Effective Java");
        assertThat(savedBook.getCategory().getName()).isEqualTo("Technologie");
    }

    @Test
    @DisplayName("Devrait enregistrer et compter plusieurs livres")
    void shouldSaveMultipleBooks() {
        // Given
        Book b1 = new Book("Clean Code", "R. Martin", "111-222", "Desc", null, true, LocalDateTime.now(), techCategory);
        Book b2 = new Book("Design Patterns", "Gang of Four", "333-444", "Desc", null, true, LocalDateTime.now(), techCategory);

        // When
        booksRepository.saveAll(List.of(b1, b2));
        List<Book> allBooks = booksRepository.findAll();

        // Then
        assertThat(allBooks).hasSize(2);
        assertThat(allBooks).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Design Patterns");
    }
}