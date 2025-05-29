package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.representation.OrganizerRepresentation;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "jakarta",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {AccountMapper.class,AddressMapper.class,ZipCodeMapper.class, EmailMapper.class})
public abstract class OrganizerMapper {

    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "password", source = "account.password")
    @Mapping(target = "email",  source="email")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "number", source = "address.number")
    @Mapping(target = "zipCode", source="address.zipCode")
    @Mapping(target = "events", ignore = true)  // Αγνοούμε τα events
    public abstract OrganizerRepresentation toRepresentation(Organizer organizer);

    @Mapping(target = "account", expression = "java(new Account(representation.getUsername(), representation.getPassword()))")
    @Mapping(target = "address", expression = "java(createAddress(representation))")
    @Mapping(target = "events", ignore = true)
    public abstract Organizer toEntity(OrganizerRepresentation representation);

    public abstract List<OrganizerRepresentation> toRepresentationList(List<Organizer> organizers);

    protected Address createAddress(OrganizerRepresentation representation) {
        if (representation.getStreet() == null || representation.getNumber() == null || representation.getZipCode() == null) {
            return null;
        }

        // Δημιουργούμε απευθείας το ZipCode object
        ZipCode zipCode = new ZipCode(representation.getZipCode());
        return new Address(representation.getStreet(), representation.getNumber(), zipCode);
    }
}