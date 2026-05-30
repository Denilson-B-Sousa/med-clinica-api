package br.edu.ifg.med_clinica_api.infra.security;

import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("resourceSecurity")
public class ResourceSecurity {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public ResourceSecurity(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public boolean isPatientOwner(UUID patientId, String email) {

        return patientRepository.findById(patientId)
                .map(patient ->
                             patient.getUser().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isDoctorOwner(UUID doctorId, String email) {

        return doctorRepository.findById(doctorId)
                .map(doctor ->
                             doctor.getUser().getEmail().equals(email))
                .orElse(false);
    }
}
