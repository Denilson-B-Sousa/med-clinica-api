package br.edu.ifg.med_clinica_api.domain.bo;

import br.edu.ifg.med_clinica_api.domain.dao.ClinicUnitRepository;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitSummaryDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.entity.ClinicUnit;
import br.edu.ifg.med_clinica_api.infra.audit.AuditAction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClinicUnitService {

    private final ClinicUnitRepository clinicUnitRepository;

    public ClinicUnitService(ClinicUnitRepository clinicUnitRepository) {
        this.clinicUnitRepository = clinicUnitRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @AuditAction("CRIAR_UNIDADE_CLINICA")
    public ClinicUnitDetailDTO registerClinicUnit(ClinicUnitRegisterDTO data) {
        var clinicUnit = clinicUnitRepository.save(new ClinicUnit(data));
        return new ClinicUnitDetailDTO(clinicUnit);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public List<ClinicUnitSummaryDTO> findAllActive() {
        return clinicUnitRepository.findByActiveTrue()
                .stream()
                .map(ClinicUnitSummaryDTO::new)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ClinicUnitDetailDTO getClinicUnitById(UUID id) {
        var clinicUnit = clinicUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade clinica nao encontrada."));

        return new ClinicUnitDetailDTO(clinicUnit);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @AuditAction("ATUALIZAR_UNIDADE_CLINICA")
    public ClinicUnitDetailDTO updateClinicUnit(UUID id, ClinicUnitUpdateDTO data) {
        var clinicUnit = clinicUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade clinica nao encontrada."));

        clinicUnit.updateData(data);

        return new ClinicUnitDetailDTO(clinicUnit);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @AuditAction("DELETAR_UNIDADE_CLINICA")
    public void deleteClinicUnit(UUID id) {
        var clinicUnit = clinicUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade clinica nao encontrada."));

        clinicUnit.logicDeletion();
    }
}
