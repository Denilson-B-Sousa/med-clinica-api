package br.edu.ifg.med_clinica_api.domain.dao;

import br.edu.ifg.med_clinica_api.domain.entity.Doctor;
import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    
    List<Doctor> findBySpeciality(MedicalSpeciality speciality);

    List<Doctor> findByClinicUnit_Id(UUID clinicUnitId);

    List<Doctor> findByClinicUnit_IdAndSpeciality(UUID clinicUnitId, MedicalSpeciality speciality);

    Optional<Doctor> findByUserEmail(String email);
}
