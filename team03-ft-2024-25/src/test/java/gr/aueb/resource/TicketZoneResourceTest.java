package gr.aueb.resource;

import gr.aueb.domain.Category;
import gr.aueb.domain.Event;
import gr.aueb.domain.TicketZone;
import gr.aueb.persistence.EventRepository;
import gr.aueb.persistence.TicketZoneRepository;
import gr.aueb.representation.TicketZoneMapper;
import gr.aueb.representation.TicketZoneRepresentation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TicketZoneResourceTest {

    @InjectMock
    TicketZoneRepository ticketZoneRepository;

    @InjectMock
    EventRepository eventRepository;

    @Inject
    TicketZoneMapper ticketZoneMapper;

    @Inject
    TicketZoneResource ticketZoneResource;

    private Event testEvent;
    private TicketZone testTicketZone;
    private TicketZoneRepresentation testTicketZoneRep;

    @BeforeEach
    public void setUp() {
        testEvent = new Event();
        testEvent.setId(1);
        testEvent.setTitle("Test Event");

        testTicketZone = new TicketZone(50.0, Category.VIP,100, 100, testEvent);
        testTicketZone.setId(1);

        testTicketZoneRep = ticketZoneMapper.toRepresentation(testTicketZone);
    }

    @Test
    public void getTicketZoneById_WhenExists_ReturnsTicketZone() {
        when(ticketZoneRepository.findByIdWithEvent(1)).thenReturn(Optional.of(testTicketZone));

        Response response = ticketZoneResource.getById(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void getTicketZoneById_WhenNotExists_Returns404() {
        when(ticketZoneRepository.findByIdWithEvent(999)).thenReturn(Optional.empty());

        Response response = ticketZoneResource.getById(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getTicketZonesByEvent_ReturnsTicketZonesList() {
        when(ticketZoneRepository.findByEventId(1)).thenReturn(Arrays.asList(testTicketZone));

        Response response = ticketZoneResource.getByEvent(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void createTicketZone_WithValidData_ReturnsCreated() {
        // Δημιουργώ ένα νέο Event για το test
        Event event = new Event();
        event.setId(1);
        event.setTitle("Test Event");

        // Δημιουργώ το TicketZone για το test
        TicketZone ticketZone = new TicketZone(50.0, Category.VIP,100, 100, event);
        ticketZone.setId(1);

        // Κάνω mock τα repositories
        doReturn(Optional.of(event)).when(eventRepository).findByIdOptional(1L);
        doNothing().when(ticketZoneRepository).persist(any(TicketZone.class));

        // Δημιουργώ το representation για το request
        TicketZoneRepresentation representation = ticketZoneMapper.toRepresentation(ticketZone);

        // Καλώ το endpoint
        Response response = ticketZoneResource.create(representation);

        // Ελέγχω τα αποτελέσματα
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(ticketZoneRepository).persist(any(TicketZone.class));

        // Επιβεβαιώνω ότι τα πεδία είναι σωστά
        TicketZoneRepresentation createdRep = (TicketZoneRepresentation) response.getEntity();
        assertEquals(50.0, createdRep.getCost());
        assertEquals(Category.VIP, createdRep.getCategory());
        assertEquals(100, createdRep.getAvailableSeats());
        assertEquals(1, createdRep.getEventId());
    }

    @Test
    public void createTicketZone_WithInvalidEvent_ReturnsBadRequest() {
        when(eventRepository.findByIdOptional(999L)).thenReturn(Optional.empty());
        testTicketZoneRep.setEventId(999);

        Response response = ticketZoneResource.create(testTicketZoneRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(ticketZoneRepository, never()).persist(any(TicketZone.class));
    }

    @Test
    public void updateTicketZone_WithValidData_ReturnsUpdated() {
        // Δημιουργώ ένα νέο Event για το test
        Event event = new Event();
        event.setId(1);
        event.setTitle("Test Event");

        // Δημιουργώ το TicketZone για το test
        TicketZone ticketZone = new TicketZone(50.0, Category.VIP,100,100, event);
        ticketZone.setId(1);

        // Κάνω mock τα repositories
        doReturn(Optional.of(ticketZone)).when(ticketZoneRepository).findByIdOptional(1L);
        doNothing().when(ticketZoneRepository).persist(any(TicketZone.class));

        // Δημιουργώ το representation για το update
        TicketZoneRepresentation updateRep = ticketZoneMapper.toRepresentation(ticketZone);
        updateRep.setCost(75.0);
        updateRep.setAvailableSeats(50);

        // Καλώ το endpoint
        Response response = ticketZoneResource.update(1, updateRep);

        // Ελέγχω τα αποτελέσματα
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(ticketZoneRepository).persist(any(TicketZone.class));

        // Επιβεβαιώνω ότι τα πεδία ενημερώθηκαν
        TicketZoneRepresentation updatedRep = (TicketZoneRepresentation) response.getEntity();
        assertEquals(75.0, updatedRep.getCost());
        assertEquals(50, updatedRep.getAvailableSeats());
    }

    @Test
    public void updateTicketZone_WithNonExistingId_Returns404() {
        when(ticketZoneRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = ticketZoneResource.update(999, testTicketZoneRep);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(ticketZoneRepository, never()).persist(any(TicketZone.class));
    }

    @Test
    public void deleteTicketZone_WhenExists_ReturnsNoContent() {
        when(ticketZoneRepository.findByIdOptional(1L)).thenReturn(Optional.of(testTicketZone));

        Response response = ticketZoneResource.delete(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(ticketZoneRepository).delete(testTicketZone);
    }

    @Test
    public void deleteTicketZone_WhenNotExists_Returns404() {
        when(ticketZoneRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = ticketZoneResource.delete(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(ticketZoneRepository, never()).delete(any(TicketZone.class));
    }
}