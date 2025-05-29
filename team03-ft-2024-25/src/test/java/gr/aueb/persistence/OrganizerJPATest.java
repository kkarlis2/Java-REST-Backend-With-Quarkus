package gr.aueb.persistence;

import gr.aueb.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrganizerJPATest {

    @Inject
    OrganizerRepository organizerRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    ZipCodeRepository zipCodeRepository;

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
    public void listOrganizers() {
        List<Organizer> organizerList = organizerRepository.listAll();
        assertEquals(2, organizerList.size());

        Organizer o1 = organizerList.get(0);
        Organizer o2 = organizerList.get(1);

        assertNotNull(o1);
        assertEquals("123456789", o1.getTaxId());
        assertEquals("TasosEvents", o1.getBrandName());
        assertEquals("210467829", o1.getPhoneNumber());
        assertEquals("Tasos", o1.getUserName());
        assertEquals("test", o1.getPassword());
        assertEquals("tasos@aueb.gr", o1.getEmail());
        assertEquals("lefkados", o1.getStreet());
        assertEquals("3", o1.getNumber());
        assertEquals("19100", o1.getZipCode());

        assertNotNull(o2);
        assertEquals("987654321", o2.getTaxId());
        assertEquals("VaggelisEvents", o2.getBrandName());
        assertEquals("210467830", o2.getPhoneNumber());
        assertEquals("Vag", o2.getUserName());
        assertEquals("pass123", o2.getPassword());
        assertEquals("vag@aueb.gr", o2.getEmail());
        assertEquals("panormou", o2.getStreet());
        assertEquals("12", o2.getNumber());
        assertEquals("11471", o2.getZipCode());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testOrganizerWithEvent() {
        Optional<Organizer> o1Opt = organizerRepository.findByTaxId("123456789");
        Optional<Organizer> o2Opt = organizerRepository.findByTaxId("987654321");

        assertTrue(o1Opt.isPresent(), "Organizer with tax ID 123456789 should exist");
        assertTrue(o2Opt.isPresent(), "Organizer with tax ID 987654321 should exist");

        Organizer o1 = o1Opt.get();
        Organizer o2 = o2Opt.get();

        List<Event> eventsForO1 = eventRepository.find("organizer", o1).list();
        assertEquals(2, eventsForO1.size(), "Organizer 1 should have 2 events");

        List<Event> eventsForO2 = eventRepository.find("organizer", o2).list();
        assertEquals(1, eventsForO2.size(), "Organizer 2 should have 1 event");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testforDuplicateOrganizers() {
        Optional<Organizer> existingOrganizerOpt = organizerRepository.findByTaxId("123456789");
        assertTrue(existingOrganizerOpt.isPresent());

        ZipCode zipCode = new ZipCode("99999");
        zipCodeRepository.persist(zipCode);

        Organizer duplicateOrganizer = new Organizer("123456789", "DuplicateEvent", "2101234567",
                "Duplicate", "duplicate123", "duplicate@aueb.gr", "Street", "10", zipCode);

        assertThrows(PersistenceException.class, () -> {
            organizerRepository.persist(duplicateOrganizer);
            organizerRepository.getEntityManager().flush();
        });
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testCascadePersistOrganizerWithEvents() {
        ZipCode zipCode = new ZipCode("99999");
        zipCodeRepository.persist(zipCode);

        Organizer newOrganizer = new Organizer("1122334455", "NewOrganizer", "2101234567",
                "newuser", "newpass", "new@aueb.gr", "New Street", "15", zipCode);

        Event newEvent1 = new Event("New Event 1", LocalDate.now().plusDays(20), LocalTime.of(17, 0),
                "New Venue", "Description 1", EventType.CONCERT, newOrganizer);
        Event newEvent2 = new Event("New Event 2", LocalDate.now().plusDays(25), LocalTime.of(19, 0),
                "New Venue 2", "Description 2", EventType.SEMINAR, newOrganizer);

        newOrganizer.addEvent(newEvent1);
        newOrganizer.addEvent(newEvent2);

        organizerRepository.persist(newOrganizer);
        organizerRepository.getEntityManager().flush();

        Optional<Organizer> persistedOrganizerOpt = organizerRepository.findByTaxId("1122334455");
        assertTrue(persistedOrganizerOpt.isPresent());
        assertEquals(2, persistedOrganizerOpt.get().getEvents().size());
    }



    @Test
    @Transactional
    @ActivateRequestContext
    public void testCascadeMergeOrganizerWithEvents() {
        Optional<Organizer> organizerOpt = organizerRepository.findByTaxId("123456789");
        assertTrue(organizerOpt.isPresent());

        Organizer organizer = organizerOpt.get();
        organizer.setBrandName("UpdatedBrand");

        // Get and update first event
        Event firstEvent = organizer.getEvents().iterator().next();
        firstEvent.setTitle("Updated Event Title");

        organizerRepository.persist(organizer);
        organizerRepository.getEntityManager().flush();

        Optional<Organizer> updatedOrganizerOpt = organizerRepository.findByTaxId("123456789");
        assertTrue(updatedOrganizerOpt.isPresent());
        assertEquals("UpdatedBrand", updatedOrganizerOpt.get().getBrandName());

        Event updatedEvent = updatedOrganizerOpt.get().getEvents().iterator().next();
        assertEquals("Updated Event Title", updatedEvent.getTitle());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testPersistOrganizerWithAddressAndZipCode() {
        ZipCode zipCode = new ZipCode("54321");
        zipCodeRepository.persist(zipCode);

        Address address = new Address("New Street", "15", zipCode);
        Organizer organizer = new Organizer("111222333", "EmbeddedOrganizer", "2109876543",
                "embeduser", "embedpass", "embed@aueb.gr", address.getStreet(),
                address.getNumber(), zipCode);

        organizerRepository.persist(organizer);
        organizerRepository.getEntityManager().flush();

        Optional<Organizer> persistedOrganizerOpt = organizerRepository.findByTaxId("111222333");
        assertTrue(persistedOrganizerOpt.isPresent());
        assertEquals("New Street", persistedOrganizerOpt.get().getStreet());
        assertEquals("54321", persistedOrganizerOpt.get().getZipCode());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testFindByUsername() {
        // Test existing username
        Optional<Organizer> organizerTasos = organizerRepository.findByUsername("Tasos");
        assertTrue(organizerTasos.isPresent(), "Should find organizer with username 'Tasos'");
        assertEquals("123456789", organizerTasos.get().getTaxId(), "Should find correct organizer");
        assertEquals("TasosEvents", organizerTasos.get().getBrandName());

        // Test another existing username
        Optional<Organizer> organizerVag = organizerRepository.findByUsername("Vag");
        assertTrue(organizerVag.isPresent(), "Should find organizer with username 'Vag'");
        assertEquals("987654321", organizerVag.get().getTaxId(), "Should find correct organizer");
        assertEquals("VaggelisEvents", organizerVag.get().getBrandName());

        // Test non-existent username
        Optional<Organizer> nonExistentOrganizer = organizerRepository.findByUsername("nonexistent");
        assertFalse(nonExistentOrganizer.isPresent(), "Should not find organizer with non-existent username");
    }
    @Test
    @Transactional
    @ActivateRequestContext
    public void testZipCodeOperations() {
        // Χρησιμοποιούμε έναν κωδικό που δεν υπάρχει στον Initializer
        String nonExistentCode = "77777";

        // Test findByCode και exists για μη υπάρχον ZipCode
        Optional<ZipCode> nonExistentZipCode = zipCodeRepository.findByCode(nonExistentCode);
        assertFalse(nonExistentZipCode.isPresent(), "ZipCode shouldn't exist before insertion");
        assertFalse(zipCodeRepository.exists(nonExistentCode), "ZipCode shouldn't exist before insertion");

        // Test findByCode και exists για υπάρχον ZipCode από initializer
        Optional<ZipCode> existingZipCode = zipCodeRepository.findByCode("19100");
        assertTrue(existingZipCode.isPresent(), "Should find existing ZipCode from initializer");
        assertTrue(zipCodeRepository.exists("19100"), "Should confirm existing ZipCode exists");

        // Test προσθήκης νέου ZipCode και επιβεβαίωση μέσω και των δύο μεθόδων
        ZipCode newZipCode = new ZipCode(nonExistentCode);
        zipCodeRepository.persist(newZipCode);
        zipCodeRepository.getEntityManager().flush();

        Optional<ZipCode> foundNewZipCode = zipCodeRepository.findByCode(nonExistentCode);
        assertTrue(foundNewZipCode.isPresent(), "Should find the newly inserted ZipCode");
        assertTrue(zipCodeRepository.exists(nonExistentCode), "Should confirm new ZipCode exists");
    }


//    @Test
//    @Transactional
//    @ActivateRequestContext
//    public void testCascadeRemoveOrganizerWithEventsAndRelatedEntities() {
//        Optional<Organizer> organizerOpt = organizerRepository.findByTaxId("123456789");
//        assertTrue(organizerOpt.isPresent(), "Organizer should exist");
//
//        Organizer organizer = organizerOpt.get();
//        organizerRepository.delete(organizer);
//        organizerRepository.getEntityManager().flush();
//
//        // Verify deletion
//        List<Event> remainingEvents = eventRepository.find("organizer", organizer).list();
//        assertTrue(remainingEvents.isEmpty(), "All events should be deleted when organizer is removed");
//
//    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.*;
//import jakarta.persistence.EntityTransaction;
//import jakarta.persistence.PersistenceException;
//import jakarta.persistence.RollbackException;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class OrganizerJPATest extends JPATest {
//
//    @Test
//    public void listOrganizers() {
//        List<Organizer> organizerList = em.createQuery("SELECT o FROM Organizer o", Organizer.class).getResultList();
//        assertEquals(2, organizerList.size());
//
//        Organizer o1 = organizerList.get(0);
//        Organizer o2 = organizerList.get(1);
//
//
//        assertNotNull(o1);
//        assertEquals("123456789", o1.getTaxId());
//        assertEquals("TasosEvents", o1.getBrandName());
//        assertEquals("210467829", o1.getPhoneNumber());
//        assertEquals("Tasos", o1.getUserName());
//        assertEquals("test", o1.getPassword());
//        assertEquals("tasos@aueb.gr", o1.getEmail());
//        assertEquals("lefkados", o1.getStreet());
//        assertEquals("3", o1.getNumber());
//        assertEquals("19100", o1.getZipCode());
//
//        assertNotNull(o2);
//        assertEquals("987654321", o2.getTaxId());
//        assertEquals("VaggelisEvents", o2.getBrandName());
//        assertEquals("210467830", o2.getPhoneNumber());
//        assertEquals("Vag", o2.getUserName());
//        assertEquals("pass123", o2.getPassword());
//        assertEquals("vag@aueb.gr", o2.getEmail());
//        assertEquals("panormou", o2.getStreet());
//        assertEquals("12", o2.getNumber());
//        assertEquals("11471", o2.getZipCode());
//    }
//
//    @Test
//    public void testOrganizerWithEvent() {
//        List<Organizer> organizers = em.createQuery("SELECT o FROM Organizer o", Organizer.class).getResultList();
//        assertEquals(2, organizers.size(), "There should be 2 organizers in the database");
//
//        Organizer o1 = organizers.stream().filter(o -> o.getTaxId().equals("123456789")).findFirst().orElse(null);
//        Organizer o2 = organizers.stream().filter(o -> o.getTaxId().equals("987654321")).findFirst().orElse(null);
//
//        assertNotNull(o1);
//        assertNotNull(o2);
//
//        List<Event> eventsForO1 = em.createQuery("SELECT e FROM Event e WHERE e.organizer = :organizer", Event.class)
//                .setParameter("organizer", o1)
//                .getResultList();
//        assertEquals(2, eventsForO1.size(), "Organizer 1 should have 2 events");
//
//        List<Event> eventsForO2 = em.createQuery("SELECT e FROM Event e WHERE e.organizer = :organizer", Event.class)
//                .setParameter("organizer", o2)
//                .getResultList();
//        assertEquals(1, eventsForO2.size(), "Organizer 2 should have 1 event");
//    }
//
//    @Test
//    public void testforDuplicateOrganizers() {
//        Organizer existingOrganizer = em.createQuery("SELECT o FROM Organizer o WHERE o.taxId = :taxId", Organizer.class)
//                .setParameter("taxId", "123456789")
//                .getSingleResult();
//
//        Organizer duplicateOrganizer = new Organizer("123456789", "DuplicateEvent", "2101234567",
//                "Duplicate", "duplicate123", "duplicate@aueb.gr", "Street", "10", new ZipCode("99999"));
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        assertThrows(PersistenceException.class, () -> {
//            em.persist(duplicateOrganizer);
//            tx.commit();
//        });
//
//        tx.rollback();
//    }
//
//    @Test
//    public void testCascadePersistOrganizerWithEvents() {
//        Organizer newOrganizer = new Organizer("1122334455", "NewOrganizer", "2101234567",
//                "newuser", "newpass", "new@aueb.gr", "New Street", "15", new ZipCode("99999"));
//
//        Event newEvent1 = new Event("New Event 1", LocalDate.now().plusDays(20), LocalTime.of(17, 0),
//                "New Venue", "Description 1", EventType.CONCERT, newOrganizer);
//        Event newEvent2 = new Event("New Event 2", LocalDate.now().plusDays(25), LocalTime.of(19, 0),
//                "New Venue 2", "Description 2", EventType.SEMINAR, newOrganizer);
//
//        newOrganizer.addEvent(newEvent1);
//        newOrganizer.addEvent(newEvent2);
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        em.persist(newOrganizer);
//        tx.commit();
//
//        Organizer persistedOrganizer = em.find(Organizer.class, newOrganizer.getId());
//        assertNotNull(persistedOrganizer);
//        assertEquals(2, persistedOrganizer.getEvents().size());
//    }
//
//    @Test
//    public void testCascadeRemoveOrganizerWithEventsAndRelatedEntities() {
//        // Επιλέγουμε έναν Organizer
//        Organizer organizer = em.createQuery(
//                        "SELECT o FROM Organizer o WHERE o.taxId = :taxId", Organizer.class)
//                .setParameter("taxId", "123456789")
//                .getSingleResult();
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        em.remove(organizer); // Διαγραφή Organizer
//
//        tx.commit();
//
//        // Ελέγχουμε αν έχουν διαγραφεί όλα τα συνδεδεμένα δεδομένα
//        long eventCount = em.createQuery("SELECT COUNT(e) FROM Event e WHERE e.organizer = :organizer", Long.class)
//                .setParameter("organizer", organizer)
//                .getSingleResult();
//        assertEquals(0, eventCount, "All events should be deleted when organizer is removed");
//
////        long ticketZoneCount = em.createQuery("SELECT COUNT(tz) FROM TicketZone tz", Long.class)
////                .getSingleResult();
////        assertEquals(0, ticketZoneCount, "All ticket zones should be deleted when organizer is removed");
////
////        long reservationCount = em.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class)
////                .getSingleResult();
////        assertEquals(0, reservationCount, "All reservations should be deleted when organizer is removed");
//    }
//
//
//
//
//    @Test
//    public void testCascadeMergeOrganizerWithEvents() {
//        Organizer organizer = em.createQuery("SELECT o FROM Organizer o WHERE o.taxId = :taxId", Organizer.class)
//                .setParameter("taxId", "123456789")
//                .getSingleResult();
//
//        organizer.setBrandName("UpdatedBrand");
//        organizer.getEvents().iterator().next().setTitle("Updated Event Title");
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        em.merge(organizer);
//        tx.commit();
//
//        Organizer updatedOrganizer = em.find(Organizer.class, organizer.getId());
//        assertEquals("UpdatedBrand", updatedOrganizer.getBrandName());
//        assertEquals("Updated Event Title", updatedOrganizer.getEvents().iterator().next().getTitle());
//    }
//
//    @Test
//    public void testCascadeDetachOrganizerWithEvents() {
//        Organizer organizer = em.createQuery(
//                        "SELECT o FROM Organizer o LEFT JOIN FETCH o.events WHERE o.taxId = :taxId", Organizer.class)
//                .setParameter("taxId", "123456789")
//                .getSingleResult();
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        em.detach(organizer);
//
//        // Ελέγξτε αν τα events υπάρχουν μετά τη διαγραφή
//        assertEquals(2, organizer.getEvents().size());
//    }
//
//
//    @Test
//    public void testPersistOrganizerWithAddressAndZipCode() {
//        ZipCode zipCode = new ZipCode("54321");
//        Address address = new Address("New Street", "15", zipCode);
//
//        Organizer organizer = new Organizer("111222333", "EmbeddedOrganizer", "2109876543",
//                "embeduser", "embedpass", "embed@aueb.gr", address.getStreet(), address.getNumber(), zipCode);
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        em.persist(organizer);
//        tx.commit();
//
//        Organizer persistedOrganizer = em.find(Organizer.class, organizer.getId());
//        assertNotNull(persistedOrganizer);
//        assertEquals("New Street", persistedOrganizer.getStreet());
//        assertEquals("54321", persistedOrganizer.getZipCode());
//    }
//
//
//
//
//}
