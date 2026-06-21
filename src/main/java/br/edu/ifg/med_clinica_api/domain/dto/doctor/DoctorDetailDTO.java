package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.entity.Address;
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
        Address address
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
                doctor.getAddress()
        );
    }
}
