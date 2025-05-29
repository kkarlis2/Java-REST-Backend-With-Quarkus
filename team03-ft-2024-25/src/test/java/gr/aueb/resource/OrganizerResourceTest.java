package gr.aueb.resource;

import gr.aueb.domain.*;
import gr.aueb.persistence.OrganizerRepository;
import gr.aueb.representation.OrganizerMapper;
import gr.aueb.representation.OrganizerRepresentation;
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
public class OrganizerResourceTest {

    @InjectMock
    OrganizerRepository organizerRepository;

    @Inject
    OrganizerMapper organizerMapper;

    @Inject
    OrganizerResource organizerResource;

    private Organizer testOrganizer;
    private OrganizerRepresentation testOrganizerRep;

    @BeforeEach
    void setUp() {
        testOrganizer = new Organizer(
                "222222222",         // taxId
                "TestO Brand",        // brandName
                "6977777777",        // phoneNumber
                "testOuser",          // username
                "password123",       // password
                "Organizer@resource.com",     // email
                "TestO Street",       // street
                "897",              // number
                new ZipCode("33333") // zipCode
        );
        testOrganizer.setId(1);

        testOrganizerRep = organizerMapper.toRepresentation(testOrganizer);
    }

    @Test
    void getAllOrganizers_ReturnsOrganizersList() {
        when(organizerRepository.listAll()).thenReturn(Arrays.asList(testOrganizer));

        Response response = organizerResource.getAllOrganizers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getOrganizerById_WhenExists_ReturnsOrganizer() {
        when(organizerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrganizer));

        Response response = organizerResource.getOrganizerById(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getOrganizerById_WhenNotExists_Returns404() {
        when(organizerRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = organizerResource.getOrganizerById(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void getOrganizerByTaxId_WhenExists_ReturnsOrganizer() {
        when(organizerRepository.findByTaxId("123456789")).thenReturn(Optional.of(testOrganizer));

        Response response = organizerResource.getOrganizerByTaxId("123456789");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getOrganizerByTaxId_WhenNotExists_Returns404() {
        when(organizerRepository.findByTaxId("999999999")).thenReturn(Optional.empty());

        Response response = organizerResource.getOrganizerByTaxId("999999999");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void createOrganizer_WithValidData_ReturnsCreated() {
        when(organizerRepository.findByTaxId(any())).thenReturn(Optional.empty());
        when(organizerRepository.findByUsername(any())).thenReturn(Optional.empty());

        Response response = organizerResource.createOrganizer(testOrganizerRep);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(organizerRepository).persist(any(Organizer.class));
    }

    @Test
    void createOrganizer_WithExistingTaxId_ReturnsConflict() {
        when(organizerRepository.findByTaxId(testOrganizerRep.getTaxId()))
                .thenReturn(Optional.of(testOrganizer));

        Response response = organizerResource.createOrganizer(testOrganizerRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        verify(organizerRepository, never()).persist(any(Organizer.class));
    }

    @Test
    void createOrganizer_WithExistingUsername_ReturnsConflict() {
        when(organizerRepository.findByTaxId(any())).thenReturn(Optional.empty());
        when(organizerRepository.findByUsername(testOrganizerRep.getUsername()))
                .thenReturn(Optional.of(testOrganizer));

        Response response = organizerResource.createOrganizer(testOrganizerRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        verify(organizerRepository, never()).persist(any(Organizer.class));
    }

    @Test
    void updateOrganizer_WithValidData_ReturnsUpdated() {
        when(organizerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrganizer));
        when(organizerRepository.findByTaxId(any())).thenReturn(Optional.empty());
        when(organizerRepository.findByUsername(any())).thenReturn(Optional.empty());

        testOrganizerRep.setBrandName("Updated Brand");
        Response response = organizerResource.updateOrganizer(1, testOrganizerRep);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(organizerRepository).persist(any(Organizer.class));
    }

    @Test
    void updateOrganizer_WithNonExistingId_Returns404() {
        when(organizerRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = organizerResource.updateOrganizer(999, testOrganizerRep);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(organizerRepository, never()).persist(any(Organizer.class));
    }

    @Test
    void updateOrganizer_WithExistingTaxId_ReturnsConflict() {
        Organizer otherOrganizer = new Organizer(
                "987654321",         // different taxId
                "Other Brand",       // different brand
                "9876543210",        // different phone
                "otheruser",         // different username
                "password456",       // different password
                "other@test.com",    // different email
                "Other Street",      // different street
                "456",              // different number
                new ZipCode("54321") // different zipCode
        );
        otherOrganizer.setId(2);

        when(organizerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrganizer));
        when(organizerRepository.findByTaxId(testOrganizerRep.getTaxId()))
                .thenReturn(Optional.of(otherOrganizer));

        Response response = organizerResource.updateOrganizer(1, testOrganizerRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        verify(organizerRepository, never()).persist(any(Organizer.class));
    }

    @Test
    void deleteOrganizer_WhenExists_ReturnsNoContent() {
        when(organizerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrganizer));

        Response response = organizerResource.deleteOrganizer(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(organizerRepository).delete(testOrganizer);
    }

    @Test
    void deleteOrganizer_WhenNotExists_Returns404() {
        when(organizerRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = organizerResource.deleteOrganizer(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(organizerRepository, never()).delete(any(Organizer.class));
    }

    @Test
    void createOrganizer_WithInvalidData_ReturnsBadRequest() {
        // Δημιουργούμε ένα αντίγραφο του testOrganizerRep με μη έγκυρα δεδομένα
        OrganizerRepresentation invalidOrganizerRep = new OrganizerRepresentation();
        invalidOrganizerRep.setTaxId(""); // άκυρο taxId

        when(organizerRepository.findByTaxId(any())).thenReturn(Optional.empty());
        when(organizerRepository.findByUsername(any())).thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(organizerRepository).persist(any(Organizer.class));

        Response response = organizerResource.createOrganizer(invalidOrganizerRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void updateOrganizer_WithInvalidData_ReturnsBadRequest() {
        // Ρυθμίζουμε το mock για να βρει τον υπάρχοντα organizer
        when(organizerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrganizer));
        when(organizerRepository.findByTaxId(any())).thenReturn(Optional.empty());
        when(organizerRepository.findByUsername(any())).thenReturn(Optional.empty());

        // Δημιουργούμε ένα αντίγραφο με μη έγκυρα δεδομένα
        OrganizerRepresentation invalidOrganizerRep = new OrganizerRepresentation();
        invalidOrganizerRep.setTaxId(""); // άκυρο taxId

        // Ρυθμίζουμε το persist να ρίξει exception
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(organizerRepository).persist(any(Organizer.class));

        Response response = organizerResource.updateOrganizer(1, invalidOrganizerRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void updateOrganizer_WithExistingUsername_ReturnsConflict() {
        Organizer otherOrganizer = new Organizer(
                "999888777",         // different taxId
                "Different Brand",    // different brand
                "6944444444",        // different phone
                testOrganizerRep.getUsername(), // same username
                "password789",       // different password
                "different@test.com", // different email
                "Different Street",   // different street
                "789",               // different number
                new ZipCode("11111") // different zipCode
        );
        otherOrganizer.setId(2);

        when(organizerRepository.findByIdOptional(1L)).thenReturn(Optional.of(testOrganizer));
        when(organizerRepository.findByTaxId(any())).thenReturn(Optional.empty());
        when(organizerRepository.findByUsername(testOrganizerRep.getUsername()))
                .thenReturn(Optional.of(otherOrganizer));

        Response response = organizerResource.updateOrganizer(1, testOrganizerRep);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        verify(organizerRepository, never()).persist(any(Organizer.class));
    }


}