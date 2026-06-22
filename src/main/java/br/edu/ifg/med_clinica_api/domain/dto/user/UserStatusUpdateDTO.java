package br.edu.ifg.med_clinica_api.domain.dto.user;

public record UserStatusUpdateDTO(
        Boolean active,
        String status
) {
    public Boolean activeValue() {
        if (active != null) {
            return active;
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Informe active ou status.");
        }

        return switch (status.trim().toUpperCase()) {
            case "ACTIVE", "ATIVO", "ENABLED" -> true;
            case "INACTIVE", "INATIVO", "DISABLED" -> false;
            default -> throw new IllegalArgumentException("Status invalido.");
        };
    }
}
