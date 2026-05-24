package br.edu.ifg.med_clinica_api.infra.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra uma ação de auditoria realizada no sistema.
     * Obtém o usuário autenticado a partir do contexto
     * de segurança do Spring Security e persiste o registro
     * contendo a ação executada, o usuário responsável e a data/hora da operação
     * @param action
     */
    public void register(String action) {
        Authentication authentication =
            SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = "anonymous";

        if(authentication != null
            && authentication.isAuthenticated()) {

            username = authentication.getName();
        }

        LocalDateTime executedAt = LocalDateTime.now();

        AuditLog log = new AuditLog(
                action,
                username,
                executedAt
        );

        repository.save(log);
    }
}
