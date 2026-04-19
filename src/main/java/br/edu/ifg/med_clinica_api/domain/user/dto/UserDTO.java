package br.edu.ifg.med_clinica_api.domain.user.dto;

public record UserDTO(
        String email,
        String password
) {
    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
