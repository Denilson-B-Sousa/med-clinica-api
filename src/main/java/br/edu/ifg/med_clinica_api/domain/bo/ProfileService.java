package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientUpdateDTO;
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

    public ProfileService(UserRepository userRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Object getMyProfile(String email) {

        User user = userRepository.findByEmail(email);

        if (user.getRole().equals(UserRole.ROLE_PATIENT)) {
            var patient = patientRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            return new PatientDetailDTO(patient);
        }

        if (user.getRole().equals(UserRole.ROLE_DOCTOR)) {
            var doctor = doctorRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            return new DoctorDetailDTO(doctor);
        }

        throw new RuntimeException("Tipo de usuário não suportado.");
    }

    @Transactional
    public Object updateMyProfile(
            String email,
            Object updateData) {

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

        throw new RuntimeException("Tipo de usuário não suportado.");
    }

    @Transactional
    public void changePassword(
            String email,
            String currentPassword,
            String newPassword) {

        User user = userRepository.findByEmail(email);

        boolean matches = passwordEncoder.matches(
                currentPassword,
                user.getPassword()
        );

        if (!matches) {
            throw new IllegalArgumentException("Senha atual inválida.");
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

            patient.logicDeletion();
            return;
        }

        if (user.getRole().equals(UserRole.ROLE_DOCTOR)) {

            var doctor = doctorRepository.findByUserEmail(email)
                    .orElseThrow(EntityNotFoundException::new);

            doctor.logicDeletion();
            return;
        }

        throw new RuntimeException("Tipo de usuário não suportado.");
    }
}
