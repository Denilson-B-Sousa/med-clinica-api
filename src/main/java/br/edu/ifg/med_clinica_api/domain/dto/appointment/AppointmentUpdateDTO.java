package br.edu.ifg.med_clinica_api.domain.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record AppointmentUpdateDTO(

        @Future
        @NotBlank
        LocalDateTime scheduleAt
) {
}
