package br.edu.ifg.med_clinica_api.infra.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServerExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDatabaseError(
            DataAccessException exception,
            HttpServletRequest request
    ) {


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage(),
                        request
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalServerError(
            Exception exception,
            HttpServletRequest request
    ) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        exception.getMessage(),
                        request
                ));
    }
}
