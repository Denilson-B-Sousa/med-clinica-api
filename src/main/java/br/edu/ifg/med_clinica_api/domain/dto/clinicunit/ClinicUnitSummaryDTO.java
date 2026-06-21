package br.edu.ifg.med_clinica_api.domain.dto.clinicunit;

import br.edu.ifg.med_clinica_api.domain.entity.Address;
import br.edu.ifg.med_clinica_api.domain.entity.ClinicUnit;

import java.util.UUID;

public record ClinicUnitSummaryDTO(
        UUID id,
        String name,
        Address address,
        String phone
) {
    public ClinicUnitSummaryDTO(ClinicUnit clinicUnit) {
        this(
                clinicUnit.getId(),
                clinicUnit.getName(),
                clinicUnit.getAddress(),
                clinicUnit.getPhone()
        );
    }
}
