package org.nttdata.com.servicioclientes.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        System.err.println("Error: " + ex.getMessage());
        //ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ErrorResponseValidItem> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponseValidItem(error.getField(), error.getDefaultMessage()))
                .toList();
        ErrorResponseValid errorResponse = new ErrorResponseValid(
                HttpStatus.BAD_REQUEST.value(),
                errors,
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Error de integridad de datos: posible duplicado");
    }
}
