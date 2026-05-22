package br.edu.ifg.med_clinica_api.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
