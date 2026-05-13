package br.edu.ifg.med_clinica_api.infra.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public abstract class BaseExceptionHandler {

    protected ErrorResponseDTO buildError (
        HttpStatus status,
        String message,
        HttpServletRequest request) {

        return new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestId()
        );
    }
}
