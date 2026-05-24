package br.edu.ifg.med_clinica_api.domain.dto.patient;

import br.edu.ifg.med_clinica_api.domain.entity.Address;
import br.edu.ifg.med_clinica_api.domain.entity.Patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientDetailDTO(UUID id, String nome, String email, String cpf, String telefone, LocalDate birthDate,
                               String gender,
                               Address endereco) {

    public PatientDetailDTO(Patient patient) {
        this (
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getCpf(),
                patient.getPhone(),
                patient.getBirthDate(),
                patient.getGender(),
                patient.getAddress()
        );

    }
}