package gr.aueb.domain;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RefundTest {
    private Event event;
    private TicketZone ticketZone;
    private Reservation reservation;
    private Visitor visitor;
    @Test
    void testRefundConstructor() {

        event=new Event();
        Visitor visitor=new Visitor();
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);
        LocalDateTime now = LocalDateTime.now();

        Refund refund = new Refund(reservation, now, TransactionStatus.SUCCESS, 10.0);

        assertNotNull(refund);
        assertEquals(reservation, refund.getReservation());
        assertEquals(TransactionStatus.SUCCESS, refund.getStatus());
        assertEquals(90.0, refund.getRefundAmount());
        assertEquals(10.0, refund.getFeePercentage());
    }

    @Test
    void testRefundAmountCalculation() {
        event=new Event();
        Visitor visitor=new Visitor();
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);
        LocalDateTime now = LocalDateTime.now();

        Refund refund = new Refund(reservation, now, TransactionStatus.SUCCESS, 25.0);
        assertEquals(75, refund.getRefundAmount());

        refund.setFeePercentage(50.0);
        assertEquals(50.0, refund.getRefundAmount());

        refund.setFeePercentage(0.0);
        assertEquals(100.0, refund.getRefundAmount());

    }

    @Test
    void testFeePercentageValidation() {
        Reservation reservation = new Reservation();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {new Refund(reservation, now,  TransactionStatus.SUCCESS,  -1.0);});

        assertThrows(IllegalArgumentException.class, () -> {new Refund(reservation, now, TransactionStatus.SUCCESS, 101.0);});
    }

    @Test
    void testSetFeePercentageUpdatesRefundAmount() {
        event=new Event();
        Visitor visitor=new Visitor();
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);
        LocalDateTime now = LocalDateTime.now();

        Refund refund = new Refund(reservation, now,  TransactionStatus.SUCCESS, 10.0);
        assertEquals(90.0, refund.getRefundAmount());

        refund.setFeePercentage(20.0);
        assertEquals(80.0, refund.getRefundAmount());

        refund.setFeePercentage(0.0);
        assertEquals(100.0, refund.getRefundAmount());

        assertThrows(IllegalArgumentException.class, () -> refund.setFeePercentage(null));
    }

    @Test
    void testRefundAmountMustBePositive() {
        Reservation reservation = new Reservation();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            new Refund(reservation, now,  TransactionStatus.SUCCESS, 110.0);
        });
    }

    @Test
    void testEqualsAndHashCode() {
        Visitor visitor = new Visitor("TASOS", "KOURSOS", "1234567890", "tasoskour@aueb.gr", "tasos_kour", "password");
        TicketZone ticketZone = new TicketZone(100.00, Category.VIP,50, 50, new Event());
        LocalDateTime now = LocalDateTime.now();

        Reservation reservation1 = new Reservation(visitor, ticketZone, 2, now, ReservationStatus.CONFIRMED,null);
        Reservation reservation2 = new Reservation(visitor, ticketZone, 3, now.plusDays(1), ReservationStatus.PENDING,null);


        Refund refund1 = new Refund(reservation1, now,  TransactionStatus.SUCCESS, 10.0);
        Refund refund2 = new Refund(reservation1, now,  TransactionStatus.SUCCESS, 10.0);
        Refund refund3 = new Refund(reservation1, now,  TransactionStatus.SUCCESS, 20.0);
        Refund refund4 = new Refund(reservation2, now,  TransactionStatus.SUCCESS, 10.0);

        assertEquals(refund1, refund2);
        assertEquals(refund1.hashCode(), refund2.hashCode());
        assertNotEquals(refund1, refund3);
        assertNotEquals(refund1.hashCode(), refund3.hashCode());
        assertNotEquals(refund1, refund4);

        assertTrue(refund1.equals(refund1));
        assertFalse(refund1.equals("A String"));
        assertFalse(refund1.equals(null));
    }


}

