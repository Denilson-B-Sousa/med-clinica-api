package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressData;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DoctorUpdateDTO(
        @NotBlank
        String name,

        @NotBlank
        String phone,

        AddressData address,

        UUID clinicUnitId
) {
}
