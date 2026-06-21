package br.edu.ifg.med_clinica_api.domain.dao;

import br.edu.ifg.med_clinica_api.domain.entity.ClinicUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClinicUnitRepository extends JpaRepository<ClinicUnit, UUID> {
    List<ClinicUnit> findByActiveTrue();
}
