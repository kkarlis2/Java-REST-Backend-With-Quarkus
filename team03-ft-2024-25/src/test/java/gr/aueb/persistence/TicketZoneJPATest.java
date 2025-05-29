package gr.aueb.persistence;

import gr.aueb.domain.Category;
import gr.aueb.domain.Reservation;
import gr.aueb.domain.TicketZone;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketZoneJPATest {

    @Inject
    TicketZoneRepository ticketZoneRepository;

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
    public void testTicketZoneInsertion() {
        List<TicketZone> ticketZones = ticketZoneRepository.listAll();
        assertEquals(6, ticketZones.size());

        TicketZone t1 = ticketZones.stream()
                .filter(t -> t.getCategory() == Category.SIMPLE && t.getCost() == 20.00)
                .findFirst()
                .orElse(null);
        assertNotNull(t1);
        assertEquals(100, t1.getAvailableSeats());
    }

//    @Test
//    @Transactional
//    @ActivateRequestContext
//    public void testAvailableSeatsUpdateAfterReservation() {
//        List<TicketZone> simpleZones = ticketZoneRepository.findByCategory(Category.SIMPLE);
//        assertFalse(simpleZones.isEmpty());
//
//        TicketZone ticketZone = simpleZones.get(0);
//        int initialAvailableSeats = ticketZone.getAvailableSeats();
//        assertTrue(initialAvailableSeats > 0, "Initial available seats should be greater than zero");
//
//        ticketZone.setAvailableSeats(initialAvailableSeats - 3);
//        ticketZoneRepository.persist(ticketZone);
//        ticketZoneRepository.getEntityManager().flush();
//
//        Optional<TicketZone> updatedZoneOpt = ticketZoneRepository.findByIdWithEvent(ticketZone.getId());
//        assertTrue(updatedZoneOpt.isPresent());
//        assertEquals(initialAvailableSeats - 3, updatedZoneOpt.get().getAvailableSeats());
//    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testTicketZoneWithEventAssociation() {
        List<TicketZone> ticketZones = ticketZoneRepository.listAll();
        assertFalse(ticketZones.isEmpty());

        TicketZone ticketZone = ticketZones.get(0);
        assertNotNull(ticketZone.getEvent());
        assertNotNull(ticketZone.getEvent().getTitle());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testCascadeRemoveTicketZoneWithReservations() {
        List<TicketZone> simpleZones = ticketZoneRepository.findByCategory(Category.SIMPLE);
        assertFalse(simpleZones.isEmpty());
        TicketZone ticketZone = simpleZones.get(0);

        // Verify reservations exist
        List<Reservation> reservations = reservationRepository.find("ticketZone", ticketZone).list();
        assertFalse(reservations.isEmpty(), "Reservations should exist for the TicketZone");

        // Remove TicketZone
        ticketZoneRepository.delete(ticketZone);
        ticketZoneRepository.getEntityManager().flush();

        // Verify that the TicketZone is removed
        assertNull(ticketZoneRepository.findById(Long.valueOf(ticketZone.getId())),
                "The TicketZone should be deleted");

        // Verify that related reservations are removed
        List<Reservation> remainingReservations = reservationRepository.find("ticketZone", ticketZone).list();
        assertTrue(remainingReservations.isEmpty(),
                "All reservations should be deleted when TicketZone is removed");
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.Category;
//import gr.aueb.domain.Reservation;
//import gr.aueb.domain.TicketZone;
//import jakarta.persistence.EntityTransaction;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TicketZoneJPATest extends JPATest {
//
//    @Test
//    public void testTicketZoneInsertion() {
//        List<TicketZone> ticketZones = em.createQuery("SELECT t FROM TicketZone t", TicketZone.class).getResultList();
//        assertEquals(6, ticketZones.size());
//
//        TicketZone t1 = ticketZones.stream().filter(t -> t.getCategory() == Category.SIMPLE && t.getCost() == 20.00).findFirst().orElse(null);
//        assertNotNull(t1);
//        assertEquals(100, t1.getAvailableSeats());
//    }
//
//    @Test
//    public void testAvailableSeatsUpdateAfterReservation() {
//        TicketZone ticketZone = em.createQuery("SELECT t FROM TicketZone t WHERE t.category = :category", TicketZone.class)
//                .setParameter("category", Category.SIMPLE)
//                .getResultList()
//                .get(0);
//
//        int initialAvailableSeats = ticketZone.getAvailableSeats();
//        assertTrue(initialAvailableSeats > 0, "Initial available seats should be greater than zero");
//
//        ticketZone.setAvailableSeats(initialAvailableSeats - 3);
//
//        em.getTransaction().begin();
//        em.merge(ticketZone);
//        em.getTransaction().commit();
//
//        TicketZone updatedZone = em.find(TicketZone.class, ticketZone.getId());
//        assertEquals(initialAvailableSeats - 3, updatedZone.getAvailableSeats());
//    }
//
//    @Test
//    public void testTicketZoneWithEventAssociation() {
//        TicketZone ticketZone = em.createQuery("SELECT t FROM TicketZone t", TicketZone.class).getResultList().get(0);
//
//        assertNotNull(ticketZone.getEvent());
//        assertNotNull(ticketZone.getEvent().getTitle());
//    }
//
//    @Test
//    public void testCascadeRemoveTicketZoneWithReservations() {
//        TicketZone ticketZone = em.createQuery("SELECT t FROM TicketZone t WHERE t.category = :category", TicketZone.class)
//                .setParameter("category", Category.SIMPLE)
//                .getResultList()
//                .get(0);
//
//        // Verify reservations exist
//        List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r WHERE r.ticketZone = :ticketZone", Reservation.class)
//                .setParameter("ticketZone", ticketZone)
//                .getResultList();
//        assertFalse(reservations.isEmpty(), "Reservations should exist for the TicketZone");
//
//        // Begin transaction and remove TicketZone
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        em.remove(ticketZone);
//        tx.commit();
//
//        // Verify that the TicketZone is removed
//        TicketZone removedZone = em.find(TicketZone.class, ticketZone.getId());
//        assertNull(removedZone, "The TicketZone should be deleted");
//
//        // Verify that related reservations are removed
//        long reservationCount = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.ticketZone = :ticketZone", Long.class)
//                .setParameter("ticketZone", ticketZone)
//                .getSingleResult();
//        assertEquals(0, reservationCount, "All reservations should be deleted when TicketZone is removed");
//    }
//}
//
