package gr.aueb.resource;

import gr.aueb.domain.Account;
import gr.aueb.domain.Visitor;
import gr.aueb.persistence.VisitorRepository;
import gr.aueb.representation.VisitorMapper;
import gr.aueb.representation.VisitorRepresentation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class VisitorResourceTest {

    @InjectMock
    VisitorRepository visitorRepository;

    @Inject
    VisitorResource visitorResource;

    private Visitor testVisitor;
    private VisitorRepresentation testVisitorRep;

    @BeforeEach
    void setUp() {
        // Setup test Visitor
        testVisitor = new Visitor(
                "Vis1",              // firstName
                "LastDoe",               // lastName
                "6912121212",        // phoneNumber
                "johnLast@test.com",     // email
                "john2doe",           // username
                "password1233"        // password
        );
        testVisitor.setId(1);

        // Setup repository mock responses
        when(visitorRepository.findByIdOptional(1L)).thenReturn(Optional.of(testVisitor));
        when(visitorRepository.findByIdOptional(999L)).thenReturn(Optional.empty());
    }

    @Test
    void getAllVisitors_ReturnsVisitorsList() {
        when(visitorRepository.listAll()).thenReturn(Arrays.asList(testVisitor));

        Response response = visitorResource.getAllVisitors();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getVisitorById_WhenExists_ReturnsVisitor() {
        Response response = visitorResource.getVisitorById(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getVisitorById_WhenNotExists_Returns404() {
        Response response = visitorResource.getVisitorById(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void getVisitorByUsername_WhenExists_ReturnsVisitor() {
        when(visitorRepository.findByUsername("johndoe")).thenReturn(Optional.of(testVisitor));

        Response response = visitorResource.getVisitorByUsername("johndoe");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getVisitorByUsername_WhenNotExists_Returns404() {
        when(visitorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Response response = visitorResource.getVisitorByUsername("nonexistent");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void getVisitorByEmail_WhenExists_ReturnsVisitor() {
        when(visitorRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testVisitor));

        Response response = visitorResource.getVisitorByEmail("john@test.com");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getVisitorByEmail_WhenNotExists_Returns404() {
        when(visitorRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        Response response = visitorResource.getVisitorByEmail("nonexistent@test.com");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void createVisitor_WithUniqueData_ReturnsCreated() {
        when(visitorRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(visitorRepository.findByEmail(any())).thenReturn(Optional.empty());

        VisitorRepresentation newVisitorRep = new VisitorRepresentation();
        newVisitorRep.setFirstName("Jane");
        newVisitorRep.setLastName("Doe");
        newVisitorRep.setPhoneNumber("9876543210");
        newVisitorRep.setEmail("jane@test.com");
        newVisitorRep.setUsername("janedoe");
        newVisitorRep.setPassword("password123");

        Response response = visitorResource.createVisitor(newVisitorRep);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(visitorRepository).persist(any(Visitor.class));
    }

    @Test
    void createVisitor_WithExistingUsername_ReturnsConflict() {
        when(visitorRepository.findByUsername("johndoe")).thenReturn(Optional.of(testVisitor));

        VisitorRepresentation newVisitorRep = new VisitorRepresentation();
        newVisitorRep.setUsername("johndoe");

        Response response = visitorResource.createVisitor(newVisitorRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        verify(visitorRepository, never()).persist(any(Visitor.class));
    }

    @Test
    void createVisitor_WithExistingEmail_ReturnsConflict() {
        when(visitorRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testVisitor));

        VisitorRepresentation newVisitorRep = new VisitorRepresentation();
        newVisitorRep.setEmail("john@test.com");

        Response response = visitorResource.createVisitor(newVisitorRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        verify(visitorRepository, never()).persist(any(Visitor.class));
    }

    @Test
    void deleteVisitor_WhenExists_ReturnsNoContent() {
        Response response = visitorResource.deleteVisitor(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(visitorRepository).delete(testVisitor);
    }

    @Test
    void deleteVisitor_WhenNotExists_Returns404() {
        Response response = visitorResource.deleteVisitor(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(visitorRepository, never()).delete(any(Visitor.class));
    }

    @Test
    void updateVisitor_WithExistingUsername_ReturnsConflict() {
        // Δημιουργία ενός άλλου visitor με το username που θέλουμε να χρησιμοποιήσουμε
        Visitor otherVisitor = new Visitor(
                "Other",
                "User",
                "6999999999",
                "other@test.com",
                "existingUsername",
                "pass123"
        );
        otherVisitor.setId(2);

        when(visitorRepository.findByIdOptional(1L)).thenReturn(Optional.of(testVisitor));
        when(visitorRepository.findByUsername("existingUsername"))
                .thenReturn(Optional.of(otherVisitor));
        when(visitorRepository.findByEmail(any())).thenReturn(Optional.empty());

        VisitorRepresentation updateVisitorRep = new VisitorRepresentation();
        updateVisitorRep.setUsername("existingUsername");
        updateVisitorRep.setEmail("newemail@test.com");

        Response response = visitorResource.updateVisitor(1, updateVisitorRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Username already exists", response.getEntity());
        verify(visitorRepository, never()).persist(any(Visitor.class));
    }

    @Test
    void updateVisitor_WithExistingEmail_ReturnsConflict() {
        // Δημιουργία ενός άλλου visitor με το email που θέλουμε να χρησιμοποιήσουμε
        Visitor otherVisitor = new Visitor(
                "Other",
                "User",
                "6999999999",
                "existing@test.com",
                "otheruser",
                "pass123"
        );
        otherVisitor.setId(2);

        when(visitorRepository.findByIdOptional(1L)).thenReturn(Optional.of(testVisitor));
        when(visitorRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(visitorRepository.findByEmail("existing@test.com"))
                .thenReturn(Optional.of(otherVisitor));

        VisitorRepresentation updateVisitorRep = new VisitorRepresentation();
        updateVisitorRep.setUsername("newusername");
        updateVisitorRep.setEmail("existing@test.com");

        Response response = visitorResource.updateVisitor(1, updateVisitorRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Email already exists", response.getEntity());
        verify(visitorRepository, never()).persist(any(Visitor.class));
    }

    @Test
    void updateVisitor_WithInvalidData_ReturnsBadRequest() {
        when(visitorRepository.findByIdOptional(1L)).thenReturn(Optional.of(testVisitor));
        when(visitorRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(visitorRepository.findByEmail(any())).thenReturn(Optional.empty());

        doThrow(new IllegalArgumentException("Invalid phone number"))
                .when(visitorRepository).persist(any(Visitor.class));

        VisitorRepresentation updateVisitorRep = new VisitorRepresentation();
        updateVisitorRep.setPhoneNumber("invalid"); // μη έγκυρος αριθμός τηλεφώνου

        Response response = visitorResource.updateVisitor(1, updateVisitorRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(visitorRepository).findByIdOptional(1L);
    }

    @Test
    void createVisitor_WithInvalidData_ReturnsBadRequest() {
        when(visitorRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(visitorRepository.findByEmail(any())).thenReturn(Optional.empty());

        doThrow(new IllegalArgumentException("Invalid email format"))
                .when(visitorRepository).persist(any(Visitor.class));

        VisitorRepresentation invalidVisitorRep = new VisitorRepresentation();
        invalidVisitorRep.setEmail("invalid-email"); // μη έγκυρο email format

        Response response = visitorResource.createVisitor(invalidVisitorRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(visitorRepository, never()).persist(any(Visitor.class));
    }
    @Test
    void updateVisitor_WithValidData_UpdatesAllFieldsAndReturnsOk() {
        // Arrange
        when(visitorRepository.findByIdOptional(1L)).thenReturn(Optional.of(testVisitor));
        when(visitorRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(visitorRepository.findByEmail(any())).thenReturn(Optional.empty());

        VisitorRepresentation updateVisitorRep = new VisitorRepresentation();
        updateVisitorRep.setFirstName("UpdatedFirst");
        updateVisitorRep.setLastName("UpdatedLast");
        updateVisitorRep.setPhoneNumber("6955555555");
        updateVisitorRep.setEmail("updated@test.com");
        updateVisitorRep.setUsername("updatedUsername");
        updateVisitorRep.setPassword("updatedPassword123");

        // Act
        Response response = visitorResource.updateVisitor(1, updateVisitorRep);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        verify(visitorRepository).persist(argThat((Visitor visitor) ->
                visitor.getFirstName().equals("UpdatedFirst") &&
                        visitor.getLastName().equals("UpdatedLast") &&
                        visitor.getPhoneNumber().equals("6955555555") &&
                        visitor.getEmail().equals("updated@test.com") &&
                        visitor.getAccount().getUsername().equals("updatedUsername") &&
                        visitor.getAccount().getPassword().equals("updatedPassword123")
        ));

    }
}