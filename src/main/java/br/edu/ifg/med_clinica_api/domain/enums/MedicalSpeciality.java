package br.edu.ifg.med_clinica_api.domain.enums;

public enum MedicalSpeciality {
    ORTOPEDIA("Ortopedia"),
    CARDIOLOGIA("Cardiologia"),
    DERMATOLOGIA("Dermatologia"),
    ENDOCRINOLOGIA("Endocrinologia"),
    GASTROENTEROLOGIA("Gastroenterologia"),
    GERIATRIA("Geriatria"),
    HEMATOLOGIA("Hematologia"),
    INFECTOLOGIA("Infectologia"),
    NEUROLOGIA("Neurologia"),
    OFTALMOLOGIA("Oftalmologia"),
    ONCOLOGIA("Oncologia"),
    PEDIATRIA("Pediatria"),
    PNEUMOLOGIA("Pneumologia"),
    GINECOLOGIA("Ginecologia"),
    REUMATOLOGIA("Reumatologia"),
    UROLOGIA("Urologia"),
    PSICOLOGIA("Psicologia"),
    PSIQUIATRIA("Psiquiatria");

    private final String descricao;

    MedicalSpeciality(String descricao) {
        this.descricao = descricao;
    }
}
