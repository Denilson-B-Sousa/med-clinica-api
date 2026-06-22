package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dao.PatientRepository;
import br.edu.ifg.med_clinica_api.domain.dto.user.UserStatusDTO;
import br.edu.ifg.med_clinica_api.domain.dto.user.UserStatusUpdateDTO;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public UserService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository
    ) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @AuditAction("ATUALIZAR_STATUS_USUARIO")
    public UserStatusDTO updateStatus(UUID id, UserStatusUpdateDTO data) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado."));
        var active = data.activeValue();

        user.setActive(active);
        patientRepository.findByUserId(id)
                .ifPresent(patient -> patient.setActive(active));
        doctorRepository.findByUserId(id)
                .ifPresent(doctor -> doctor.updateStatus(active));

        return new UserStatusDTO(user);
    }
}
