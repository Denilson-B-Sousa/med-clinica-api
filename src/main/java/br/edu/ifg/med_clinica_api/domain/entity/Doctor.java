package br.edu.ifg.med_clinica_api.domain.entity;

import br.edu.ifg.med_clinica_api.domain.enums.MedicalSpeciality;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorRegisterDTO;
import br.edu.ifg.med_clinica_api.domain.dto.doctor.DoctorUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "doctors")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String crm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalSpeciality speciality;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private Boolean active;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Doctor(DoctorRegisterDTO data) {
        this.active = true;
        this.name = data.name();
        this.cpf = data.cpf();
        this.email = data.email();
        this.phone = data.phone();
        this.crm = data.crm();
        this.speciality = data.speciality();
        this.address = new Address(data.address());
    }

    public void updateData(DoctorUpdateDTO data) {
        if (data.name() != null) {
            this.name = data.name();
        }

        if(data.phone() != null) {
            this.phone = data.phone();
        }
    }

    public void logicDeletion() {
        this.active = false;
    }

}
