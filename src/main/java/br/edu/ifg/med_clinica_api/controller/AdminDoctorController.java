package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.DoctorService;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.AdminDoctorDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.AdminDoctorUserDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorUpdateDTO;
import br.edu.ifg.med_clinica_api.domain.dto.pages.SimplePageResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/doctors")
public class AdminDoctorController {

    private final DoctorService doctorService;

    public AdminDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<AdminDoctorDTO>> findDoctors(
            @RequestParam(required = false) UUID clinicUnitId
    ) {
        return ResponseEntity.ok(doctorService.findAdminDoctors(clinicUnitId));
    }

    @GetMapping("/users")
    public ResponseEntity<SimplePageResponseDTO<AdminDoctorUserDTO>> findDoctorUsers(
            @RequestParam(required = false) UUID clinicUnitId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(doctorService.findAdminDoctorUsers(clinicUnitId, pageable));
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorDetailDTO> update(
            @PathVariable UUID doctorId,
            @RequestBody @Valid DoctorUpdateDTO data
    ) {
        return ResponseEntity.ok(doctorService.updateDoctor(doctorId, data));
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> delete(@PathVariable UUID doctorId) {
        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }
}
