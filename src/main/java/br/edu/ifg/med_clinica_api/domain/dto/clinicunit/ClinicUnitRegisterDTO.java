package br.edu.ifg.med_clinica_api.domain.dto.clinicunit;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClinicUnitRegisterDTO(
        @NotBlank(message = "O nome da unidade e obrigatorio.")
        String name,

        @NotNull(message = "O endereco da unidade e obrigatorio.")
        AddressData address,

        @NotBlank(message = "O telefone da unidade e obrigatorio.")
        String phone
) {
}
