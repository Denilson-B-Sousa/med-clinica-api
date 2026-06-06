package br.edu.ifg.med_clinica_api.domain.dto.address;

import br.edu.ifg.med_clinica_api.domain.entity.Address;

public record AddressAppointmentDTO(
        String street,
        String number,
        String city
) {
    public AddressAppointmentDTO(Address address) {
        this(
                address.getStreet(),
                address.getNumber(),
                address.getCity()
        );
    }

}
