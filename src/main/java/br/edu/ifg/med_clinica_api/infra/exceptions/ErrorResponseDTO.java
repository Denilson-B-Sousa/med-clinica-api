package br.edu.ifg.med_clinica_api.infra.exceptions;

import java.time.LocalDateTime;

public record ErrorResponseDTO(

        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
