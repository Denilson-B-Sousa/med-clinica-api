package br.edu.ifg.med_clinica_api.model.entity;

import br.edu.ifg.med_clinica_api.model.enums.AppointmentStatus;
import br.edu.ifg.med_clinica_api.model.dto.appointment.AppointmentRegisterDTO;
import br.edu.ifg.med_clinica_api.model.dto.appointment.AppointmentUpdateDTO;
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

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "duration_in-minutes", nullable = false)
    private Integer durationInMinutes;

    public Appointment(UUID id, Patient patient, Doctor doctor, LocalDateTime scheduledAt, AppointmentStatus status, Integer durationInMinutes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.durationInMinutes = 60;
    }

    public Appointment(Patient patient, Doctor doctor, AppointmentRegisterDTO data) {
        this.patient = patient;
        this.doctor = doctor;
        this.scheduledAt = data.scheduleAt();
        this.durationInMinutes = data.durationInMinutes() != null ? data.durationInMinutes() : 30;
        this.status = AppointmentStatus.SCHEDULED;

    }

    public void updateData(AppointmentUpdateDTO data) {

    }

    public void cancel() {
       this.status = AppointmentStatus.CANCELED;
    }
}
