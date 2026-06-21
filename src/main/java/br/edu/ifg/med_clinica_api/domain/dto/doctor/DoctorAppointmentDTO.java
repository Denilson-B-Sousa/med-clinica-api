package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitSummaryDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Doctor;

import java.util.UUID;

public record DoctorAppointmentDTO(
        UUID id,
        String name,
        String crm,
        String speciality,
        ClinicUnitSummaryDTO clinicUnit
) {
    public DoctorAppointmentDTO(Doctor doctor) {
        this(
                doctor.getId(),
                doctor.getName(),
                doctor.getCrm(),
                doctor.getSpeciality().name(),
                doctor.getClinicUnit() != null ? new ClinicUnitSummaryDTO(doctor.getClinicUnit()) : null
        );
    }
}
