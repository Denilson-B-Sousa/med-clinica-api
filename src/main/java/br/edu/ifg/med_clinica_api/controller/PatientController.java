package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.PatientService;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientListDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController()
@RequestMapping("pacientes")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientDetailDTO> register (
            @RequestBody @Valid PatientRegisterDTO data,
            UriComponentsBuilder uriBuilder
            ) {

        var patient = patientService.registerPatient(data);

        var uri = uriBuilder
                .path("/pacientes/{id}")
                .buildAndExpand(patient.id())
                .toUri();

        return ResponseEntity.created(uri).body(patient);
    }

    @GetMapping
    public ResponseEntity<Page<PatientListDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(patientService.listAllPatients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDetailDTO> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDetailDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid PatientUpdateDTO data) {
        return ResponseEntity.ok(patientService.updatePatient(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
