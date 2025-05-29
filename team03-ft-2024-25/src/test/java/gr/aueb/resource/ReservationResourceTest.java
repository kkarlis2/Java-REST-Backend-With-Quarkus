package gr.aueb.resource;

import gr.aueb.domain.*;
import gr.aueb.persistence.ReservationRepository;
import gr.aueb.persistence.VisitorRepository;
import gr.aueb.representation.ReservationMapper;
import gr.aueb.representation.ReservationRepresentation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ReservationResourceTest {

    @InjectMock
    ReservationRepository reservationRepository;

    @InjectMock
    VisitorRepository visitorRepository;

    @Inject
    ReservationResource reservationResource;

    private Reservation testReservation;
    private Visitor testVisitor;
    private TicketZone testTicketZone;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();

        // Setup test visitor
        testVisitor = new Visitor(
                "John",              // firstName
                "Doe",               // lastName
                "6944444444",        // phoneNumber
                "john@test.com",     // email
                "johndoe",           // username
                "password123"        // password
        );
        testVisitor.setId(1);

        // Setup test TicketZone
        testTicketZone = new TicketZone();
        testTicketZone.setId(1);
        testTicketZone.setAvailableSeats(100);
        testTicketZone.setCost(50.0);

        // Setup test Reservation
        testReservation = new Reservation(
                testVisitor,
                testTicketZone,
                2,                    // reservedSeats
                testDate,            // reservationDate
                ReservationStatus.PENDING,
                DiscountCat.STUDENT
        );
        testReservation.setId(1);

        // Setup basic repository mocks
        when(reservationRepository.findByIdOptional(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.findByIdOptional(999L)).thenReturn(Optional.empty());
    }

    @Test
    void getAllReservations_ReturnsReservationsList() {
        when(reservationRepository.listAll()).thenReturn(Arrays.asList(testReservation));

        Response response = reservationResource.getAllReservations();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getReservationById_WhenExists_ReturnsReservation() {
        Response response = reservationResource.getReservationById(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getReservationById_WhenNotExists_Returns404() {
        Response response = reservationResource.getReservationById(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void getReservationsByVisitor_ReturnsReservations() {
        when(reservationRepository.findByVisitorId(1)).thenReturn(Arrays.asList(testReservation));

        Response response = reservationResource.getReservationsByVisitor(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getReservationsByStatus_ReturnsReservations() {
        when(reservationRepository.findByStatus(ReservationStatus.PENDING))
                .thenReturn(Arrays.asList(testReservation));

        Response response = reservationResource.getReservationsByStatus(ReservationStatus.PENDING);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void createReservation_WithValidData_ReturnsCreated() {
        // Mock visitor repository in mapper
        when(visitorRepository.findById(1L)).thenReturn(testVisitor);

        ReservationRepresentation reservationRep = new ReservationRepresentation();
        reservationRep.reservedSeats = 2;
        reservationRep.reservationDate = testDate;
        reservationRep.status = ReservationStatus.PENDING;
        reservationRep.discount = DiscountCat.STUDENT;
        reservationRep.visitorId = 1;
        reservationRep.ticketZoneId = 1;

        Response response = reservationResource.createReservation(reservationRep);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(reservationRepository).persist(any(Reservation.class));
    }

    @Test
    void updateReservation_WhenExists_ReturnsUpdated() {
        ReservationRepresentation reservationRep = new ReservationRepresentation();
        reservationRep.reservedSeats = 3;
        reservationRep.reservationDate = testDate;
        reservationRep.status = ReservationStatus.CONFIRMED;
        reservationRep.discount = DiscountCat.PWD;

        Response response = reservationResource.updateReservation(1, reservationRep);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(reservationRepository).persist(any(Reservation.class));
    }

    @Test
    void updateReservation_WhenNotExists_Returns404() {
        ReservationRepresentation reservationRep = new ReservationRepresentation();

        Response response = reservationResource.updateReservation(999, reservationRep);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(reservationRepository, never()).persist(any(Reservation.class));
    }

    @Test
    void deleteReservation_WhenExists_ReturnsNoContent() {
        Response response = reservationResource.deleteReservation(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(reservationRepository).delete(testReservation);
    }

    @Test
    void deleteReservation_WhenNotExists_Returns404() {
        Response response = reservationResource.deleteReservation(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(reservationRepository, never()).delete(any(Reservation.class));
    }

    @Test
    void cancelReservation_WhenExists_ReturnsUpdated() {
        Response response = reservationResource.cancelReservation(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(reservationRepository).persist(any(Reservation.class));
    }

    @Test
    void cancelReservation_WhenNotExists_Returns404() {
        Response response = reservationResource.cancelReservation(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(reservationRepository, never()).persist(any(Reservation.class));
    }

    @Test
    void createReservation_WithInvalidData_ReturnsBadRequest() {
        ReservationRepresentation invalidReservationRep = new ReservationRepresentation();
        // Θέτουμε μη έγκυρα δεδομένα
        invalidReservationRep.reservedSeats = -1; // μη έγκυρος αριθμός θέσεων

        when(reservationRepository.findByIdOptional(any())).thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException("Invalid seats number"))
                .when(reservationRepository).persist(any(Reservation.class));

        Response response = reservationResource.createReservation(invalidReservationRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(reservationRepository, never()).persist(any(Reservation.class));
    }

    @Test
    void updateReservation_WithInvalidData_ReturnsBadRequest() {
        // Ρυθμίζουμε το mock για να βρει την υπάρχουσα κράτηση
        when(reservationRepository.findByIdOptional(1L)).thenReturn(Optional.of(testReservation));

        ReservationRepresentation invalidReservationRep = new ReservationRepresentation();
        invalidReservationRep.reservedSeats = -1; // μη έγκυρος αριθμός θέσεων

        doThrow(new IllegalArgumentException("Invalid seats number"))
                .when(reservationRepository).persist(any(Reservation.class));

        Response response = reservationResource.updateReservation(1, invalidReservationRep);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(reservationRepository, times(1)).findByIdOptional(1L);
    }

    @Test
    void cancelReservation_WithInvalidStateTransition_ReturnsBadRequest() {
        // Δημιουργούμε μια κράτηση που είναι ήδη ακυρωμένη
        Reservation cancelledReservation = testReservation;
        cancelledReservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findByIdOptional(1L)).thenReturn(Optional.of(cancelledReservation));
        doThrow(new IllegalArgumentException("Cannot cancel an already cancelled reservation"))
                .when(reservationRepository).persist(any(Reservation.class));

        Response response = reservationResource.cancelReservation(1);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(reservationRepository, times(1)).findByIdOptional(1L);
    }
}
