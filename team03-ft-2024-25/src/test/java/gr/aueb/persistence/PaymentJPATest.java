package gr.aueb.persistence;

import gr.aueb.domain.Payment;
import gr.aueb.domain.Reservation;
import gr.aueb.domain.TransactionStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentJPATest {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    Initializer initializer;

    @BeforeAll
    @Transactional
    @ActivateRequestContext
    void init() {
        initializer.prepareData();
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testPaymentInsertion() {
        List<Payment> payments = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .filter(t -> t instanceof Payment)
                .map(t -> (Payment) t)
                .collect(Collectors.toList());

        assertEquals(2, payments.size());

        Payment payment = payments.get(0);
        assertNotNull(payment);
        assertEquals(40.00, payment.getAmount(), 0.01);
        assertEquals(TransactionStatus.SUCCESS, payment.getStatus());

        assertEquals("1234567890987654", payment.getCardNumber());
        assertEquals("KARLIS KOSTAS", payment.getCardHolderName());
        assertNotNull(payment.getExpiryDate());
        assertEquals(456, payment.getCvv());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testPaymentAssociationWithReservation() {
        Payment payment = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .filter(t -> t instanceof Payment)
                .map(t -> (Payment) t)
                .filter(p -> p.getCardNumber().equals("1234567890987654"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Payment not found"));

        assertNotNull(payment);

        Reservation reservation = payment.getReservation();
        assertNotNull(reservation);
        assertEquals(2, reservation.getReservedSeats());
        assertEquals(40.00, payment.getAmount());
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.Payment;
//import gr.aueb.domain.Reservation;
//import gr.aueb.domain.TransactionStatus;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class PaymentJPATest extends JPATest {
//
//    @Test
//    public void testPaymentInsertion() {
//        List<Payment> payments = em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
//        assertEquals(2, payments.size());
//
//        Payment payment = payments.get(0);
//        assertNotNull(payment);
//        assertEquals(40.00, payment.getAmount(), 0.01);
//        assertEquals(TransactionStatus.SUCCESS, payment.getStatus());
//
//        assertEquals("1234567890987654", payment.getCardNumber());
//        assertEquals("KARLIS KOSTAS", payment.getCardHolderName());
//        assertNotNull(payment.getExpiryDate());
//        assertEquals(456, payment.getCvv());
//    }
//
//    @Test
//    public void testPaymentAssociationWithReservation() {
//        List<Payment> payments = em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
//
//        assertFalse(payments.isEmpty());
//
//        Payment payment = payments.stream()
//                .filter(p -> p.getCardNumber().equals("1234567890987654"))
//                .findFirst()
//                .orElse(null);
//
//        assertNotNull(payment);
//
//        Reservation reservation = payment.getReservation();
//        assertNotNull(reservation);
//        assertEquals(2, reservation.getReservedSeats());
//        assertEquals(40.00, payment.getAmount());
//    }
//
//
//}
//
