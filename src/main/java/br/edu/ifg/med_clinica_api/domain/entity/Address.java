package br.edu.ifg.med_clinica_api.domain.entity;

import br.edu.ifg.med_clinica_api.domain.dto.address.AddressData;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Embeddable
/* @Embeddable ->
    Address não será uma tabela própria no banco
    mas poderá ser embutida dentro de outra
    entidade no caso doctor e patient
 */
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
