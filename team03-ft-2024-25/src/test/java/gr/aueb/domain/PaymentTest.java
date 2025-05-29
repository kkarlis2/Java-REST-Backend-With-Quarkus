package gr.aueb.domain;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class PaymentTest {

    private Event event;
    private TicketZone ticketZone;
    private Reservation reservation;
    private Visitor visitor;

    @Test
    void testPaymentConstructor() {
        event=new Event();
        visitor=new Visitor();
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100,event);
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);

        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment(reservation, now, TransactionStatus.SUCCESS, "1234567812345678",
                "TASOS KOURSOS", now.plusYears(1), 123);

        assertNotNull(payment, "Payment should be created");
        assertEquals(reservation, payment.getReservation(), "Reservation should match");
        assertEquals(100.0, payment.calculateAmount(), "Amount should match");
        assertEquals(TransactionStatus.SUCCESS, payment.getStatus(), "Status should match");
        assertEquals("1234567812345678", payment.getCardNumber(), "Card number should match");
        assertEquals("TASOS KOURSOS", payment.getCardHolderName(), "Card holder name should match");
        assertEquals(now.plusYears(1), payment.getExpiryDate(), "Expiry date should match");
        assertEquals(123, payment.getCvv(), "CVV should match");
    }

    @Test
    void testCardNumberValidation() {
        Reservation reservation = new Reservation();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "123", "TASOS KOURSOS",now.plusYears(1), 123);});
        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                null, "TASOS KOURSOS",now.plusYears(1), 123);});
    }

    @Test
    void testcardHolderNameValidation(){
        Reservation reservation = new Reservation();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", "", now.plusYears(1), 123);});
        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", null, now.plusYears(1), 123);});
    }

    @Test
    void testExpirydateValidation(){
        Reservation reservation = new Reservation();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS",now.minusDays(1), 123);});
        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS",null, 123);});
    }

    @Test
    void testCVVvalidation(){
        Reservation reservation = new Reservation();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS",now.plusYears(1), 12);});
        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS",now.plusYears(1), 1000);});
        assertThrows(IllegalArgumentException.class, () -> {new Payment(reservation, now,TransactionStatus.SUCCESS,
                "1234567812345678", "TASOS KOURSOS",now.plusYears(1), null);});
    }

    @Test
    void testEqualsAndHashCode() {
        event = new Event();
        visitor = new Visitor();
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100, event);
        reservation = new Reservation(visitor, ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING, null);

        LocalDateTime now = LocalDateTime.now();

        Payment payment1 = new Payment(reservation, now, TransactionStatus.SUCCESS, "1234567812345678", "TASOS KOURSOS", now.plusYears(1), 123);
        Payment payment2 = new Payment(reservation, now, TransactionStatus.SUCCESS, "1234567812345678", "TASOS KOURSOS", now.plusYears(1), 123);

        assertTrue(payment1.equals(payment1));
        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());

        payment2.setCardNumber("0000111122223333");
        assertNotEquals(payment1, payment2);
        assertNotEquals(payment1.hashCode(), payment2.hashCode());

        payment2.setCardNumber("1234567812345678");
        payment2.setCardHolderName("DIFFERENT NAME");
        assertNotEquals(payment1, payment2);

        payment2.setCardHolderName("TASOS KOURSOS"); // Reset holder name
        payment2.setExpiryDate(now.plusYears(2));
        assertNotEquals(payment1, payment2);
        assertNotEquals(payment1.hashCode(), payment2.hashCode());

        payment2.setExpiryDate(now.plusYears(1));
        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());

        assertFalse(payment1.equals(null));
        assertFalse(payment1.equals("A String"));
    }



}

