package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.DoctorService;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.AdminDoctorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
