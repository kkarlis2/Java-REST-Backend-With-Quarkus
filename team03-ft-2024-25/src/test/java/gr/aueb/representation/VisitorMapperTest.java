package gr.aueb.representation;

import gr.aueb.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class VisitorMapperTest {

    @Inject
    VisitorMapper visitorMapper;

    @Test
    public void testToRepresentation() {
        // Arrange
        Visitor visitor = new Visitor(
                "John",              // firstName
                "Doe",               // lastName
                "210-1234567",       // phoneNumber
                "john@example.com",  // email
                "johndoe",           // username
                "password123"        // password
        );

        // Act
        VisitorRepresentation representation = visitorMapper.toRepresentation(visitor);

        // Assert
        assertNotNull(representation);
        assertEquals("John", representation.getFirstName());
        assertEquals("Doe", representation.getLastName());
        assertEquals("210-1234567", representation.getPhoneNumber());
        assertEquals("johndoe", representation.getUsername());
        assertEquals("john@example.com", representation.getEmail());
        assertNotNull(representation.getReservations(), "Reservations should not be null");
        assertTrue(representation.getReservations().isEmpty(), "Reservations should be empty");
    }

    @Test
    public void testToEntity() {
        // Arrange
        VisitorRepresentation representation = new VisitorRepresentation();
        representation.setFirstName("John");
        representation.setLastName("Doe");
        representation.setPhoneNumber("210-1234567");
        representation.setUsername("johndoe");
        representation.setPassword("password123");
        representation.setEmail("john@example.com");

        // Act
        Visitor visitor = visitorMapper.toEntity(representation);

        // Assert
        assertNotNull(visitor);
        assertEquals("John", visitor.getFirstName());
        assertEquals("Doe", visitor.getLastName());
        assertEquals("210-1234567", visitor.getPhoneNumber());

        // Check embedded objects
        assertNotNull(visitor.getAccount(), "Account should not be null");
        assertEquals("johndoe", visitor.getUsername());
        assertEquals("password123", visitor.getPassword());

        assertNotNull(visitor.getEmail(), "Email should not be null");
        assertEquals("john@example.com", visitor.getEmail());

        assertTrue(visitor.getReservations().isEmpty(), "Reservations should be empty");
    }

    @Test
    public void testMandatoryFieldValidation() {
        // Test First Name
        assertThrows(IllegalArgumentException.class, () -> {
            VisitorRepresentation representation = new VisitorRepresentation();
            representation.setLastName("Doe");
            representation.setPhoneNumber("210-1234567");
            representation.setEmail("john@example.com");
            representation.setUsername("johndoe");
            representation.setPassword("password123");
            // Missing firstName
            visitorMapper.toEntity(representation);
        }, "First name should be mandatory");

        // Test Last Name
        assertThrows(IllegalArgumentException.class, () -> {
            VisitorRepresentation representation = new VisitorRepresentation();
            representation.setFirstName("John");
            representation.setPhoneNumber("210-1234567");
            representation.setEmail("john@example.com");
            representation.setUsername("johndoe");
            representation.setPassword("password123");
            // Missing lastName
            visitorMapper.toEntity(representation);
        }, "Last name should be mandatory");

        // Test Phone Number
        assertThrows(IllegalArgumentException.class, () -> {
            VisitorRepresentation representation = new VisitorRepresentation();
            representation.setFirstName("John");
            representation.setLastName("Doe");
            representation.setEmail("john@example.com");
            representation.setUsername("johndoe");
            representation.setPassword("password123");
            // Missing phoneNumber
            visitorMapper.toEntity(representation);
        }, "Phone number should be mandatory");

        // Test Email
        assertThrows(IllegalArgumentException.class, () -> {
            VisitorRepresentation representation = new VisitorRepresentation();
            representation.setFirstName("John");
            representation.setLastName("Doe");
            representation.setPhoneNumber("210-1234567");
            representation.setUsername("johndoe");
            representation.setPassword("password123");
            // Missing email
            visitorMapper.toEntity(representation);
        }, "Email should be mandatory");
    }

    @Test
    public void testEmailValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            VisitorRepresentation representation = createValidRepresentation();
            representation.setEmail("");
            visitorMapper.toEntity(representation);
        }, "Empty email should not be allowed");

        assertThrows(IllegalArgumentException.class, () -> {
            VisitorRepresentation representation = createValidRepresentation();
            representation.setEmail(null);
            visitorMapper.toEntity(representation);
        }, "Null email should not be allowed");
    }

    private VisitorRepresentation createValidRepresentation() {
        VisitorRepresentation representation = new VisitorRepresentation();
        representation.setFirstName("John");
        representation.setLastName("Doe");
        representation.setPhoneNumber("210-1234567");
        representation.setUsername("johndoe");
        representation.setPassword("password123");
        representation.setEmail("john@example.com");
        return representation;
    }
}
