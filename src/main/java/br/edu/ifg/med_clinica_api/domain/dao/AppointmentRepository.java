package br.edu.ifg.med_clinica_api.domain.dao;

import br.edu.ifg.med_clinica_api.domain.entity.Appointment;
import br.edu.ifg.med_clinica_api.domain.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pagination);

    @EntityGraph(attributePaths = {"patient", "doctor", "clinicUnit"})
    Page<Appointment> findAll(Specification<Appointment> specification, Pageable pageable);

    List<Appointment> findByDoctor_IdAndStatusAndScheduleAtBefore(
            UUID doctorId,
            AppointmentStatus status,
            LocalDateTime scheduleAt
    );

    /**
     * Busca o histórico de consultas de um paciente com filtros opcionais.
     * EntityGraph -> O @EntityGraph serve para otimizar a busca.
     * para carregar o médico junto com cada consulta. Isso evita que o Hibernate
     * faça várias consultas extras ao banco quando o DTO acessar appointment.getDoctor()
     * Regras:
     * - Sempre filtra pelo paciente autenticado.
     * - Se status for informado, filtra pelo status da consulta.
     * - Se search for informado, busca nos dados do médico:
     *   nome, CRM, especialidade, cidade e rua.
     * - A paginação e ordenação são aplicadas pelo Pageable.
     */
        @EntityGraph(attributePaths = {"doctor", "doctor.clinicUnit", "clinicUnit"})
        @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.doctor d
            LEFT JOIN a.clinicUnit c
            WHERE a.patient.id = :patientId
                AND (:status IS NULL OR a.status = :status)
            """)
        Page<Appointment> findPatientHistoryWithoutSearch(
                @Param("patientId") UUID patientId,
                @Param("status") AppointmentStatus status,
                Pageable pageable
        );

        @EntityGraph(attributePaths = {"doctor", "doctor.clinicUnit", "clinicUnit"})
        @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.doctor d
            LEFT JOIN a.clinicUnit c
            WHERE a.patient.id = :patientId
                AND (:status IS NULL OR a.status = :status)
                AND (
                    LOWER(d.name) LIKE :search ESCAPE '\\'
                    OR LOWER(d.crm) LIKE :search ESCAPE '\\'
                    OR LOWER(CAST(d.speciality AS string)) LIKE :search ESCAPE '\\'
                    OR LOWER(c.name) LIKE :search ESCAPE '\\'
                    OR LOWER(c.address.city) LIKE :search ESCAPE '\\'
                    OR LOWER(c.address.street) LIKE :search ESCAPE '\\'
                )
            """)
        Page<Appointment> findPatientHistoryWithSearch(
                @Param("patientId") UUID patientId,
                @Param("status") AppointmentStatus status,
                @Param("search") String search,
                Pageable pageable
        );
    }
