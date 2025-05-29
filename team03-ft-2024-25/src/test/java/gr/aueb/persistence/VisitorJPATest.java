package gr.aueb.persistence;

import gr.aueb.domain.Reservation;
import gr.aueb.domain.Visitor;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class VisitorJPATest {

    @Inject
    VisitorRepository visitorRepository;

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
    @ActivateRequestContext
    public void testVisitorInsertion() {
        List<Visitor> visitors = visitorRepository.listAll();
        assertEquals(3, visitors.size());

        Optional<Visitor> v1Opt = visitorRepository.findByUsername("kkarlis");
        assertTrue(v1Opt.isPresent(), "Visitor 'kkarlis' should exist");
        Visitor v1 = v1Opt.get();
        assertEquals("kostas", v1.getFirstName());
        assertEquals("karlis", v1.getLastName());
        assertEquals("6985486420", v1.getPhoneNumber());
        assertEquals("karlis@aueb.gr", v1.getEmail());
        assertEquals("kkarlis", v1.getUsername());

        Optional<Visitor> v2Opt = visitorRepository.findByUsername("vagzyg");
        assertTrue(v2Opt.isPresent(), "Visitor 'vagzyg' should exist");
        assertEquals("vaggelis", v2Opt.get().getFirstName());
        assertEquals("zygokostas", v2Opt.get().getLastName());

        Optional<Visitor> v3Opt = visitorRepository.findByUsername("tasoskour");
        assertTrue(v3Opt.isPresent(), "Visitor 'tasoskour' should exist");
        assertEquals("Tasos", v3Opt.get().getFirstName());
        assertEquals("Koursos", v3Opt.get().getLastName());
    }

    @Test
    @ActivateRequestContext
    public void testVisitorWithReservations() {
        Optional<Visitor> v1Opt = visitorRepository.findByUsername("kkarlis");
        assertTrue(v1Opt.isPresent());
        Visitor v1 = v1Opt.get();

        List<Reservation> reservations = reservationRepository.findByVisitorId(v1.getId());
        assertEquals(2, reservations.size());

        reservations.forEach(reservation -> {
            assertNotNull(reservation.getTicketZone());
            assertTrue(reservation.getReservedSeats() > 0);
        });
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testforDuplicateAccounts() {
        Optional<Visitor> existingVisitorOpt = visitorRepository.findByUsername("kkarlis");
        assertTrue(existingVisitorOpt.isPresent(), "Visitor kkarlis should exist in database");

        Visitor duplicateVisitor = new Visitor("TASOSSS", "KOURSOSS", "1234567890",
                "tasoskour@aueb.com", "kkarlis", "newpassword");

        assertThrows(jakarta.persistence.PersistenceException.class, () -> {
            visitorRepository.persist(duplicateVisitor);
            // Need to flush to trigger the constraint
            visitorRepository.getEntityManager().flush();
        });
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testForDuplicateVisitorEmail() {
        // First, make sure our test visitor exists
        Optional<Visitor> existingVisitorOpt = visitorRepository.findByEmail("karlis@aueb.gr");
        assertTrue(existingVisitorOpt.isPresent());

        // Create a new visitor with the same email
        Visitor duplicateEmailVisitor = new Visitor("Vaggeliss", "Zygokwstas", "6987654321",
                "karlis@aueb.gr", "vag", "password123");

        // The persist should fail and we need to flush to trigger the constraint
        assertThrows(PersistenceException.class, () -> {
            visitorRepository.persist(duplicateEmailVisitor);
            visitorRepository.getEntityManager().flush(); // This is important!
        });
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testForDuplicateVisitorPhoneNumber() {
        // Πρώτα επιβεβαιώνουμε ότι έχουμε τα σωστά test data
        Optional<Visitor> existingVisitor = visitorRepository.findByPhoneNumber("6985486421");
        assertTrue(existingVisitor.isPresent(), "Original visitor should exist");

        // Δημιουργούμε νέο visitor με το ίδιο τηλέφωνο
        Visitor duplicatePhoneVisitor = new Visitor("Vaggeliss", "Zygokwstas", "6985486421",
                "vag@aueb.gr", "vag", "password123");

        // Το persist πρέπει να αποτύχει
        assertThrows(PersistenceException.class, () -> {
            visitorRepository.persist(duplicatePhoneVisitor);
            visitorRepository.getEntityManager().flush();
        });
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testCascadeRemoveVisitorWithReservations() {
        Optional<Visitor> visitorOpt = visitorRepository.findByUsername("kkarlis");
        assertTrue(visitorOpt.isPresent());
        Visitor visitor = visitorOpt.get();

        List<Reservation> reservations = reservationRepository.findByVisitorId(visitor.getId());
        assertFalse(reservations.isEmpty(), "Visitor should have reservations");

        visitorRepository.delete(visitor);

        // Verify reservations were deleted
        List<Reservation> remainingReservations = reservationRepository.findByVisitorId(visitor.getId());
        assertTrue(remainingReservations.isEmpty(),
                "All reservations should be deleted when visitor is removed");
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.Reservation;
//import gr.aueb.domain.Visitor;
//import jakarta.persistence.EntityTransaction;
//import jakarta.persistence.PersistenceException;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class VisitorJPATest extends JPATest {
//
//    @Test
//    public void testVisitorInsertion() {
//        List<Visitor> visitors = em.createQuery("SELECT v FROM Visitor v", Visitor.class).getResultList();
//        assertEquals(3, visitors.size());
//
//        Visitor v1 = visitors.stream().filter(v -> v.getUsername().equals("kkarlis")).findFirst().orElse(null);
//        assertNotNull(v1, "Visitor 'kkarlis' should exist");
//        assertEquals("kostas", v1.getFirstName());
//        assertEquals("karlis", v1.getLastName());
//        assertEquals("6985486420", v1.getPhoneNumber());
//        assertEquals("karlis@aueb.gr", v1.getEmail());
//        assertEquals("kkarlis", v1.getUsername());
//
//        Visitor v2 = visitors.stream().filter(v -> v.getUsername().equals("vagzyg")).findFirst().orElse(null);
//        assertNotNull(v2, "Visitor 'vagzyg' should exist");
//        assertEquals("vaggelis", v2.getFirstName());
//        assertEquals("zygokostas", v2.getLastName());
//
//        Visitor v3 = visitors.stream().filter(v -> v.getUsername().equals("tasoskour")).findFirst().orElse(null);
//        assertNotNull(v3, "Visitor 'tasoskour' should exist");
//        assertEquals("Tasos", v3.getFirstName());
//        assertEquals("Koursos", v3.getLastName());
//    }
//
//    @Test
//    public void testVisitorWithReservations() {
//        List<Visitor> visitors = em.createQuery("SELECT v FROM Visitor v", Visitor.class).getResultList();
//        Visitor v1 = visitors.stream().filter(v -> v.getUsername().equals("kkarlis")).findFirst().orElse(null);
//        assertNotNull(v1);
//
//        List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r WHERE r.visitor = :visitor", Reservation.class)
//                .setParameter("visitor", v1)
//                .getResultList();
//
//        assertEquals(2, reservations.size());
//
//        Reservation r1 = reservations.get(0);
//        assertNotNull(r1.getTicketZone());
//        assertTrue(r1.getReservedSeats() > 0);
//
//        Reservation r2 = reservations.get(1);
//        assertNotNull(r2.getTicketZone());
//    }
//
//    @Test
//    public void testforDuplicateAccounts() {
//        Visitor existingVisitor = em.createQuery("SELECT v FROM Visitor v WHERE v.account.username = :username", Visitor.class)
//                .setParameter("username", "kkarlis")
//                .getSingleResult();
//        assertNotNull(existingVisitor);
//
//        Visitor duplicateVisitor = new Visitor("TASOSSS", "KOURSOSS", "1234567890", "tasoskour@aueb.com", "kkarlis", "newpassword");
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        assertThrows(PersistenceException.class, () -> {
//            em.persist(duplicateVisitor);
//            tx.commit();
//        });
//        tx.rollback();
//    }
//
//    @Test
//    public void testForDuplicateVisitorEmail() {
//        Visitor existingVisitor = em.createQuery("SELECT v FROM Visitor v WHERE v.email.email = :email", Visitor.class)
//                .setParameter("email", "karlis@aueb.gr")
//                .getSingleResult();
//        Visitor duplicateEmailVisitor = new Visitor("Vaggeliss", "Zygokwstas", "6987654321", "karlis@aueb.gr", "vag", "password123");
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        assertThrows(PersistenceException.class, () -> {
//            em.persist(duplicateEmailVisitor);
//            tx.commit();
//        });
//
//        tx.rollback();
//    }
//
//    @Test
//    public void testForDuplicateVisitorPhoneNumber() {
//        Visitor existingVisitor = em.createQuery("SELECT v FROM Visitor v WHERE v.phoneNumber = :phoneNumber", Visitor.class)
//                .setParameter("phoneNumber", "6985486420")
//                .getSingleResult();
//
//        Visitor duplicatePhoneVisitor = new Visitor("Vaggeliss", "Zygokwstas", "6985486420", "vag@aueb.gr", "vag", "password123");
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        assertThrows(PersistenceException.class, () -> {
//            em.persist(duplicatePhoneVisitor);
//            tx.commit();
//        });
//
//        tx.rollback();
//    }
//
//    @Test
//    public void testCascadeRemoveVisitorWithReservations() {
//        // Αναζήτηση του Visitor μέσω του account.username
//        Visitor visitor = em.createQuery(
//                        "SELECT v FROM Visitor v WHERE v.account.username = :username", Visitor.class)
//                .setParameter("username", "kkarlis")
//                .getSingleResult();
//
//        // Επαλήθευση ότι υπάρχουν συνδεδεμένες κρατήσεις
//        List<Reservation> reservations = em.createQuery(
//                        "SELECT r FROM Reservation r WHERE r.visitor = :visitor", Reservation.class)
//                .setParameter("visitor", visitor)
//                .getResultList();
//
//        assertFalse(reservations.isEmpty(), "Visitor should have reservations");
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        // Διαγραφή του Visitor
//        em.remove(visitor);
//        tx.commit();
//
//        // Επαλήθευση ότι οι συνδεδεμένες κρατήσεις διαγράφηκαν
//        long reservationCount = em.createQuery(
//                        "SELECT COUNT(r) FROM Reservation r WHERE r.visitor.account.username = :username", Long.class)
//                .setParameter("username", "kkarlis")
//                .getSingleResult();
//
//        assertEquals(0, reservationCount, "All reservations should be deleted when visitor is removed");
//    }
//
//
//
//}
//
//
//
