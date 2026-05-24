package br.edu.ifg.med_clinica_api.infra.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String action;
    private String username;
    private LocalDateTime executedAt;

    public AuditLog(String action, String username, LocalDateTime executedAt) {
        this.action = action;
        this.username = username;
        this.executedAt = executedAt;
    }
}
