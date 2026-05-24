package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientDetailDTO;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public UserService(UserRepository userRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public Object getAuthenticatedUser(String email) {
        User user = userRepository.findByEmail(email);

        if (user.getRole().equals(UserRole.ROLE_PATIENT)) {
            var patient = patientRepository.findByUserEmail(email)
                .orElseThrow(EntityNotFoundException::new);

            return new PatientDetailDTO(patient);
        }

        if(user.getRole().equals(UserRole.ROLE_DOCTOR)) {
            var doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(EntityNotFoundException::new);

            return new DoctorDetailDTO(doctor);
        }

        throw new RuntimeException("Tipo de usuário não suportado.");
    }
}
