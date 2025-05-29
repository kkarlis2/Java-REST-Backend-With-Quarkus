package gr.aueb.representation;

import gr.aueb.domain.TicketZone;
import gr.aueb.representation.TicketZoneRepresentation;
import gr.aueb.persistence.EventRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import jakarta.inject.Inject;
import java.util.List;

@Mapper(componentModel = "jakarta", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {EventMapper.class})
public abstract class TicketZoneMapper {

    @Inject
    EventRepository eventRepository;

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(target = "reservations", ignore = true)
    public abstract TicketZoneRepresentation toRepresentation(TicketZone ticketZone);

    @Mapping(target = "event", ignore = true)
    public abstract TicketZone toEntity(TicketZoneRepresentation representation);

    public abstract List<TicketZoneRepresentation> toRepresentationList(List<TicketZone> ticketZones);


}
