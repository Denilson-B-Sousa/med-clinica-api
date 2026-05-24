package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorListDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @AuditAction("CRIAR_MÉDICO")
    public DoctorDetailDTO registerDoctor(DoctorRegisterDTO data) {
        User user = createDoctorUser(data);
        Doctor doctor = createDoctor(data, user);

        return new DoctorDetailDTO(doctor);
    }

    private User createDoctorUser(DoctorRegisterDTO data) {
        User user = new User();
        user.setEmail(data.email());
        user.setRole(UserRole.ROLE_DOCTOR);
        user.setPassword(passwordEncoder.encode(data.password()));

        return userRepository.save(user);
    }

    private Doctor createDoctor(DoctorRegisterDTO data, User user) {
        Doctor doctor = new Doctor(data);
        doctor.setUser(user);

        return doctorRepository.save(doctor);
    }

    public Page<DoctorListDTO> listAllDoctor(Pageable pagination) {
        return doctorRepository.findByActiveTrue(pagination)
                .map(DoctorListDTO::new);
    }

    @Transactional
    @AuditAction("ATUALIZAR_MÉDICO")
    public DoctorDetailDTO updateDoctor(UUID id, DoctorUpdateDTO data) {
        var doctor = doctorRepository.getReferenceById(id);
        doctor.updateData(data);

        return new DoctorDetailDTO(doctor);
    }

    @Transactional
    @AuditAction("DELETAR_MÉDICO")
    public void deleteDoctor(UUID id) {
        var doctor = doctorRepository.getReferenceById(id);
        doctor.logicDeletion();
    }

    public DoctorDetailDTO getDoctorById(UUID id) {
        var doctor = doctorRepository.getReferenceById(id);
        return new DoctorDetailDTO(doctor);
    }

}
