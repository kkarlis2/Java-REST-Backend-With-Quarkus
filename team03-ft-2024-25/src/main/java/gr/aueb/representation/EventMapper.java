package gr.aueb.representation;


import gr.aueb.domain.*;
import gr.aueb.persistence.OrganizerRepository;
import gr.aueb.representation.EventRepresentation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;
@ApplicationScoped
@Mapper(componentModel = "jakarta",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {TicketZoneMapper.class})
public abstract class EventMapper {

    @Inject
    protected OrganizerRepository organizerRepository;

    @Mapping(target = "organizerId", source = "organizer.id")
    @Mapping(target = "ticketZones", source = "ticketZones")
    public abstract EventRepresentation toRepresentation(Event event);

    @Mapping(target = "organizer", expression = "java(findOrganizer(representation))")
    public abstract Event toEntity(EventRepresentation representation);

    public abstract List<EventRepresentation> toRepresentationList(List<Event> events);

    protected Organizer findOrganizer(EventRepresentation representation) {
        if (representation.organizerId == null) {
            throw new IllegalArgumentException("Organizer ID is required");
        }

        Organizer organizer = organizerRepository.findById(Long.valueOf(representation.organizerId));
        if (organizer == null) {
            throw new IllegalArgumentException("Organizer not found with ID: " + representation.organizerId);
        }
        return organizer;
    }
}