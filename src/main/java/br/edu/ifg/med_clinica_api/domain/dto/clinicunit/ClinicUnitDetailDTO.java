package br.edu.ifg.med_clinica_api.domain.dto.clinicunit;

import br.edu.ifg.med_clinica_api.domain.entity.Address;
import br.edu.ifg.med_clinica_api.domain.entity.ClinicUnit;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClinicUnitDetailDTO(
        UUID id,
        String name,
        Address address,
        String phone,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ClinicUnitDetailDTO(ClinicUnit clinicUnit) {
        this(
                clinicUnit.getId(),
                clinicUnit.getName(),
                clinicUnit.getAddress(),
                clinicUnit.getPhone(),
                clinicUnit.getActive(),
                clinicUnit.getCreatedAt(),
                clinicUnit.getUpdatedAt()
        );
    }
}
