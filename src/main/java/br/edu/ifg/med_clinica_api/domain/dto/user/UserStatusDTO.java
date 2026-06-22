package br.edu.ifg.med_clinica_api.domain.dto.user;

import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;

import java.util.UUID;

public record UserStatusDTO(
        UUID id,
        String email,
        UserRole role,
        String status
) {
    public UserStatusDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                !Boolean.FALSE.equals(user.getActive()) ? "ACTIVE" : "INACTIVE"
        );
    }
}
