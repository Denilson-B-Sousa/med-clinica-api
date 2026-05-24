package br.edu.ifg.med_clinica_api.controller;

import br.edu.ifg.med_clinica_api.domain.bo.AppointmentService;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentDetailDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/consultas")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentDetailDTO> scheduleAppointment(
            @RequestBody @Valid AppointmentRegisterDTO data,
            UriComponentsBuilder uriBuilder
    ) {
        var appointmentDetail = appointmentService.scheduleAppointment(data);

        var uri = uriBuilder.path("/consultas/{id}")
                .buildAndExpand(appointmentDetail.id())
                .toUri();

        return ResponseEntity.created(uri).body(appointmentDetail);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentDetailDTO>> listAllAppointmentsByStatus(
            @RequestParam AppointmentStatus status,
            Pageable pagination
    ) {
        var appointments = appointmentService.listAllAppointmentsByStatus(status, pagination);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDetailDTO> getAppointmentById(@PathVariable UUID id) {
        var appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDetailDTO> updateAppointment(
            @PathVariable UUID id,
            @RequestBody @Valid AppointmentUpdateDTO data
    ) {
        var updatedAppointment = appointmentService.updateAppointment(id, data);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable UUID id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}