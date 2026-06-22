package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.ClinicUnitRepository;
import br.edu.ifg.med_clinica_api.domain.dao.DoctorRepository;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.AdminDoctorDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.entity.User;
import br.edu.ifg.med_clinica_api.domain.dao.UserRepository;
import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;
import br.edu.ifg.med_clinica_api.domain.enums.UserRole;
import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ClinicUnitRepository clinicUnitRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DoctorService(
            DoctorRepository doctorRepository,
            ClinicUnitRepository clinicUnitRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.doctorRepository = doctorRepository;
        this.clinicUnitRepository = clinicUnitRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
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
        var clinicUnit = clinicUnitRepository.findById(data.clinicUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Unidade clinica nao encontrada."));

        if (!Boolean.TRUE.equals(clinicUnit.getActive())) {
            throw new IllegalStateException("Unidade clinica inativa nao pode receber medicos.");
        }

        Doctor doctor = new Doctor(data);
        doctor.setUser(user);
        doctor.updateClinicUnit(clinicUnit);

        return doctorRepository.save(doctor);
    }


    public List<DoctorDetailDTO> findAllDoctors(
            MedicalSpeciality speciality,
            UUID clinicUnitId
    ) {
        List<Doctor> doctors;

        if (clinicUnitId != null && speciality != null) {
            doctors = doctorRepository.findByClinicUnit_IdAndSpeciality(clinicUnitId, speciality);
        } else if (clinicUnitId != null) {
            doctors = doctorRepository.findByClinicUnit_Id(clinicUnitId);
        } else if (speciality != null) {
            doctors = doctorRepository.findBySpeciality(speciality);
        } else {
            doctors = doctorRepository.findAll();
        }

        return doctors.stream()
                .map(DoctorDetailDTO::new)
                .toList();
    }

    public List<AdminDoctorDTO> findAdminDoctors(UUID clinicUnitId) {
        List<Doctor> doctors = clinicUnitId == null
                ? doctorRepository.findAll()
                : doctorRepository.findByClinicUnit_Id(clinicUnitId);

        return doctors.stream()
                .map(AdminDoctorDTO::new)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @AuditAction("ATUALIZAR_MÉDICO")
    public DoctorDetailDTO updateDoctor(UUID id, DoctorUpdateDTO data) {
        var doctor = doctorRepository.getReferenceById(id);

        if (data.clinicUnitId() != null) {
            var clinicUnit = clinicUnitRepository.findById(data.clinicUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Unidade clinica nao encontrada."));

            if (!Boolean.TRUE.equals(clinicUnit.getActive())) {
                throw new IllegalStateException("Unidade clinica inativa nao pode receber medicos.");
            }

            doctor.updateClinicUnit(clinicUnit);
        }

        doctor.updateData(data);

        return new DoctorDetailDTO(doctor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @AuditAction("DELETAR_MÉDICO")
    public void deleteDoctor(UUID id) {
        var doctor = doctorRepository.getReferenceById(id);
        doctor.logicDeletion();
    }

    @PreAuthorize(
            "hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')"
    )
    public DoctorDetailDTO getDoctorById(UUID id) {
        var doctor = doctorRepository.getReferenceById(id);
        return new DoctorDetailDTO(doctor);
    }

}
