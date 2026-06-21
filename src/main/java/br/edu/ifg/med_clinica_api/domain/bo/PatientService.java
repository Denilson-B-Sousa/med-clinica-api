package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientListDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import br.edu.ifg.med_clinica_api.domain.entity.Patient;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @AuditAction("REGISTRAR_PACIENTE")
    public PatientDetailDTO registerPatient(PatientRegisterDTO data) {
        User user = createPatientUser(data);
        Patient patient = createPatient(data, user);

        return new PatientDetailDTO(patient);
    }

    private User createPatientUser(PatientRegisterDTO data) {
        User user = new User();
        user.setEmail(data.email());
        user.setRole(UserRole.ROLE_PATIENT);
        user.setPassword(passwordEncoder.encode(data.password()));

        return userRepository.save(user);
    }

    private Patient createPatient(PatientRegisterDTO data, User user) {
        Patient patient = new Patient(data);
        patient.setUser(user);

        return patientRepository.save(patient);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')" )
    public Page<PatientListDTO> listAllPatients(Pageable pagination) {
        return patientRepository.findByActiveTrue(pagination)
                .map(PatientListDTO::new);
    }

    @PreAuthorize(
       "hasRole('ADMIN') || " +
       "@resourceSecurity.isPatientOwner(#id, authentication.name)"
    )
    @Transactional
    @AuditAction("ATUALIZAR_PACIENTE")
    public PatientDetailDTO updatePatient(UUID id, PatientUpdateDTO data) {
        var patient = patientRepository.getReferenceById(id);
        patient.updateData(data);

        return new PatientDetailDTO(patient);
    }

    @PreAuthorize(
            "hasRole('ADMIN') || " +
            "@resourceSecurity.isPatientOwner(#id, authentication.name)"
    )
    @Transactional
    @AuditAction("DELETAR_PACIENTE")
    public void deletePatient(UUID id) {
        var patient = patientRepository.getReferenceById(id);
        patient.logicDeletion();
    }

    @PreAuthorize(
            "hasRole('ADMIN') || " +
            "hasRole('DOCTOR') || " +
            "@resourceSecurity.isPatientOwner(#id, authentication.name)"
    )
    @AuditAction("LISTAR_PACIENTE")
    public PatientDetailDTO getPatientById(UUID id) {
        var patient = patientRepository.getReferenceById(id);
        return new PatientDetailDTO(patient);
    }

}
