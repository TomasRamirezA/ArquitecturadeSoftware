package edu.eci.arsw.parallelism.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
/**
 * Global controller advice to translate exceptions into HTTP responses.
 */
public class GlobalExceptionHandler {
    /**
     * Global exception handler for validation errors thrown by Jakarta Validation.
     *
     * @param ex the captured {@link ConstraintViolationException}
     * @return a {@link ResponseEntity} with HTTP 400 and the violation message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}