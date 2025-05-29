package gr.aueb.persistence;

import gr.aueb.domain.Reservation;
import gr.aueb.domain.Transaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationJPATest2 {
    @Inject
    ReservationRepository reservationRepository;

    @Inject
    VisitorRepository visitorRepository;

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
    public void testCascadeRemoveReservationWithTransactions() {
        List<Reservation> reservations = reservationRepository.find(
                "visitor.firstName", "kostas").list();
        assertFalse(reservations.isEmpty());
        Reservation reservation = reservations.get(0);

        // Verify transactions exist
        List<Transaction> transactions = reservationRepository.getEntityManager()
                .createQuery("SELECT t FROM Transaction t WHERE t.reservation = :reservation", Transaction.class)
                .setParameter("reservation", reservation)
                .getResultList();
        assertFalse(transactions.isEmpty(), "Transactions should exist for the Reservation");

        // Remove Reservation
        reservationRepository.delete(reservation);
        reservationRepository.getEntityManager().flush();

        // Verify that the Reservation is removed
        assertNull(reservationRepository.findById(Long.valueOf(reservation.getId())),
                "The Reservation should be deleted");

        // Verify that related transactions are removed
        long transactionCount = reservationRepository.getEntityManager()
                .createQuery("SELECT COUNT(t) FROM Transaction t WHERE t.reservation = :reservation", Long.class)
                .setParameter("reservation", reservation)
                .getSingleResult();
        assertEquals(0, transactionCount,
                "All transactions should be deleted when Reservation is removed");
    }
}
