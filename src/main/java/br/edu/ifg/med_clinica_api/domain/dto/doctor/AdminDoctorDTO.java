package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;

import java.util.UUID;

public record AdminDoctorDTO(
        UUID id,
        String name,
        UUID clinicUnitId,
        String clinicUnitName,
        MedicalSpeciality speciality,
        String status
) {
    public AdminDoctorDTO(Doctor doctor) {
        this(
                doctor.getId(),
                doctor.getName(),
                doctor.getClinicUnit() != null ? doctor.getClinicUnit().getId() : null,
                doctor.getClinicUnit() != null ? doctor.getClinicUnit().getName() : null,
                doctor.getSpeciality(),
                Boolean.TRUE.equals(doctor.getActive()) ? "ACTIVE" : "INACTIVE"
        );
    }
}
