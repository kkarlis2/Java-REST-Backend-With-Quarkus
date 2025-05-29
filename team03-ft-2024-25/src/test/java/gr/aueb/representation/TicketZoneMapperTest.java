package gr.aueb.representation;

import gr.aueb.domain.TicketZone;
import gr.aueb.domain.Event;
import gr.aueb.domain.Category;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TicketZoneMapperTest {

    @Inject
    TicketZoneMapper ticketZoneMapper;

    @Test
    public void testToRepresentation() {
        // Prepare test data
        Event event = new Event();
        TicketZone ticketZone = new TicketZone();
        ticketZone.setEvent(event);
        ticketZone.setCost(50.0);
        ticketZone.setCategory(Category.VIP);
        ticketZone.setAvailableSeats(100);

        // Test mapping to representation
        TicketZoneRepresentation representation = ticketZoneMapper.toRepresentation(ticketZone);

        // Assertions
        assertNotNull(representation);
        assertEquals(ticketZone.getEvent().getId(), representation.getEventId());
        assertEquals(ticketZone.getCost(), representation.getCost());
        assertEquals(ticketZone.getCategory(), representation.getCategory());
        assertEquals(ticketZone.getAvailableSeats(), representation.getAvailableSeats());
    }

    @Test
    public void testToEntityWithEventSetDirectly() {
        // Prepare test data
        Event event = new Event();
        TicketZoneRepresentation representation = new TicketZoneRepresentation();
        representation.setCost(75.0);
        representation.setCategory(Category.SIMPLE);
        representation.setAvailableSeats(200);

        // Test mapping to entity
        TicketZone ticketZone = ticketZoneMapper.toEntity(representation);

        // Directly setting the event for testing
        ticketZone.setEvent(event);

        // Assertions
        assertNotNull(ticketZone);
        assertEquals(event, ticketZone.getEvent());
        assertEquals(representation.getCost(), ticketZone.getCost());
        assertEquals(representation.getCategory(), ticketZone.getCategory());
        assertEquals(representation.getAvailableSeats(), ticketZone.getAvailableSeats());
    }

    @Test
    public void testToRepresentationList() {
        Event event = new Event();

        TicketZone zone1 = new TicketZone();
        zone1.setEvent(event);
        zone1.setCost(30.0);
        zone1.setCategory(Category.ARENA);
        zone1.setAvailableSeats(50);

        TicketZone zone2 = new TicketZone();
        zone2.setEvent(event);
        zone2.setCost(40.0);
        zone2.setCategory(Category.VIP);
        zone2.setAvailableSeats(75);

        List<TicketZone> zones = Arrays.asList(zone1, zone2);

        List<TicketZoneRepresentation> representations = ticketZoneMapper.toRepresentationList(zones);

        assertEquals(2, representations.size());
        assertEquals(zone1.getCost(), representations.get(0).getCost());
        assertEquals(zone2.getCategory(), representations.get(1).getCategory());
    }

    @Test
    public void testToEntityWithEmptyEvent() {
        TicketZoneRepresentation representation = new TicketZoneRepresentation();
        representation.setCost(0.0); // Providing a default cost to avoid the error
        representation.setCategory(Category.SIMPLE);
        representation.setAvailableSeats(0);

        TicketZone ticketZone = ticketZoneMapper.toEntity(representation);

        assertNotNull(ticketZone);
        assertNull(ticketZone.getEvent());
        assertEquals(0.0, ticketZone.getCost());
        assertEquals(Category.SIMPLE, ticketZone.getCategory());
        assertEquals(0, ticketZone.getAvailableSeats());
    }

    @Test
    public void testReservationsGetterSetter() {
        // Arrange
        TicketZoneRepresentation representation = new TicketZoneRepresentation();
        ReservationRepresentation reservation1 = new ReservationRepresentation();
        ReservationRepresentation reservation2 = new ReservationRepresentation();
        List<ReservationRepresentation> reservations = Arrays.asList(reservation1, reservation2);

        // Act
        representation.setReservations(reservations);

        // Assert
        assertNotNull(representation.getReservations());
        assertEquals(2, representation.getReservations().size());
        assertEquals(reservations, representation.getReservations());

        // Test with null value
        representation.setReservations(null);
        assertNull(representation.getReservations());
    }
}
