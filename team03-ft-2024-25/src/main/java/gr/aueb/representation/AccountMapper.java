package gr.aueb.representation;

import gr.aueb.domain.Account;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class AccountMapper {
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    public abstract Account toEntity(AccountRepresentation representation);

    public String toRepresentation(Account account) {
        return account != null ? account.getUsername() : null;
    }
}





