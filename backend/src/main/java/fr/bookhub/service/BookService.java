package fr.bookhub.service;

import fr.bookhub.bo.Book;
import fr.bookhub.dto.BookRequest;
import fr.bookhub.dto.BookResponse;
import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
    BookResponse getBookByIsbn(String isbn);
    BookResponse getBookById(Integer id);
    Book createBook(BookRequest dto);
    Book updateBook(String isbn, BookRequest dto);
}