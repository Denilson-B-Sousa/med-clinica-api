package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitSummaryDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;

import java.util.UUID;

public record DoctorDetailDTO(
        UUID id,
        String name,
        String cpf,
        String email,
        String phone,
        String crm,
        MedicalSpeciality speciality,
        ClinicUnitSummaryDTO clinicUnit
) {
    public DoctorDetailDTO(Doctor doctor) {
        this(
                doctor.getId(),
                doctor.getName(),
                doctor.getCpf(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getCrm(),
                doctor.getSpeciality(),
                doctor.getClinicUnit() != null ? new ClinicUnitSummaryDTO(doctor.getClinicUnit()) : null
        );
    }
}
