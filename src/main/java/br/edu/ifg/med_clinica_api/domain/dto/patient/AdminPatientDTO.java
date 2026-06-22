package br.edu.ifg.med_clinica_api.domain.dto.patient;

import br.edu.ifg.med_clinica_api.domain.entity.Patient;

import java.time.LocalDate;
import java.util.UUID;

public record AdminPatientDTO(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        String gender,
        LocalDate birthDate,
        UUID userId,
        String status
) {
    public AdminPatientDTO(Patient patient) {
        this(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getCpf(),
                patient.getPhone(),
                patient.getGender(),
                patient.getBirthDate(),
                patient.getUser() != null ? patient.getUser().getId() : null,
                Boolean.TRUE.equals(patient.getActive()) ? "ACTIVE" : "INACTIVE"
        );
    }
}
