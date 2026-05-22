package br.edu.ifg.med_clinica_api.model.entity;

import br.edu.ifg.med_clinica_api.model.dto.address.AddressData;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Address {
    private String street;
    private String number;
    private String district;
    private String state;
    private String city;
    private String zipcode;

    public Address(AddressData data) {
        this.street = data.street();
        this.number = data.number();
        this.district = data.district();
        this.state = data.state();
        this.city = data.city();
        this.zipcode = data.zipcode();
    }

}
