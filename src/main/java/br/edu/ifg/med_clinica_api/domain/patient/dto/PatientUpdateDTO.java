package br.edu.ifg.med_clinica_api.domain.patient.dto;

import br.edu.ifg.med_clinica_api.domain.address.Address;
import br.edu.ifg.med_clinica_api.domain.patient.Patient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientUpdateDTO(
        @NotBlank
        String name,

        @NotBlank
        String phone,

        @NotBlank
        @Email
        String email,

        @Valid
        Address address
) {
    public PatientUpdateDTO(Patient patient) {
        this(
                patient.getName(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress()
        );
    }
}
