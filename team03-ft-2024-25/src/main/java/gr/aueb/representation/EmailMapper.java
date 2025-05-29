package gr.aueb.representation;

import gr.aueb.domain.Email;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EmailMapper {
    public String toRepresentation(Email email) {
        return email != null ? email.getEmail() : null;
    }

    public Email toEntity(String email) {
        return email != null ? new Email(email) : null;
    }
}

