package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.AppointmentService;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AdminAppointmentDTO;
import br.edu.ifg.med_clinica_api.domain.dto.pages.SimplePageResponseDTO;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentPeriod;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<SimplePageResponseDTO<AdminAppointmentDTO>> findAppointments(
            @RequestParam(required = false) UUID clinicUnitId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentPeriod period,
            @PageableDefault(size = 10, sort = "scheduleAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        var appointments = appointmentService.findAdminAppointments(
                clinicUnitId,
                doctorId,
                patientName,
                status,
                date,
                period,
                pageable
        );

        return ResponseEntity.ok(appointments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointmentPermanently(id);
        return ResponseEntity.noContent().build();
    }
}
