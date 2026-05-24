package br.edu.ifg.med_clinica_api.domain.dto.patient;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record PatientRegisterDTO(

        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email
        String email,

        @NotBlank
        @CPF
        String cpf,

        @NotBlank
        String password,

        @NotBlank(message = "O telefone é obrigatório.")
        @Pattern(
                regexp = "\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}",
                message = "Telefone inválido"
        )
        String phone,

        @Valid
        AddressData address,

        @NotNull
        LocalDate birthDate,

        @NotBlank(message = "O gênero é obrigatório.")
        String gender
) {
}
