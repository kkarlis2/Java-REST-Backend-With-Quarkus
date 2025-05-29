package gr.aueb.persistence;

import gr.aueb.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationJPATest {

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
    public void listReservations() {
        List<Reservation> reservationsList = reservationRepository.listAll();
        assertEquals(4, reservationsList.size());

        Reservation r = reservationsList.get(1);
        assertNotNull(r.getVisitor());
        assertNotNull(r.getTicketZone());
        assertEquals(1, r.getReservedSeats());
        assertNotNull(r.getReservationDate());
        assertEquals(ReservationStatus.CONFIRMED, r.getStatus());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testReservationWithVisitor() {
        List<Reservation> reservations = reservationRepository.listAll();

        Reservation r1 = reservations.stream()
                .filter(r -> r.getVisitor().getFirstName().equals("kostas"))
                .findFirst()
                .orElse(null);
        assertNotNull(r1);
        assertNotNull(r1.getVisitor());
        assertEquals("kostas", r1.getVisitor().getFirstName());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testReservationStatusUpdate() {
        List<Reservation> pendingReservations = reservationRepository.findByStatus(ReservationStatus.PENDING);
        assertFalse(pendingReservations.isEmpty());

        Reservation reservation = pendingReservations.get(0);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        reservationRepository.persist(reservation);
        reservationRepository.getEntityManager().flush();

        Reservation updatedReservation = reservationRepository.findById(Long.valueOf(reservation.getId()));
        assertEquals(ReservationStatus.CONFIRMED, updatedReservation.getStatus(),
                "Reservation status should be updated to CONFIRMED");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testReservationWithTicketZone() {
        List<Reservation> reservations = reservationRepository.listAll();
        Reservation r1 = reservations.get(0);

        assertNotNull(r1.getTicketZone(), "Ticket zone should not be null");
        assertEquals(Category.SIMPLE, r1.getTicketZone().getCategory(),
                "Ticket zone category should be SIMPLE");
        assertEquals(20.00, r1.getTicketZone().getCost(), 0.01,
                "Ticket zone cost should match");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testReservationWithDiscountCategory() {
        List<Reservation> reservations = reservationRepository.find("discount", DiscountCat.STUDENT).list();
        assertFalse(reservations.isEmpty());

        Reservation reservation = reservations.get(0);
        assertEquals(DiscountCat.STUDENT, reservation.getDiscount());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testFindActiveReservationsByVisitor() {
        // Βρίσκουμε πρώτα έναν visitor με το firstName "kostas"
        Optional<Visitor> visitor = visitorRepository.find("firstName", "kostas").firstResultOptional();
        assertTrue(visitor.isPresent());

        // Παίρνουμε τις ενεργές κρατήσεις του
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByVisitor(visitor.get().getId());

        // Ελέγχουμε ότι βρήκαμε κρατήσεις
        assertFalse(activeReservations.isEmpty());

        // Ελέγχουμε ότι καμία από τις κρατήσεις δεν είναι ακυρωμένη
        for (Reservation reservation : activeReservations) {
            assertEquals(visitor.get().getId(), reservation.getVisitor().getId());
            assertNotEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        }

        // Ακυρώνουμε μια κράτηση
        Reservation reservationToCancel = activeReservations.get(0);
        reservationToCancel.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.persist(reservationToCancel);

        // Ελέγχουμε ότι η ακυρωμένη κράτηση δεν εμφανίζεται στις ενεργές
        List<Reservation> updatedActiveReservations =
                reservationRepository.findActiveReservationsByVisitor(visitor.get().getId());
        assertFalse(updatedActiveReservations.stream()
                .anyMatch(r -> r.getId().equals(reservationToCancel.getId())));
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.*;
//import jakarta.persistence.EntityTransaction;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ReservationJPATest extends JPATest{
//
//    @Test
//    public void listReservations() {
//        List<Reservation> reservationsList = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
//        assertEquals(4, reservationsList.size());
//
//        Reservation r = reservationsList.get(1);
//        assertNotNull(r.getVisitor());
//        assertNotNull(r.getTicketZone());
//        assertEquals(1, r.getReservedSeats());
//        assertNotNull(r.getReservationDate());
//        assertEquals(ReservationStatus.CONFIRMED, r.getStatus());
//    }
//
//    @Test
//    public void testReservationWithVisitor() {
//        List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
//
//        Reservation r1 = reservations.stream().filter(r -> r.getVisitor().getFirstName().equals("kostas")).findFirst().orElse(null);
//        assertNotNull(r1);
//        assertNotNull(r1.getVisitor());
//        assertEquals("kostas", r1.getVisitor().getFirstName());
//    }
//
//    @Test
//    public void testReservationStatusUpdate() {
//        Reservation reservation = em.createQuery("SELECT r FROM Reservation r WHERE r.status = :status", Reservation.class)
//                .setParameter("status", ReservationStatus.PENDING)
//                .getResultList()
//                .get(0);
//
//        assertNotNull(reservation);
//        reservation.setStatus(ReservationStatus.CONFIRMED);
//
//        em.getTransaction().begin();
//        em.merge(reservation);
//        em.getTransaction().commit();
//
//        Reservation updatedReservation = em.find(Reservation.class, reservation.getId());
//        assertEquals(ReservationStatus.CONFIRMED, updatedReservation.getStatus(), "Reservation status should be updated to CONFIRMED");
//    }
//
//    @Test
//    public void testReservationWithTicketZone() {
//        List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
//        Reservation r1 = reservations.get(0);
//
//        assertNotNull(r1.getTicketZone(), "Ticket zone should not be null");
//        assertEquals(Category.SIMPLE, r1.getTicketZone().getCategory(), "Ticket zone category should be SIMPLE");
//        assertEquals(20.00, r1.getTicketZone().getCost(), 0.01, "Ticket zone cost should match");
//    }
//
//    @Test
//    public void testReservationWithDiscountCategory() {
//        Reservation reservation = em.createQuery("SELECT r FROM Reservation r WHERE r.discount = :discount", Reservation.class)
//                .setParameter("discount", DiscountCat.STUDENT)
//                .getSingleResult();
//
//        assertNotNull(reservation);
//        assertEquals(DiscountCat.STUDENT, reservation.getDiscount());
//    }
//
//    @Test
//    public void testCascadeRemoveReservationWithTransactions() {
//        Reservation reservation = em.createQuery("SELECT r FROM Reservation r WHERE r.visitor.firstName = :visitorName", Reservation.class)
//                .setParameter("visitorName", "kostas")
//                .getResultList()
//                .get(0);
//
//        // Verify transactions exist
//        List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t WHERE t.reservation = :reservation", Transaction.class)
//                .setParameter("reservation", reservation)
//                .getResultList();
//        assertFalse(transactions.isEmpty(), "Transactions should exist for the Reservation");
//
//        // Begin transaction and remove Reservation
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        em.remove(reservation);
//        tx.commit();
//
//        // Verify that the Reservation is removed
//        Reservation removedReservation = em.find(Reservation.class, reservation.getId());
//        assertNull(removedReservation, "The Reservation should be deleted");
//
//        // Verify that related transactions are removed
//        long transactionCount = em.createQuery("SELECT COUNT(t) FROM Transaction t WHERE t.reservation = :reservation", Long.class)
//                .setParameter("reservation", reservation)
//                .getSingleResult();
//        assertEquals(0, transactionCount, "All transactions should be deleted when Reservation is removed");
//    }
//}
//
