package fr.bookhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(LoansNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(LoansNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erreur", ex.getMessage()));
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyBorrowed(BookAlreadyBorrowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erreur", ex.getMessage()));
    }

    @ExceptionHandler(LoansLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleLimit(LoansLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erreur", ex.getMessage()));
    }
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserBlockedByOverdueException.class)
    public ResponseEntity<Map<String, String>> handleBlocked(UserBlockedByOverdueException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("erreur", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne : " + ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}