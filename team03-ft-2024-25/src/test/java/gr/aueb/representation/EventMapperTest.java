package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.persistence.EventRepository;
import gr.aueb.persistence.OrganizerRepository;
import gr.aueb.persistence.ZipCodeRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class EventMapperTest {

    @Inject
    EventMapper eventMapper;

    @Inject
    OrganizerRepository organizerRepository;

    @Inject
    ZipCodeRepository zipCodeRepository;

    @Inject
    EventRepository eventRepository;

    @AfterEach
    @ActivateRequestContext
    void cleanup() {
        // Πρώτα πρέπει να διαγράψουμε τα TicketZones που σχετίζονται με τα Events
        for (Event event : eventRepository.listAll()) {
            event.getTicketZones().clear(); // Clear the collection first
        }
        eventRepository.getEntityManager().flush(); // Flush the changes

        // Τώρα μπορούμε να διαγράψουμε με τη σωστή σειρά
        eventRepository.deleteAll();
        organizerRepository.deleteAll();
        zipCodeRepository.deleteAll();
    }

    public Organizer createTestOrganizer() {
        // First, persist the ZipCode entity
        ZipCode zipCode = new ZipCode("00000");
        zipCodeRepository.persist(zipCode); // Persist the zip code

        // Now create and persist the Organizer entity
        Organizer organizer = new Organizer(
                "TEST123456",      // unique taxId
                "Test Organizer",  // brandName
                "210-1234567",     // phoneNumber
                "testcruser",      // userName
                "password123",     // password
                "test367@test.com", // email
                "Test Street",     // street
                "42",              // number
                zipCode            // zipCode (already persisted)
        );
        organizerRepository.persist(organizer); // Persist the organizer
        return organizer;
    }



    @Test
    public void testToRepresentation() {
        // Arrange
        Organizer testOrganizer = createTestOrganizer();

        Event event = new Event(
                "Test Event",          // title
                LocalDate.now(),       // date
                LocalTime.of(20, 30),  // time
                "Test Location",       // location
                "Test Description",    // description
                EventType.CONCERT,     // eventType
                testOrganizer         // organizer
        );

        // Act
        EventRepresentation representation = eventMapper.toRepresentation(event);

        // Assert
        assertNotNull(representation);
        assertEquals("Test Event", representation.title);
        assertEquals(LocalDate.now(), representation.date);
        assertEquals(LocalTime.of(20, 30), representation.time);
        assertEquals("Test Location", representation.location);
        assertEquals("Test Description", representation.description);
        assertEquals(EventType.CONCERT, representation.eventType);
        assertEquals(testOrganizer.getId(), representation.organizerId);
        assertNotNull(representation.ticketZones);
        assertTrue(representation.ticketZones.isEmpty());
    }

    @Test
    public void testToEntity() {
        // Arrange
        Organizer testOrganizer = createTestOrganizer();

        EventRepresentation representation = new EventRepresentation();
        representation.title = "Test Event";
        representation.date = LocalDate.now();
        representation.time = LocalTime.of(20, 30);
        representation.location = "Test Location";
        representation.description = "Test Description";
        representation.eventType = EventType.CONCERT;
        representation.organizerId = testOrganizer.getId();
        representation.ticketZones = new HashSet<>();

        // Act
        Event event = eventMapper.toEntity(representation);

        // Assert
        assertNotNull(event);
        assertEquals("Test Event", event.getTitle());
        assertEquals(LocalDate.now(), event.getDate());
        assertEquals(LocalTime.of(20, 30), event.getTime());
        assertEquals("Test Location", event.getLocation());
        assertEquals("Test Description", event.getDescription());
        assertEquals(EventType.CONCERT, event.getEventType());
        assertNotNull(event.getOrganizer());
        assertEquals(testOrganizer.getId(), event.getOrganizer().getId());
        assertTrue(event.getTicketZones().isEmpty());
    }

    @Test
    public void testInvalidOrganizerId() {
        // Arrange
        EventRepresentation representation = new EventRepresentation();
        representation.title = "Test Event";
        representation.date = LocalDate.now();
        representation.time = LocalTime.of(20, 30);
        representation.location = "Test Location";
        representation.description = "Test Description";
        representation.eventType = EventType.CONCERT;
        representation.organizerId = -1; // Μη έγκυρο ID
        representation.ticketZones = new HashSet<>();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                        eventMapper.toEntity(representation),
                "Should throw exception for invalid organizer ID"
        );
    }
}
