package br.edu.ifg.med_clinica_api.infra.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;

import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class ClientExceptionHandler extends BaseExceptionHandler{

    // 404 NOT FOUND
    @ExceptionHandler
    public ResponseEntity<?> handleNotFound(
        EntityNotFoundException exception, HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage(),
                        request
                )
        );
    }


    // 400 BAD REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequest (
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        request
                )
        );
    }


    // 405 METHOD NOT ALLOWED
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<?> handleNotAllowed(
            MethodNotAllowedException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                buildError(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        exception.getMessage(),
                        request
                )
        );
    }

    // 401 UNAUTHORIZED
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleUnauthorized(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(
                        HttpStatus.UNAUTHORIZED,
                        exception.getMessage(),
                        request
                )
        );
    }

    // 403 FORBIDDEN
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildError(
                        HttpStatus.FORBIDDEN,
                        exception.getMessage(),
                        request
                )
        );
    }

}
