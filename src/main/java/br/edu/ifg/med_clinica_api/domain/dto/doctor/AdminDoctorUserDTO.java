package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;

import java.util.UUID;

public record AdminDoctorUserDTO(
        UUID id,
        String name,
        String email,
        String crm,
        MedicalSpeciality speciality,
        UUID clinicUnitId,
        String clinicUnitName,
        UUID userId,
        String userEmail,
        String status
) {
    public AdminDoctorUserDTO(Doctor doctor) {
        this(
                doctor.getId(),
                doctor.getName(),
                doctor.getEmail(),
                doctor.getCrm(),
                doctor.getSpeciality(),
                doctor.getClinicUnit() != null ? doctor.getClinicUnit().getId() : null,
                doctor.getClinicUnit() != null ? doctor.getClinicUnit().getName() : null,
                doctor.getUser() != null ? doctor.getUser().getId() : null,
                doctor.getUser() != null ? doctor.getUser().getEmail() : null,
                Boolean.TRUE.equals(doctor.getActive()) ? "ACTIVE" : "INACTIVE"
        );
    }
}
