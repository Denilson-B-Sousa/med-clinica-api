package br.edu.ifg.med_clinica_api.model.dto.doctor;

import br.edu.ifg.med_clinica_api.model.entity.Doctor;
import br.edu.ifg.med_clinica_api.model.enums.MedicalSpeciality;

import java.util.UUID;

public record DoctorDetailDTO(
        UUID id,
        String name,
        String cpf,
        String email,
        String phone,
        String crm,
        MedicalSpeciality speciality
) {
    public DoctorDetailDTO(Doctor doctor) {
        this(
                doctor.getId(),
                doctor.getName(),
                doctor.getCpf(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getCrm(),
                doctor.getSpeciality()
        );
    }
}
