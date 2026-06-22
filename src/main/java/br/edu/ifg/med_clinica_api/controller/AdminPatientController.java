package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.PatientService;
import br.edu.ifg.med_clinica_api.domain.dto.patient.AdminPatientDTO;
import br.edu.ifg.med_clinica_api.domain.dto.pages.SimplePageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin/patients")
public class AdminPatientController {

    private final PatientService patientService;

    public AdminPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<SimplePageResponseDTO<AdminPatientDTO>> findPatients(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(new SimplePageResponseDTO<>(patientService.listAdminPatients(pageable)));
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> delete(@PathVariable UUID patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.noContent().build();
    }
}
