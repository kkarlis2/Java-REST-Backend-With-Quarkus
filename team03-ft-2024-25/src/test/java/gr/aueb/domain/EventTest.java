package gr.aueb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private Event event;
    private LocalDate date;
    private LocalTime time;
    private Organizer organizer;

    @BeforeEach
    public void setUp() {
        date = LocalDate.now().plusDays(10);
        time = LocalTime.of(19,0);
        organizer = new Organizer("123456789", "Event planning company", "6981498968", "kon.karlis", "password123", "kon.karlis@aueb.gr", "Eyelpidwn", "47", new ZipCode("12345"));
        event = new Event("Anna Vissi Concert", date, time, "Kallimarmaro Arena", "A live show from Anna Vissi", EventType.CONCERT,organizer);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Anna Vissi Concert", event.getTitle());
        assertEquals(date, event.getDate());
        assertEquals(time, event.getTime());
        assertEquals("Kallimarmaro Arena", event.getLocation());
        assertEquals("A live show from Anna Vissi", event.getDescription());
        assertEquals(EventType.CONCERT, event.getEventType());
        assertEquals(organizer, event.getOrganizer());
    }

    @Test
    public void testSetters() {
        event.setTitle("Rock Concert");
        event.setLocation("OAKA arena");
        event.setDescription("The biggest rock concert!");
        event.setEventType(EventType.THEATER);


        assertEquals("Rock Concert", event.getTitle());
        assertEquals("OAKA arena", event.getLocation());
        assertEquals("The biggest rock concert!", event.getDescription());
        assertEquals(EventType.THEATER, event.getEventType());

        Organizer newOrganizer = new Organizer("987654321", "New Organizer", "1234567899", "kkarlis2", "987654321", "kostaskarlis2001@gmail.com", "Pathsiwn", "4", new ZipCode("11143"));
        event.setOrganizer(newOrganizer);
    }

    @Test
    public void testAddTicketZone() {
        TicketZone vipZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        event.addTicketZone(vipZone);

        assertEquals(1, event.getTicketZones().size());
        assertTrue(event.getTicketZones().contains(vipZone));
        assertEquals(event, vipZone.getEvent());
        assertFalse(event.getTicketZones().isEmpty());

        TicketZone Zone = new TicketZone(100.00, Category.ARENA,50, 50,event);
        assertFalse(event.getTicketZones().contains(Zone));
    }

    @Test
    public void testSetTicketZones() {
        TicketZone vipZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        TicketZone simpleZone = new TicketZone(20.00, Category.SIMPLE,300, 300,event);

        HashSet<TicketZone> ticketZones = new HashSet<>();
        ticketZones.add(vipZone);
        ticketZones.add(simpleZone);

        event.setTicketZones(ticketZones);

        assertEquals(2, event.getTicketZones().size());
        assertTrue(event.getTicketZones().contains(vipZone));
        assertTrue(event.getTicketZones().contains(simpleZone));

        assertThrows(IllegalArgumentException.class,()-> event.setTicketZones(null));
    }

    @Test
    public void testOrganizer(){
        assertEquals(organizer, event.getOrganizer());

        Organizer newOrganizer = new Organizer("111111111", "Organizer2", "6981498969", "org2", "helloworld", "org2@aueb.gr", "Axarnwn", "234", new ZipCode("10565"));
        event.setOrganizer(newOrganizer);

        assertEquals(newOrganizer, event.getOrganizer());

    }
    @Test
    public void testSettersThrowExceptionForInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> event.setTitle(null));
        assertThrows(IllegalArgumentException.class, () -> event.setDate(null));
        assertThrows(IllegalArgumentException.class, () -> event.setTime(null));
        assertThrows(IllegalArgumentException.class, () -> event.setLocation(null));
        assertThrows(IllegalArgumentException.class,()-> event.setDescription(null));
        assertThrows(IllegalArgumentException.class,()-> event.setDescription(""));
        assertThrows(IllegalArgumentException.class, () -> event.setEventType(null));
        assertThrows(IllegalArgumentException.class, () -> event.setTitle(""));
        assertThrows(IllegalArgumentException.class, () -> event.setLocation(""));
        assertThrows(IllegalArgumentException.class, () -> event.setOrganizer(null));
    }

    @Test
    public void testEquals() {
        Organizer organizer = new Organizer("123456789", "Event Organizer", "1234567890", "user", "password", "email@example.com", "Street", "10", new ZipCode("12345"));
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalTime time = LocalTime.of(18, 0);

        Event event1 = new Event("Concert", date, time, "Arena", "A great concert", EventType.CONCERT, organizer);
        Event event2 = new Event("Concert", date, time, "Arena", "Another description", EventType.CONCERT, organizer);

        assertTrue(event1.equals(event1));

        assertFalse(event1.equals(null));

        assertFalse(event1.equals("A String"));

        Event event3 = new Event("Concert", LocalDate.of(2024, 2, 1), time, "Arena", "A great concert", EventType.CONCERT, organizer);
        Event event4 = new Event("Concert", date, LocalTime.of(20, 0), "Arena", "A great concert", EventType.CONCERT, organizer);
        Event event5 = new Event("Concert", date, time, "Different Arena", "A great concert", EventType.CONCERT, organizer);

        assertFalse(event1.equals(event3));
        assertFalse(event1.equals(event4));
        assertFalse(event1.equals(event5));

        assertTrue(event1.equals(event2));
    }

    @Test
    public void testHashCode() {
        Organizer organizer = new Organizer("123456789", "Event Organizer", "1234567890", "user", "password", "email@example.com", "Street", "10", new ZipCode("12345"));
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalTime time = LocalTime.of(18, 0);

        Event event1 = new Event("Concert", date, time, "Arena", "A great concert", EventType.CONCERT, organizer);
        Event event2 = new Event("Concert", date, time, "Arena", "Another description", EventType.CONCERT, organizer);
        Event event3 = new Event("Concert", LocalDate.of(2024, 2, 1), time, "Arena", "A great concert", EventType.CONCERT, organizer);

        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }





}
