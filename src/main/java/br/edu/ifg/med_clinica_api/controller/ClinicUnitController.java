package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.ClinicUnitService;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitSummaryDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/unidades-clinica")
public class ClinicUnitController {

    private final ClinicUnitService clinicUnitService;

    public ClinicUnitController(ClinicUnitService clinicUnitService) {
        this.clinicUnitService = clinicUnitService;
    }

    @PostMapping
    public ResponseEntity<ClinicUnitDetailDTO> register(
            @RequestBody @Valid ClinicUnitRegisterDTO data,
            UriComponentsBuilder uriBuilder
    ) {
        var clinicUnit = clinicUnitService.registerClinicUnit(data);

        var uri = uriBuilder
                .path("/unidades-clinica/{id}")
                .buildAndExpand(clinicUnit.id())
                .toUri();

        return ResponseEntity.created(uri).body(clinicUnit);
    }

    @GetMapping
    public List<ClinicUnitSummaryDTO> findAll() {
        return clinicUnitService.findAllActive();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicUnitDetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(clinicUnitService.getClinicUnitById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicUnitDetailDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid ClinicUnitUpdateDTO data
    ) {
        return ResponseEntity.ok(clinicUnitService.updateClinicUnit(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clinicUnitService.deleteClinicUnit(id);
        return ResponseEntity.noContent().build();
    }
}
