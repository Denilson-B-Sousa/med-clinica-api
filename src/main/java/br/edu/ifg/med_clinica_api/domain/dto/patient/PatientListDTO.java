package br.edu.ifg.med_clinica_api.domain.dto.patient;

import br.edu.ifg.med_clinica_api.domain.entity.Address;
import br.edu.ifg.med_clinica_api.domain.entity.Patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientListDTO(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        String gender,
        LocalDate birthDate,
        Address address
) {

    public PatientListDTO(Patient patient) {
        this(
                patient.getId(),
                patient.getName(),
                patient.getCpf(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getGender(),
                patient.getBirthDate(),
                patient.getAddress()
        );
    }

}
