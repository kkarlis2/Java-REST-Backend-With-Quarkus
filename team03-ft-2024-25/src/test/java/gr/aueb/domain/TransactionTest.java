package gr.aueb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Event event;
    private TicketZone ticketZone;
    private Reservation reservation;
    private Visitor visitor;
    private Payment transaction;
    private Email email;

    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();
        event = new Event();
        email = new Email("vagzyg@aueb.gr");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                "vagg", "password123");
        ticketZone = new TicketZone(50.00, Category.VIP,100, 100, event);
        reservation = new Reservation(visitor, ticketZone, 2, now, ReservationStatus.PENDING, null);
        transaction = new Payment(reservation, now, TransactionStatus.SUCCESS, "1234567812345678", "TASOS KOURSOS", now.plusYears(1), 123);
    }

    @Test
    void testTransactionConstructor() {
        assertNotNull(transaction);
        assertEquals(reservation, transaction.getReservation());
        assertEquals(100.0, transaction.calculateAmount());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertNotNull(transaction.getTransactionDate());
    }

    @Test
    void testSetAmountValidValue() {
        transaction.setAmount(100.0);
        assertEquals(100.0, transaction.getAmount());
    }

    @Test
    void testTransactionDateCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment(reservation, null, TransactionStatus.SUCCESS, "1234567812345678", "TASOS KOURSOS", LocalDateTime.now().plusYears(1), 123);
        });
    }

    @Test
    void testTransactionStatusCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment(reservation, LocalDateTime.now(), null, "1234567812345678", "TASOS KOURSOS", LocalDateTime.now().plusYears(1), 123);
        });
    }

    @Test
    void testReservationCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Payment(null, LocalDateTime.now(), TransactionStatus.SUCCESS, "1234567812345678", "TASOS KOURSOS", LocalDateTime.now().plusYears(1), 123);
        });
    }

    @Test
    void testAmountCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> transaction.setAmount(null));
    }

    @Test
    void testAmountCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> transaction.setAmount(-10.0));
    }

    @Test
    void testAmountCannotBeZero() {
        assertThrows(IllegalArgumentException.class, () -> transaction.setAmount(0.0));
    }

    @Test
    void testPrePersist() {
        Payment newTransaction = new Payment(reservation, LocalDateTime.now(), TransactionStatus.SUCCESS, "8765432187654321", "Another User", LocalDateTime.now().plusYears(1), 456);
        newTransaction.prePersist();
        assertEquals(100.0, newTransaction.getAmount());
    }


    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction1 = new Transaction(reservation, now, TransactionStatus.SUCCESS) {
            @Override
            public Double calculateAmount() {
                return 100.0;
            }
        };

        Transaction transaction2 = new Transaction(reservation, now, TransactionStatus.SUCCESS) {
            @Override
            public Double calculateAmount() {
                return 100.0;
            }
        };

        Transaction transaction3 = new Transaction(reservation, now, TransactionStatus.PENDING) {
            @Override
            public Double calculateAmount() {
                return 100.0;
            }
        };

        assertTrue(transaction1.equals(transaction1));
        assertFalse(transaction1.equals(null));
        assertFalse(transaction1.equals("Not a Transaction"));
        assertTrue(transaction1.equals(transaction2));
        assertFalse(transaction1.equals(transaction3));

        transaction3.setTransactionDate(now.plusDays(1));
        assertFalse(transaction1.equals(transaction3));

        assertEquals(transaction1.hashCode(), transaction2.hashCode());
        assertNotEquals(transaction1.hashCode(), transaction3.hashCode());
    }


}
