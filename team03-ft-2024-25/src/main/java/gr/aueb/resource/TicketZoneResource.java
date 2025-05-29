package gr.aueb.resource;

import gr.aueb.domain.TicketZone;
import gr.aueb.persistence.TicketZoneRepository;
import gr.aueb.persistence.EventRepository;
import gr.aueb.representation.TicketZoneMapper;
import gr.aueb.representation.TicketZoneRepresentation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/ticketzones")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketZoneResource {

    @Inject
    TicketZoneRepository ticketZoneRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    TicketZoneMapper ticketZoneMapper;

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return ticketZoneRepository.findByIdWithEvent(id)
                .map(ticketZoneMapper::toRepresentation)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @GET
    @Path("/event/{eventId}")
    public Response getByEvent(@PathParam("eventId") Integer eventId) {
        List<TicketZoneRepresentation> zones = ticketZoneMapper.toRepresentationList(
                ticketZoneRepository.findByEventId(eventId)
        );
        return Response.ok(zones).build();
    }

    @POST
    @Transactional
    public Response create(TicketZoneRepresentation representation) {
        TicketZone ticketZone = ticketZoneMapper.toEntity(representation);

        return eventRepository.findByIdOptional(Long.valueOf(representation.getEventId()))
                .map(event -> {
                    ticketZone.setEvent(event);
                    ticketZoneRepository.persist(ticketZone);
                    return Response.status(Response.Status.CREATED)
                            .entity(ticketZoneMapper.toRepresentation(ticketZone))
                            .build();
                })
                .orElse(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Event not found")
                        .build());
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, TicketZoneRepresentation representation) {
        return ticketZoneRepository.findByIdOptional(Long.valueOf(id))
                .map(zone -> {
                    zone.setCost(representation.getCost());
                    zone.setCategory(representation.getCategory());
                    zone.setAvailableSeats(representation.getAvailableSeats());
                    ticketZoneRepository.persist(zone);
                    return Response.ok(ticketZoneMapper.toRepresentation(zone)).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        return ticketZoneRepository.findByIdOptional(Long.valueOf(id))
                .map(zone -> {
                    ticketZoneRepository.delete(zone);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
