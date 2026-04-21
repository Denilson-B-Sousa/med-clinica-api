package br.edu.ifg.med_clinica_api.domain.patient;

import br.edu.ifg.med_clinica_api.domain.patient.dto.PatientDetailDTO;
import br.edu.ifg.med_clinica_api.domain.patient.dto.PatientListDTO;
import br.edu.ifg.med_clinica_api.domain.patient.dto.PatientRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.patient.dto.PatientUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.user.User;
import br.edu.ifg.med_clinica_api.domain.user.UserRepository;
import br.edu.ifg.med_clinica_api.domain.user.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<PatientListDTO> listAllPatients(Pageable pagination) {
        return patientRepository.findByActiveTrue(pagination)
                .map(PatientListDTO::new);
    }

    @Transactional
    public PatientDetailDTO updatePatient(UUID id, PatientUpdateDTO data) {
        var patient = patientRepository.getReferenceById(id);
        patient.updateData(data);

        return new PatientDetailDTO(patient);
    }

    @Transactional
    public void deletePatient(UUID id) {
        var patient = patientRepository.getReferenceById(id);
        patient.logicDeletion();
    }

    public PatientDetailDTO getPatientById(UUID id) {
        var patient = patientRepository.getReferenceById(id);
        return new PatientDetailDTO(patient);
    }

}
