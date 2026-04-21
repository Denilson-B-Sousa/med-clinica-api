package br.edu.ifg.med_clinica_api.domain.doctor.dto;

import br.edu.ifg.med_clinica_api.domain.address.Address;
import br.edu.ifg.med_clinica_api.domain.doctor.MedicalSpeciality;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DoctorRegisterDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O CPF é obrigatório.")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}")
        String cpf,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String phone,

        @NotBlank
        @Pattern(
                regexp = "^\\d{4,6}-[A-Z]{2}$",
                message = "CRM deve estar no formato 123456-UF (ex: 123456-SP)"
        )
        String crm,

        @NotNull
        Address address,

        @NotNull
        MedicalSpeciality speciality
) {
}
