package gr.aueb.representation;

import gr.aueb.domain.ZipCode;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ZipCodeMapper {
    public String toRepresentation(ZipCode zipCode) {
        return zipCode != null ? zipCode.getZipCode() : null;
    }

    public ZipCode toEntity(String zipCode) {
        return zipCode != null ? new ZipCode(zipCode) : null;
    }
}


