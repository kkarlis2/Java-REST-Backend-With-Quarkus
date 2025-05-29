package gr.aueb.representation;

import gr.aueb.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class OrganizerMapperTest {

    @Inject
    OrganizerMapper organizerMapper;

    @Test
    public void testToRepresentation() {
        // Arrange
        Account account = new Account("testuser", "password123");
        Email email = new Email("test@example.com");
        Address address = new Address("Test Street", "42", new ZipCode("12345"));

        Organizer organizer = new Organizer(
                "123456789",           // taxId
                "Test Brand",          // brandName
                "210-1234567",        // phoneNumber
                "testuser",           // userName
                "password123",        // password
                "test@example.com",   // email
                "Test Street",        // street
                "42",                 // number
                new ZipCode("12345")  // zipCode
        );

        // Act
        OrganizerRepresentation representation = organizerMapper.toRepresentation(organizer);

        // Assert
        assertNotNull(representation);
        assertEquals("123456789", representation.getTaxId());
        assertEquals("Test Brand", representation.getBrandName());
        assertEquals("210-1234567", representation.getPhoneNumber());
        assertEquals("testuser", representation.getUsername());
        assertEquals("test@example.com", representation.getEmail());
        assertEquals("Test Street", representation.getStreet());
        assertEquals("42", representation.getNumber());
        assertEquals("12345", representation.getZipCode());
    }

    @Test
    public void testToEntity() {
        // Arrange
        OrganizerRepresentation representation = new OrganizerRepresentation();
        representation.setTaxId("123456789");
        representation.setBrandName("Test Brand");
        representation.setPhoneNumber("210-1234567");
        representation.setUsername("testuser");
        representation.setPassword("password123");
        representation.setEmail("test@example.com");
        representation.setStreet("Test Street");
        representation.setNumber("42");
        representation.setZipCode("12345");

        // Act
        Organizer organizer = organizerMapper.toEntity(representation);

        // Assert
        assertNotNull(organizer);
        assertEquals("123456789", organizer.getTaxId());
        assertEquals("Test Brand", organizer.getBrandName());
        assertEquals("210-1234567", organizer.getPhoneNumber());

        // Check embedded objects
        assertNotNull(organizer.getAccount(), "Account should not be null");
        assertEquals("testuser", organizer.getUserName());
        assertEquals("password123", organizer.getPassword());

        assertNotNull(organizer.getEmail(), "Email should not be null");
        assertEquals("test@example.com", organizer.getEmail());

        assertNotNull(organizer.getStreet(), "Street should not be null");
        assertEquals("Test Street", organizer.getStreet());
        assertEquals("42", organizer.getNumber());
        assertEquals("12345", organizer.getZipCode());
    }

    @Test
    public void testEventsGetterAndSetter() {
        // Arrange
        OrganizerRepresentation representation = new OrganizerRepresentation();
        Set<EventRepresentation> events = new HashSet<>();
        EventRepresentation event = new EventRepresentation();
        events.add(event);

        // Act
        representation.setEvents(events);

        // Assert
        assertNotNull(representation.getEvents());
        assertEquals(events, representation.getEvents());
        assertEquals(1, representation.getEvents().size());
    }

    @Test
    public void testToString() {
        // Arrange
        OrganizerRepresentation representation = new OrganizerRepresentation();
        representation.setId(1);
        representation.setTaxId("123456789");
        representation.setBrandName("Test Brand");
        representation.setPhoneNumber("210-1234567");
        representation.setUsername("testuser");
        representation.setEmail("test@example.com");

        // Act
        String result = representation.toString();

        // Assert
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("taxId='123456789'"));
        assertTrue(result.contains("brandName='Test Brand'"));
        assertTrue(result.contains("phoneNumber='210-1234567'"));
        assertTrue(result.contains("username='testuser'"));
        assertTrue(result.contains("email='test@example.com'"));
    }


}