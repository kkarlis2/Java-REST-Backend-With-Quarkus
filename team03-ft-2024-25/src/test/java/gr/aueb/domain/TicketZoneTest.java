package gr.aueb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class TicketZoneTest {

    private Organizer organizer;
    private TicketZone ticketZone;
    private Event event;

    @BeforeEach
    public void setUp() {
        organizer = new Organizer("123456789", "Event planning company", "6981498968", "kon.karlis", "password123", "kon.karlis@aueb.gr", "Eyelpidwn", "47", new ZipCode("12345"));
        event = new Event("Anna Vissi Concert", LocalDate.now().plusDays(10), LocalTime.of(19,0), "Kallimarmaro Arena", "A live show from Anna Vissi", EventType.CONCERT,organizer);
        ticketZone = new TicketZone(50.00,Category.VIP,100, 100,event);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(50.00,ticketZone.getCost());
        assertEquals(Category.VIP, ticketZone.getCategory());
        assertEquals(100, ticketZone.getAvailableSeats());
        assertEquals(event, ticketZone.getEvent());
    }

    @Test
    public void testSetters() {
        ticketZone.setCost(75.00);
        ticketZone.setCategory(Category.SIMPLE);
        ticketZone.setAvailableSeats(150);

        assertEquals(75.00, ticketZone.getCost());
        assertEquals(Category.SIMPLE, ticketZone.getCategory());
        assertEquals(150, ticketZone.getAvailableSeats());

        Event newEvent = new Event("Jazz Concert", LocalDate.now(), LocalTime.now(), "Jazz Club", "A jazz concert", EventType.CONCERT,organizer);
        ticketZone.setEvent(newEvent);

        assertEquals(newEvent, ticketZone.getEvent());
    }

    @Test
    public void testSettersForExceptions() {
        assertThrows(IllegalArgumentException.class, () -> ticketZone.setCost(null));
        assertThrows(IllegalArgumentException.class, () -> ticketZone.setCategory(null));
        assertThrows(IllegalArgumentException.class, () -> ticketZone.setAvailableSeats(-1));
        assertThrows(IllegalArgumentException.class, () -> ticketZone.setAvailableSeats(null));
        assertThrows(IllegalArgumentException.class, () -> ticketZone.setEvent(null));
    }

}
