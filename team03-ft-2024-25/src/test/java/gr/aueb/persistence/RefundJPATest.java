package gr.aueb.persistence;

import gr.aueb.domain.Refund;
import gr.aueb.domain.Reservation;
import gr.aueb.domain.ReservationStatus;
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
public class RefundJPATest {

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
    public void testRefundInsertion() {
        // Παίρνουμε τα refunds μέσω των reservations
        List<Refund> refunds = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .filter(t -> t instanceof Refund)
                .map(t -> (Refund) t)
                .collect(Collectors.toList());

        assertEquals(1, refunds.size(), "There should be 1 refund in the database");

        Refund refund = refunds.get(0);
        assertNotNull(refund, "Refund should not be null");
        assertEquals(TransactionStatus.SUCCESS, refund.getStatus(), "Refund status should be SUCCESS");
        assertEquals(5.00, refund.getFeePercentage(), 0.01, "Fee percentage should match");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testRefundChangesReservationStatus() {
        Refund refund = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .filter(t -> t instanceof Refund)
                .map(t -> (Refund) t)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No refund found"));

        Reservation reservation = refund.getReservation();
        assertNotNull(reservation);
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testRefundAmountCalculation() {
        Refund refund = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .filter(t -> t instanceof Refund)
                .map(t -> (Refund) t)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No refund found"));

        double expectedRefundAmount = refund.getReservation().calculateTotalCost() *
                (1 - refund.getFeePercentage() / 100);

        assertEquals(expectedRefundAmount, refund.getRefundAmount(), 0.01,
                "Refund amount should be calculated correctly");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testRefundAssociationWithReservation() {
        Refund refund = reservationRepository.listAll().stream()
                .flatMap(r -> r.getTransactions().stream())
                .filter(t -> t instanceof Refund)
                .map(t -> (Refund) t)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No refund found"));

        Reservation reservation = refund.getReservation();
        assertNotNull(reservation);
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertTrue(reservation.getTransactions().contains(refund));
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.Refund;
//import gr.aueb.domain.Reservation;
//import gr.aueb.domain.ReservationStatus;
//import gr.aueb.domain.TransactionStatus;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class RefundJPATest extends JPATest {
//
//    @Test
//    public void testRefundInsertion() {
//        // Έλεγχος ότι υπάρχει τουλάχιστον μία επιστροφή
//        List<Refund> refunds = em.createQuery("SELECT r FROM Refund r", Refund.class).getResultList();
//        assertEquals(1, refunds.size(), "There should be 1 refund in the database");
//
//        Refund refund = refunds.get(0);
//        assertNotNull(refund, "Refund should not be null");
//        assertEquals(TransactionStatus.SUCCESS, refund.getStatus(), "Refund status should be SUCCESS");
//        assertEquals(5.00, refund.getFeePercentage(), 0.01, "Fee percentage should match");
//    }
//
//    @Test
//    public void testRefundChangesReservationStatus() {
//        Refund refund = em.createQuery("SELECT r FROM Refund r", Refund.class).getSingleResult();
//        Reservation reservation = refund.getReservation();
//
//        assertNotNull(reservation);
//        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
//    }
//
//
//    @Test
//    public void testRefundAmountCalculation() {
//        Refund refund = em.createQuery("SELECT r FROM Refund r", Refund.class).getSingleResult();
//        double expectedRefundAmount = refund.getReservation().calculateTotalCost() * (1 - refund.getFeePercentage() / 100);
//
//        assertEquals(expectedRefundAmount, refund.getRefundAmount(), 0.01, "Refund amount should be calculated correctly");
//    }
//
//    @Test
//    public void testRefundAssociationWithReservation() {
//        Refund refund = em.createQuery("SELECT r FROM Refund r", Refund.class).getSingleResult();
//        Reservation reservation = refund.getReservation();
//
//        assertNotNull(reservation);
//        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
//        assertTrue(reservation.getTransactions().contains(refund));
//    }
//}
