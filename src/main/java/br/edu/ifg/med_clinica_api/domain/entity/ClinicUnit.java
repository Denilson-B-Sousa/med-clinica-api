package br.edu.ifg.med_clinica_api.domain.entity;

import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.clinicunit.ClinicUnitUpdateDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "clinic_units")
@Entity
@Getter
@NoArgsConstructor
public class ClinicUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ClinicUnit(ClinicUnitRegisterDTO data) {
        this.name = data.name();
        this.address = new Address(data.address());
        this.phone = data.phone();
        this.active = true;
    }

    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateData(ClinicUnitUpdateDTO data) {
        if (data.name() != null) {
            this.name = data.name();
        }

        if (data.address() != null) {
            this.address = new Address(data.address());
        }

        if (data.phone() != null) {
            this.phone = data.phone();
        }
    }

    public void logicDeletion() {
        this.active = false;
    }
}
