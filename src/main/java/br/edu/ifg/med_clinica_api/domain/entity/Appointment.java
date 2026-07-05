package br.edu.ifg.med_clinica_api.domain.entity;

import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.appointment.AppointmentUpdateDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "appointments")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_unit_id")
    private ClinicUnit clinicUnit;

    @Column(name = "schedule_at", nullable = false)
    private LocalDateTime scheduleAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "duration_in_minutes", nullable = false)
    private Integer durationInMinutes;

    @Column(name = "attendance_confirmed", nullable = false, columnDefinition = "boolean default false")
    private Boolean attendanceConfirmed = false;

    @Column(name = "attendance_confirmed_at")
    private LocalDateTime attendanceConfirmedAt;

    public Appointment(
            UUID id,
            Patient patient,
            Doctor doctor,
            ClinicUnit clinicUnit,
            LocalDateTime scheduleAt,
            AppointmentStatus status,
            Integer durationInMinutes,
            Boolean attendanceConfirmed,
            LocalDateTime attendanceConfirmedAt
    ) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.clinicUnit = clinicUnit;
        this.scheduleAt = scheduleAt;
        this.status = status;
        this.durationInMinutes = durationInMinutes;
        this.attendanceConfirmed = attendanceConfirmed;
        this.attendanceConfirmedAt = attendanceConfirmedAt;
    }

    public Appointment(Patient patient, Doctor doctor, ClinicUnit clinicUnit, AppointmentRegisterDTO data) {
        this.patient = patient;
        this.doctor = doctor;
        this.clinicUnit = clinicUnit;
        this.scheduleAt = data.scheduleAt();
        this.durationInMinutes = data.durationInMinutes() != null ? data.durationInMinutes() : 30;
        this.status = AppointmentStatus.SCHEDULED;
        this.attendanceConfirmed = false;

    }

    public void updateData(AppointmentUpdateDTO data, ClinicUnit clinicUnit) {
        if (data == null || data.scheduleAt() == null) {
            throw new IllegalArgumentException("A nova data e hora da consulta sao obrigatorias.");
        }

        if (this.status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Somente consultas agendadas podem ser atualizadas.");
        }

        if (Boolean.TRUE.equals(this.attendanceConfirmed)) {
            throw new IllegalStateException("Consulta com presenca confirmada nao pode ser atualizada.");
        }

        this.scheduleAt = data.scheduleAt();
        this.clinicUnit = clinicUnit;
    }

    public void cancel() {
        if (this.status == AppointmentStatus.CANCELED) {
            throw new IllegalStateException("A consulta ja esta cancelada.");
        }

        if (this.status == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Nao e possivel cancelar uma consulta ja concluida.");
        }

        if (Boolean.TRUE.equals(this.attendanceConfirmed)) {
            throw new IllegalStateException("Consulta com presenca confirmada nao pode ser cancelada.");
        }

        this.status = AppointmentStatus.CANCELED;
    }

    public void confirmAttendance() {
        if (this.status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Somente consultas agendadas podem ter presenca confirmada.");
        }

        if (Boolean.TRUE.equals(this.attendanceConfirmed)) {
            return;
        }

        this.attendanceConfirmed = true;
        this.attendanceConfirmedAt = LocalDateTime.now(java.time.ZoneOffset.UTC);
        this.status = AppointmentStatus.CONFIRMED;
    }
}
