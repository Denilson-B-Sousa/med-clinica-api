package br.edu.ifg.med_clinica_api.domain.dao;

import br.edu.ifg.med_clinica_api.domain.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Page<Patient> findByActiveTrue(Pageable pagination);

    Optional<Patient> findByUserEmail(String email);
}
