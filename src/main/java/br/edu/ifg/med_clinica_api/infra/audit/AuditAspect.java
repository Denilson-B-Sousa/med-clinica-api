package br.edu.ifg.med_clinica_api.infra.audit;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     *Executa o registro de auditoria após a conclusão
     * bem-sucedida de um médotodo anotado com @AuditAction
     *
     * @param auditAction anotação contendo a ação auditada
     */
    @AfterReturning("@annotation(auditAction)")
    public void audit(AuditAction auditAction) {
        auditService.register(auditAction.value());
    }
}
