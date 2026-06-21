package br.edu.ifg.med_clinica_api.domain.dto.clinicunit;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressData;

public record ClinicUnitUpdateDTO(
        String name,
        AddressData address,
        String phone
) {
}
