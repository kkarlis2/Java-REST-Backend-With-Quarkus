package gr.aueb.representation;

import gr.aueb.domain.Address;
import gr.aueb.domain.ZipCode;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "jakarta", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {ZipCodeMapper.class})
public abstract class AddressMapper {

    public abstract AddressRepresentation toRepresentation(Address address);

    public Address toEntity(AddressRepresentation representation) {
        if (representation == null) {
            return null;
        }
        return new Address(
                representation.street,
                representation.number,
                new ZipCode(representation.zipCode)
        );
    }
}
