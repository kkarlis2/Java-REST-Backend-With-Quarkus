package gr.aueb.representation;

import gr.aueb.domain.Email;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EmailMapperTest {

    @Inject
    private EmailMapper emailMapper;

    @Test
    void testToRepresentation() {
        // Given: a sample Email
        Email email = new Email("test@example.com");

        // When: converting Email to String
        String emailRepresentation = emailMapper.toRepresentation(email);

        // Then: validate the representation
        assertNotNull(emailRepresentation, "Representation should not be null");
        assertEquals(email.getEmail(), emailRepresentation, "Email should match");
    }

    @Test
    void testToEntity() {
        // Given: a sample email string
        String emailString = "test@example.com";

        // When: converting String to Email
        Email email = emailMapper.toEntity(emailString);

        // Then: validate the entity
        assertNotNull(email, "Email should not be null");
        assertEquals(emailString, email.getEmail(), "Email should match");
    }

    @Test
    void testToRepresentationNullEmail() {
        // Given: a null Email
        Email nullEmail = null;

        // When: converting null Email to String
        String emailRepresentation = emailMapper.toRepresentation(nullEmail);

        // Then: validate that the result is null
        assertNull(emailRepresentation, "Representation should be null");
    }

    @Test
    void testToEntityNullString() {
        // Given: a null String
        String nullString = null;

        // When: converting null String to Email
        Email email = emailMapper.toEntity(nullString);

        // Then: validate that the result is null
        assertNull(email, "Email should be null");
    }

    @Test
    void shouldGetAndSetEmail() {
        // Given
        EmailRepresentation emailRep = new EmailRepresentation();
        Email email = new Email("test@example.com");

        // When
        emailRep.setEmail(email);

        // Then
        assertNotNull(emailRep.getEmail());
        assertEquals(email, emailRep.getEmail());
    }

    @Test
    void shouldCreateWithNullEmail() {
        // When
        EmailRepresentation emailRep = new EmailRepresentation();

        // Then
        assertNull(emailRep.getEmail());
    }

    @Test
    void shouldSetNullEmail() {
        // Given
        EmailRepresentation emailRep = new EmailRepresentation();
        emailRep.setEmail(new Email("test@example.com"));

        // When
        emailRep.setEmail(null);

        // Then
        assertNull(emailRep.getEmail());
    }
}

