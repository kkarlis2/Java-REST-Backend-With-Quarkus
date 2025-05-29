package gr.aueb.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Visitor visitor;
    private TicketZone ticketZone;
    private Reservation reservation;
    private Event event;

    @BeforeEach
    void setUp(){
        visitor = new Visitor("Kostas", "Karlis", "1234567890", "kon.karlis@aueb.gr",
                "kkarlis2", "password123");
        Organizer organizer = new Organizer("123456789", "BarkingWell Media", "9876543210",
                "bark123", "pass", "barkingwellmedia@gmail.com", "Evelpidwn", "10",
                new ZipCode("12345"));
        event = new Event("Anna Vissi Concert", LocalDate.of(2024, 11, 25), LocalTime.of(19,0),
                "Kallimarmaro Arena", "A live show from Anna Vissi", EventType.CONCERT,organizer);
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,DiscountCat.STUDENT);

    }
    @Test
    void testReservationConstructor() {
        assertNotNull(reservation);
        assertEquals(visitor, reservation.getVisitor());
        assertEquals(ticketZone, reservation.getTicketZone());
        assertEquals(2, reservation.getReservedSeats());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(DiscountCat.STUDENT, reservation.getDiscount());

    }

    @Test
    void testAddTransactionPayment(){

        Payment payment = new Payment(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS, "1234567812345678", "KONSTANTINOS KARLIS", LocalDateTime.now().plusYears(1), 123);
        reservation.addTransaction(payment);

        double expectedAmount = ticketZone.getCost() * reservation.getReservedSeats() * 0.8; // 20% έκπτωση
        assertEquals(expectedAmount, payment.calculateAmount());

        assertEquals(1, reservation.getTransactions().size());
        assertTrue(reservation.getTransactions().contains(payment));
        assertEquals(TransactionStatus.SUCCESS, payment.getStatus());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

    }

    @Test
    void testAddTransactionNull() {
        assertThrows(IllegalArgumentException.class, () -> reservation.addTransaction(null));
    }
    @Test
    void testAddTransactionRefund() {
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING, DiscountCat.PWD);

        Payment payment = new Payment(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS", LocalDateTime.now().plusYears(1), 123);
        Refund refund = new Refund(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS, 10.0);

        reservation.addTransaction(payment);
        reservation.addTransaction(refund);

        double expectedAmount = ticketZone.getCost() * reservation.getReservedSeats() * 0.5;
        assertEquals(expectedAmount, payment.calculateAmount(), "Payment amount should reflect PWD discount");
        assertEquals(2, reservation.getTransactions().size(), "Reservation should have two transactions");
        assertTrue(reservation.getTransactions().contains(refund), "Refund should be added to the reservation");
        assertEquals(TransactionStatus.SUCCESS, refund.getStatus());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus(), "Reservation status should be CANCELLED after refund");
    }

    @Test
    void testAddMultiplePaymentsThrowsException() {
        Payment payment1 = new Payment(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS", LocalDateTime.now().plusYears(1), 123);
        Payment payment2 = new Payment(reservation, LocalDateTime.now(), TransactionStatus.PENDING,
                "8765432187654321", "KOSTAS KARLIS", LocalDateTime.now().plusYears(1), 321);

        reservation.addTransaction(payment1);

        assertThrows(IllegalArgumentException.class, () -> reservation.addTransaction(payment2));
    }

    @Test
    void testAddMultipleRefundsThrowsException() {
        Payment payment = new Payment(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS,
                "1234567812345678", "John Doe", LocalDateTime.now().plusYears(1), 123);
        Refund refund1 = new Refund(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS,
                10.0);
        Refund refund2 = new Refund(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS,
                5.0);

        reservation.addTransaction(payment);
        reservation.addTransaction(refund1);

        assertThrows(IllegalArgumentException.class, () -> reservation.addTransaction(refund2),
                "A reservation can only have one refund!");
    }
    @Test
    void testReservedSeatsMustBeGreaterThanZero() {

        assertThrows(IllegalArgumentException.class, () -> {
            new Reservation(visitor, ticketZone, 0, LocalDate.now().atTime(10, 0), ReservationStatus.PENDING,DiscountCat.STUDENT);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Reservation(visitor, ticketZone, -1, LocalDate.now().atTime(10, 0), ReservationStatus.PENDING,DiscountCat.PWD);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Reservation(visitor, ticketZone, null, LocalDate.now().atTime(10, 0), ReservationStatus.PENDING, DiscountCat.STUDENT);
        });
    }

    @Test
    void testSettersNullValidation() {
        assertThrows(IllegalArgumentException.class, () -> reservation.setVisitor(null));
        assertThrows(IllegalArgumentException.class, () -> reservation.setTicketZone(null));
        assertThrows(IllegalArgumentException.class, () -> reservation.setReservationDate(null));
        assertThrows(IllegalArgumentException.class, () -> reservation.setStatus(null));
    }

    @Test
    void testCalculateAmount() {
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING, null);
        double expectedAmount = ticketZone.getCost() * reservation.getReservedSeats();
        assertEquals(expectedAmount, reservation.calculateTotalCost(), "Amount should be calculated without discount");


        reservation.setDiscount(DiscountCat.STUDENT);
        expectedAmount = ticketZone.getCost() * reservation.getReservedSeats() * 0.8; // 20% έκπτωση
        assertEquals(expectedAmount, reservation.calculateTotalCost(), "Amount should be calculated with student discount");

        reservation.setDiscount(DiscountCat.PWD);
        expectedAmount = ticketZone.getCost() * reservation.getReservedSeats() * 0.5; // 50% έκπτωση
        assertEquals(expectedAmount, reservation.calculateTotalCost(), "Amount should be calculated with PWD discount");


    }

    @Test
    void testTransactionTypePayment() {
        Payment payment = new Payment(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS, "1234567812345678", "KONSTANTINOS KARLIS", LocalDateTime.now().plusYears(1), 123);
        reservation.addTransaction(payment);

        assertEquals(1, reservation.getTransactions().size());
        assertTrue(reservation.getTransactions().contains(payment));
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    void testEqualsWithDifferentFields() {
        Visitor differentVisitor = new Visitor("Different", "Visitor", "9876543210", "diff.visitor@aueb.gr", "diffVisitor", "password");
        Reservation differentVisitorReservation = new Reservation(
                differentVisitor,
                ticketZone,
                reservation.getReservedSeats(),
                reservation.getReservationDate(),
                reservation.getStatus(),
                reservation.getDiscount()
        );
        assertNotEquals(reservation, differentVisitorReservation);

        TicketZone differentTicketZone = new TicketZone(100.00, Category.ARENA,50, 50, event);
        Reservation differentTicketZoneReservation = new Reservation(
                visitor,
                differentTicketZone,
                reservation.getReservedSeats(),
                reservation.getReservationDate(),
                reservation.getStatus(),
                reservation.getDiscount()
        );
        assertNotEquals(reservation, differentTicketZoneReservation);

        Reservation differentDateReservation = new Reservation(
                visitor,
                ticketZone,
                reservation.getReservedSeats(),
                reservation.getReservationDate().plusDays(1),
                reservation.getStatus(),
                reservation.getDiscount()
        );
        assertNotEquals(reservation, differentDateReservation);

        Reservation differentSeatsReservation = new Reservation(
                visitor,
                ticketZone,
                reservation.getReservedSeats() + 1,
                reservation.getReservationDate(),
                reservation.getStatus(),
                reservation.getDiscount()
        );
        assertNotEquals(reservation, differentSeatsReservation);

        Reservation differentStatusReservation = new Reservation(
                visitor,
                ticketZone,
                reservation.getReservedSeats(),
                reservation.getReservationDate(),
                ReservationStatus.CONFIRMED, 
                reservation.getDiscount()
        );
        assertNotEquals(reservation, differentStatusReservation);

        Reservation differentDiscountReservation = new Reservation(
                visitor,
                ticketZone,
                reservation.getReservedSeats(),
                reservation.getReservationDate(),
                reservation.getStatus(),
                DiscountCat.PWD
        );
        assertNotEquals(reservation, differentDiscountReservation);
    }


}
