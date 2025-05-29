package gr.aueb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingHistoryTest {
    private Visitor visitor;
    private List<Reservation> reservations;
    private BookingHistory bookingHistory;
    private TicketZone ticketZone;
    private Event event;

    @BeforeEach
    void setUp() {
        // Setup visitor with account
        visitor = new Visitor("John", "Doe", "1234567890", "john@example.com", "johndoe", "password123");

        // Setup event and ticket zone
        event = new Event(); // Add necessary event details
        ticketZone = new TicketZone(50.0, Category.VIP,100, 100, event);

        // Setup initial reservation
        Reservation reservation = new Reservation(
                visitor,
                ticketZone,
                2,
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED,
                null
        );

        reservations = new ArrayList<>();
        reservations.add(reservation);

        bookingHistory = new BookingHistory(visitor, reservations);
    }

    @Test
    void getReservations_WithValidAccount_ReturnsReservationsList() {
        // Act
        List<Reservation> result = bookingHistory.getReservations();

        // Assert
        assertNotNull(result);
        assertEquals(reservations, result);
    }

    @Test
    void getReservations_WithNoAccount_ThrowsIllegalStateException() {
        // Arrange
        visitor.setAccount(null);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            bookingHistory.getReservations();
        });
    }

    @Test
    void cancelReservation_ValidReservation_UpdatesStatusAndSeats() {
        // Arrange
        Reservation reservation = reservations.get(0);
        int initialAvailableSeats = ticketZone.getAvailableSeats();

        // Act
        bookingHistory.cancelReservation(reservation);

        // Assert
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(initialAvailableSeats + reservation.getReservedSeats(),
                ticketZone.getAvailableSeats());
    }

    @Test
    void cancelReservation_NonExistentReservation_ThrowsIllegalArgumentException() {
        // Arrange
        Reservation nonExistentReservation = new Reservation(
                visitor,
                ticketZone,
                1,
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED,
                null
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookingHistory.cancelReservation(nonExistentReservation);
        });
    }

    @Test
    void cancelReservation_AlreadyCancelledReservation_ThrowsIllegalStateException() {
        // Arrange
        Reservation reservation = reservations.get(0);
        reservation.setStatus(ReservationStatus.CANCELLED);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            bookingHistory.cancelReservation(reservation);
        });
    }

    @Test
    void cancelReservation_UpdatesTicketZoneSeats() {
        // Arrange
        Reservation reservation = reservations.get(0);
        int initialSeats = ticketZone.getAvailableSeats();
        int reservedSeats = reservation.getReservedSeats();

        // Act
        bookingHistory.cancelReservation(reservation);

        // Assert
        assertEquals(initialSeats + reservedSeats, ticketZone.getAvailableSeats());
    }
}
