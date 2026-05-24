package br.edu.ifg.med_clinica_api.domain.entity;

import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.patient.PatientUpdateDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Data
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String cpf;
    private String email;
    private String phone;

    @Embedded
    private Address address;
    private LocalDate birthDate;
    private String gender;
    private Boolean active;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Patient(PatientRegisterDTO data) {
        this.active = true;
        this.name = data.name();
        this.cpf = data.cpf();
        this.email = data.email();
        this.phone = data.phone();
        this.birthDate = data.birthDate();
        this.gender = data.gender();
        this.address = new Address(data.address());
    }


    public void logicDeletion() {
        this.active = false;
    }
    public void updateData(PatientUpdateDTO data) {

        if (data.name() != null) {
            this.name = data.name();
        }

        if(data.phone() != null) {
            this.phone = data.phone();
        }
    }
}
