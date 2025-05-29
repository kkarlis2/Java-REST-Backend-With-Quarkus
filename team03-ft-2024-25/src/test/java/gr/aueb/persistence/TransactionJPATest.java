package gr.aueb.persistence;

import gr.aueb.domain.*;
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
public class TransactionJPATest {

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
    public void testTransactionInsertion() {
        // Παίρνουμε όλα τα reservations
        List<Reservation> reservations = reservationRepository.listAll();

        // Συλλέγουμε όλα τα transactions
        List<Transaction> transactions = reservations.stream()
                .flatMap(r -> r.getTransactions().stream())
                .collect(Collectors.toList());
        assertEquals(3, transactions.size());

        // Φιλτράρουμε για payments
        List<Payment> payments = transactions.stream()
                .filter(t -> t instanceof Payment)
                .map(t -> (Payment) t)
                .collect(Collectors.toList());
        assertEquals(2, payments.size());

        Payment payment = payments.get(0);
        assertNotNull(payment);
        assertEquals(TransactionStatus.SUCCESS, payment.getStatus());
        assertEquals(40.00, payment.getAmount());

        // Φιλτράρουμε για refunds
        List<Refund> refunds = transactions.stream()
                .filter(t -> t instanceof Refund)
                .map(t -> (Refund) t)
                .collect(Collectors.toList());
        assertEquals(1, refunds.size());

        Refund refund = refunds.get(0);
        assertNotNull(refund);
        assertEquals(TransactionStatus.SUCCESS, refund.getStatus());
        assertTrue(refund.getRefundAmount() > 0);
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testTransactionStatus() {
        List<Transaction> transactions = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .collect(Collectors.toList());

        for (Transaction transaction : transactions) {
            assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        }
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.Payment;
//import gr.aueb.domain.Refund;
//import gr.aueb.domain.Transaction;
//import gr.aueb.domain.TransactionStatus;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static io.smallrye.common.constraint.Assert.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class TransactionJPATest extends JPATest {
//
//    @Test
//    public void testTransactionInsertion() {
//        List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
//        assertEquals(3, transactions.size());
//
//        List<Payment> payments = em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
//        assertEquals(2, payments.size());
//
//        Payment payment = payments.get(0);
//        assertNotNull(payment);
//        assertEquals(TransactionStatus.SUCCESS, payment.getStatus());
//        assertEquals(40.00, payment.getAmount());
//
//        List<Refund> refunds = em.createQuery("SELECT r FROM Refund r", Refund.class).getResultList();
//        assertEquals(1, refunds.size());
//
//        Refund refund = refunds.get(0);
//        assertNotNull(refund);
//        assertEquals(TransactionStatus.SUCCESS, refund.getStatus());
//        assertTrue(refund.getRefundAmount() > 0);
//    }
//
//    @Test
//    public void testTransactionStatus() {
//        List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
//
//        for (Transaction transaction : transactions) {
//            assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
//        }
//    }
//}
//
