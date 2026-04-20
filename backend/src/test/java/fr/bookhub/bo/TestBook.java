package fr.bookhub.bo;

import fr.bookhub.bo.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.CategoriesRepository;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class BookRepositoryTest {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Test
    void test_save_4_books() {

        // 1. Catégorie obligatoire
        Categories category = new Categories();
        category.setName("Programming");
        category = categoriesRepository.save(category);

        // 2. Livre 1
        Book book1 = new Book();
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert C. Martin");
        book1.setIsbn("9780132350884");
        book1.setDescription("A Handbook of Agile Software Craftsmanship");
        book1.setAvailable(true);
        book1.setCreatedAt(LocalDateTime.now());
        book1.setCategory(category);

        // 3. Livre 2
        Book book2 = new Book();
        book2.setTitle("The Pragmatic Programmer");
        book2.setAuthor("Andrew Hunt & David Thomas");
        book2.setIsbn("9780201616224");
        book2.setDescription("Your Journey to Mastery");
        book2.setAvailable(true);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setCategory(category);

        // 4. Livre 3
        Book book3 = new Book();
        book3.setTitle("Design Patterns");
        book3.setAuthor("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides");
        book3.setIsbn("9780201633610");
        book3.setDescription("Elements of Reusable Object-Oriented Software");
        book3.setAvailable(false);
        book3.setCreatedAt(LocalDateTime.now());
        book3.setCategory(category);

        // 5. Livre 4
        Book book4 = new Book();
        book4.setTitle("Refactoring");
        book4.setAuthor("Martin Fowler");
        book4.setIsbn("9780201485677");
        book4.setDescription("Improving the Design of Existing Code");
        book4.setAvailable(true);
        book4.setCreatedAt(LocalDateTime.now());
        book4.setCategory(category);


        // Appel du comportement
        Book bookDB = booksRepository.save(book1);
        booksRepository.save(book2);
        booksRepository.save(book3);
        booksRepository.save(book4);

        // Vérifications
        assertThat(bookDB).isNotNull();
        assertThat(bookDB.getId()).isNotNull();
        assertThat(bookDB.getIsbn()).isEqualTo("9780132350884");
        assertThat(bookDB.getTitle()).isEqualTo("Clean Code");
    }

}