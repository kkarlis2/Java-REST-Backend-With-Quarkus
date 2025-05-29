package gr.aueb.resource;

import gr.aueb.domain.Event;
import gr.aueb.persistence.EventRepository;
import gr.aueb.representation.EventMapper;
import gr.aueb.representation.EventRepresentation;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventRepository eventRepository;

    @Inject
    EventMapper eventMapper;

    @GET
    public Response getAllEvents() {
        List<Event> events = eventRepository.listAll();
        return Response.ok(eventMapper.toRepresentationList(events)).build();
    }

    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") Integer id) {
        return eventRepository.findByIdOptional(Long.valueOf(id))
                .map(event -> Response.ok(eventMapper.toRepresentation(event)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/search")
    public Response searchEvents(@QueryParam("title") String title) {
        if (title != null && !title.isEmpty()) {
            List<Event> events = eventRepository.findByTitleContaining(title);
            return Response.ok(eventMapper.toRepresentationList(events)).build();
        }
        return getAllEvents();
    }

    @GET
    @Path("/date/{date}")
    public Response getEventsByDate(@PathParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<Event> events = eventRepository.findByDate(date);
            return Response.ok(eventMapper.toRepresentationList(events)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date format. Use: YYYY-MM-DD")
                    .build();
        }
    }

    @GET
    @Path("/upcoming")
    public Response getUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingEvents();
        return Response.ok(eventMapper.toRepresentationList(events)).build();
    }

    @POST
    @Transactional
    public Response createEvent(EventRepresentation eventRep) {
        try {
            Event event = eventMapper.toEntity(eventRep);
            eventRepository.persist(event);
            return Response.status(Response.Status.CREATED)
                    .entity(eventMapper.toRepresentation(event))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateEvent(@PathParam("id") Integer id, EventRepresentation eventRep) {
        return eventRepository.findByIdOptional(Long.valueOf(id))
                .map(existingEvent -> {
                    try {
                        // Ενημέρωση των πεδίων
                        existingEvent.setTitle(eventRep.title);
                        existingEvent.setDate(eventRep.date);
                        existingEvent.setTime(eventRep.time);
                        existingEvent.setLocation(eventRep.location);
                        existingEvent.setDescription(eventRep.description);
                        existingEvent.setEventType(eventRep.eventType);

                        eventRepository.persist(existingEvent);
                        return Response.ok(eventMapper.toRepresentation(existingEvent)).build();
                    } catch (IllegalArgumentException e) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(e.getMessage())
                                .build();
                    }
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteEvent(@PathParam("id") Integer id) {
        return eventRepository.findByIdOptional(Long.valueOf(id))
                .map(event -> {
                    eventRepository.delete(event);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}