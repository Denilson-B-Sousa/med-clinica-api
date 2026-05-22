package br.edu.ifg.med_clinica_api.model.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressData(

        @NotBlank
        String street,

        @NotBlank
        String number,

        @NotBlank
        String district,

        @NotBlank
        String city,

        @NotBlank
        String state,

        @NotBlank
        @Pattern(regexp = "\\d{8}")
        String zipcode

) {
}
