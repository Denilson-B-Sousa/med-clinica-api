package br.edu.ifg.med_clinica_api.domain.dto.user;

import br.edu.ifg.med_clinica_api.domain.enums.UserRole;

import java.util.UUID;

public record UserProfileDTO(
        UUID id,
        UUID userId,
        UUID patientId,
        UUID doctorId,
        String name,
        String email,
        UserRole role,
        Object profile
) {
}
