package gr.aueb.representation;

import gr.aueb.domain.Visitor;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import gr.aueb.domain.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BookingHistoryMapperTest {

    @Inject
    BookingHistoryMapper mapper;

    private Visitor visitor;
    private Event event;
    private TicketZone ticketZone;
    private Reservation reservation;
    private BookingHistory bookingHistory;
    private Organizer organizer;

    @BeforeEach
    void setUp() {
        // Setup test data
        organizer = new Organizer();

        event = new Event(
                "Test Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );

        ticketZone = new TicketZone(100.0, Category.VIP,50, 50, event);
        event.addTicketZone(ticketZone);

        visitor = new Visitor(
                "John",
                "Doe",
                "1234567890",
                "john@example.com",
                "johndoe",
                "password123"
        );

        reservation = new Reservation(
                visitor,
                ticketZone,
                2,
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED,
                DiscountCat.STUDENT
        );

        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        bookingHistory = new BookingHistory(visitor, reservations);
    }

    @Test
    void whenMappingValidBookingHistory_thenAllFieldsAreMappedCorrectly() {
        // Act
        BookingHistoryRepresentation result = mapper.toRepresentation(bookingHistory);

        // Assert
        assertNotNull(result);
        assertNotNull(result.reservations);
        assertEquals(1, result.reservations.size());

        BookingHistoryRepresentation.ReservationInfo info = result.reservations.get(0);
        assertEquals(reservation.getId(), info.reservationId);
        assertEquals(event.getTitle(), info.eventTitle);
        assertEquals(event.getDate(), info.eventDate);
        assertEquals(event.getTime(), info.eventTime);
        assertEquals(reservation.getReservedSeats(), info.ticketCount);
        assertEquals(Category.VIP.name(), info.ticketCategory);
        assertEquals(reservation.calculateTotalCost(), info.totalAmount, 0.001);
        assertEquals(ReservationStatus.CONFIRMED, info.status);
    }

    @Test
    void whenMappingEmptyBookingHistory_thenReturnsEmptyList() {
        // Arrange
        BookingHistory emptyHistory = new BookingHistory(visitor, new ArrayList<>());

        // Act
        BookingHistoryRepresentation result = mapper.toRepresentation(emptyHistory);

        // Assert
        assertNotNull(result);
        assertNotNull(result.reservations);
        assertTrue(result.reservations.isEmpty());
    }

    @Test
    void whenMappingMultipleReservations_thenAllReservationsAreMappedCorrectly() {
        // Arrange
        List<Reservation> multipleReservations = new ArrayList<>();
        multipleReservations.add(reservation);

        // Add another reservation with different category
        TicketZone simpleZone = new TicketZone(50.0, Category.SIMPLE,100, 100, event);
        event.addTicketZone(simpleZone);

        Reservation secondReservation = new Reservation(
                visitor,
                simpleZone,
                3,
                LocalDateTime.now(),
                ReservationStatus.PENDING,
                null
        );
        multipleReservations.add(secondReservation);

        BookingHistory historyWithMultiple = new BookingHistory(visitor, multipleReservations);

        // Act
        BookingHistoryRepresentation result = mapper.toRepresentation(historyWithMultiple);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.reservations.size());

        // Verify first reservation
        BookingHistoryRepresentation.ReservationInfo firstInfo = result.reservations.get(0);
        assertEquals(Category.VIP.name(), firstInfo.ticketCategory);
        assertEquals(2, firstInfo.ticketCount);
        assertEquals(ReservationStatus.CONFIRMED, firstInfo.status);

        // Verify second reservation
        BookingHistoryRepresentation.ReservationInfo secondInfo = result.reservations.get(1);
        assertEquals(Category.SIMPLE.name(), secondInfo.ticketCategory);
        assertEquals(3, secondInfo.ticketCount);
        assertEquals(ReservationStatus.PENDING, secondInfo.status);
    }

    @Test
    void whenMappingReservationWithDiscount_thenTotalAmountIsCalculatedCorrectly() {
        // Student discount should be 20% off
        BookingHistoryRepresentation result = mapper.toRepresentation(bookingHistory);

        BookingHistoryRepresentation.ReservationInfo info = result.reservations.get(0);
        double expectedAmount = 100.0 * 2 * 0.8; // price * seats * student discount
        assertEquals(expectedAmount, info.totalAmount, 0.001);
    }

    @Test
    void whenMappingReservationWithMinimalFields_thenMapsCorrectly() {
        // Arrange
        Event minimalEvent = new Event(
                "Minimal Event",  // Title cannot be null due to validation
                LocalDate.now(),
                LocalTime.now(),
                "Some Location", // Location cannot be null due to validation
                "Basic Description", // Description cannot be null due to validation
                EventType.CONCERT,
                organizer
        );

        TicketZone minimalZone = new TicketZone(100.0, Category.VIP,50, 50, minimalEvent);

        Reservation minimalReservation = new Reservation(
                visitor,
                minimalZone,
                1,
                LocalDateTime.now(),
                ReservationStatus.PENDING,
                null  // Testing with null discount
        );

        List<Reservation> reservations = new ArrayList<>();
        reservations.add(minimalReservation);

        BookingHistory historyWithMinimal = new BookingHistory(visitor, reservations);

        // Act
        BookingHistoryRepresentation result = mapper.toRepresentation(historyWithMinimal);

        // Assert
        assertNotNull(result);
        assertNotNull(result.reservations);
        assertEquals(1, result.reservations.size());

        BookingHistoryRepresentation.ReservationInfo info = result.reservations.get(0);
        assertEquals("Minimal Event", info.eventTitle);
        assertNotNull(info.eventDate);
        assertNotNull(info.eventTime);
        assertEquals(ReservationStatus.PENDING, info.status);
        assertEquals(1, info.ticketCount);
        assertEquals(Category.VIP.name(), info.ticketCategory);
        // Verify the total amount without discount
        assertEquals(100.0, info.totalAmount, 0.001); // price * seats with no discount
    }
    @Test
    void testBookingHistoryRepresentationGettersSetters() {
        // Arrange
        BookingHistoryRepresentation historyRep = new BookingHistoryRepresentation();
        List<BookingHistoryRepresentation.ReservationInfo> reservationsList = new ArrayList<>();

        // Act
        historyRep.setReservations(reservationsList);

        // Assert
        assertEquals(reservationsList, historyRep.getReservations());
    }

    @Test
    void testReservationInfoGettersSetters() {
        // Arrange
        BookingHistoryRepresentation.ReservationInfo info = new BookingHistoryRepresentation.ReservationInfo();

        // Test data
        Integer reservationId = 1;
        String eventTitle = "Test Event";
        LocalDate eventDate = LocalDate.now();
        LocalTime eventTime = LocalTime.now();
        int ticketCount = 2;
        String ticketCategory = "VIP";
        double totalAmount = 100.0;
        ReservationStatus status = ReservationStatus.CONFIRMED;

        // Act
        info.setReservationId(reservationId);
        info.setEventTitle(eventTitle);
        info.setEventDate(eventDate);
        info.setEventTime(eventTime);
        info.setTicketCount(ticketCount);
        info.setTicketCategory(ticketCategory);
        info.setTotalAmount(totalAmount);
        info.setStatus(status);

        // Assert
        assertEquals(reservationId, info.getReservationId());
        assertEquals(eventTitle, info.getEventTitle());
        assertEquals(eventDate, info.getEventDate());
        assertEquals(eventTime, info.getEventTime());
        assertEquals(ticketCount, info.getTicketCount());
        assertEquals(ticketCategory, info.getTicketCategory());
        assertEquals(totalAmount, info.getTotalAmount(), 0.001);
        assertEquals(status, info.getStatus());
    }
}
