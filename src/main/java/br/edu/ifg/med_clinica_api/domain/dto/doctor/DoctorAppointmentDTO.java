package br.edu.ifg.med_clinica_api.domain.dto.doctor;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressAppointmentDTO;
import br.edu.ifg.med_clinica_api.domain.entity.Doctor;

import java.util.UUID;

public record DoctorAppointmentDTO(
        UUID id,
        String name,
        String crm,
        String speciality,
        AddressAppointmentDTO address
) {
    public DoctorAppointmentDTO(Doctor doctor) {
        this(
                doctor.getId(),
                doctor.getName(),
                doctor.getCrm(),
                doctor.getSpeciality().name(),
                new AddressAppointmentDTO(doctor.getAddress())
        );
    }
}
