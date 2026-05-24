package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorUpdateDTO(
        @NotBlank
        String name,

        @NotBlank
        String phone,

        @NotNull
        AddressData address
) {
}
