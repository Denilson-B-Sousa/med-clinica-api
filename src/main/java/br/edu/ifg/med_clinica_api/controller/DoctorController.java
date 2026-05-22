package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.model.bo.DoctorService;
import br.edu.ifg.med_clinica_api.model.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.model.dto.doctor.DoctorListDTO;
import br.edu.ifg.med_clinica_api.model.dto.doctor.DoctorRegisterDTO;
import br.edu.ifg.med_clinica_api.model.dto.doctor.DoctorUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("medicos")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorDetailDTO> register (
            @RequestBody @Valid DoctorRegisterDTO data,
            UriComponentsBuilder uriBuilder
    ) {

        var doctor = doctorService.registerDoctor(data);

        var uri = uriBuilder
                .path("/medicos/{id}")
                .buildAndExpand(doctor.id())
                .toUri();

        return ResponseEntity.created(uri).body(doctor);
    }

    @GetMapping
    public ResponseEntity<Page<DoctorListDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(doctorService.listAllDoctor(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDetailDTO> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDetailDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid DoctorUpdateDTO data) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
