package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.dto.user.UserProfileDTO;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileDTO getMyProfile(String email) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("Usuario nao encontrado.");
        }

        if (user.getRole().equals(UserRole.ROLE_PATIENT)) {
            var patient = patientRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            return new UserProfileDTO(
                    patient.getId(),
                    user.getId(),
                    patient.getId(),
                    null,
                    patient.getName(),
                    patient.getEmail(),
                    user.getRole(),
                    new PatientDetailDTO(patient)
            );
        }

        if (user.getRole().equals(UserRole.ROLE_DOCTOR)) {
            var doctor = doctorRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            return new UserProfileDTO(
                    doctor.getId(),
                    user.getId(),
                    null,
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getEmail(),
                    user.getRole(),
                    new DoctorDetailDTO(doctor)
            );
        }

        if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
            return new UserProfileDTO(
                    user.getId(),
                    user.getId(),
                    null,
                    null,
                    null,
                    user.getEmail(),
                    user.getRole(),
                    null
            );
        }

        throw new RuntimeException("Tipo de usuario nao suportado.");
    }

    @Transactional
    public Object updateMyProfile(
            String email,
            Object updateData
    ) {

        User user = userRepository.findByEmail(email);

        if (user.getRole().equals(UserRole.ROLE_PATIENT)) {

            var patient = patientRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            patient.updateData((PatientUpdateDTO) updateData);

            return new PatientDetailDTO(patient);
        }

        if (user.getRole().equals(UserRole.ROLE_DOCTOR)) {

            var doctor = doctorRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            doctor.updateData((DoctorUpdateDTO) updateData);

            return new DoctorDetailDTO(doctor);
        }

        throw new RuntimeException("Tipo de usuario nao suportado.");
    }

    @Transactional
    public void changePassword(
            String email,
            String currentPassword,
            String newPassword
    ) {

        User user = userRepository.findByEmail(email);

        boolean matches = passwordEncoder.matches(
                currentPassword,
                user.getPassword()
        );

        if (!matches) {
            throw new IllegalArgumentException("Senha atual invalida.");
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );
    }

    @Transactional
    public void deactivateMyAccount(String email) {

        User user = userRepository.findByEmail(email);

        if (user.getRole().equals(UserRole.ROLE_PATIENT)) {

            var patient = patientRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            user.setActive(false);
            patient.logicDeletion();
            return;
        }

        throw new RuntimeException("Somente pacientes podem excluir o proprio perfil.");
    }
}
