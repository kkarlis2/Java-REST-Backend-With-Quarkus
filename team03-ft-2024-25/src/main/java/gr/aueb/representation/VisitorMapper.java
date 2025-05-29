package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.representation.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Mapper(componentModel = "jakarta",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {AccountMapper.class, EmailMapper.class, ReservationMapper.class})
public abstract class VisitorMapper {

    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "password", source = "account.password")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "reservations", source = "reservations")
    public abstract VisitorRepresentation toRepresentation(Visitor visitor);

    @Mapping(target = "account", expression = "java(new Account(representation.getUsername(), representation.getPassword()))")
    @Mapping(target = "reservations", ignore = true)
    public abstract Visitor toEntity(VisitorRepresentation representation);

    public abstract List<VisitorRepresentation> toRepresentationList(List<Visitor> visitors);

    protected Set<ReservationRepresentation> reservationSetToReservationRepresentationSet(Set<Reservation> set) {
        if (set == null) {
            return null;
        }
        return new HashSet<>(set.stream()
                .map(this::reservationToReservationRepresentation)
                .collect(Collectors.toList()));
    }

    protected abstract ReservationRepresentation reservationToReservationRepresentation(Reservation reservation);
}