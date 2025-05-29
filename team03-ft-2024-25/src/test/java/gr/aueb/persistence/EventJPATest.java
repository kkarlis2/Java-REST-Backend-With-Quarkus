package gr.aueb.persistence;

import gr.aueb.domain.Event;
import gr.aueb.domain.EventType;
import gr.aueb.domain.Organizer;
import gr.aueb.domain.TicketZone;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventJPATest {

    @Inject
    EventRepository eventRepository;

    @Inject
    OrganizerRepository organizerRepository;

    @Inject
    TicketZoneRepository ticketZoneRepository;

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
    public void listEvent() {
        List<Event> eventList = eventRepository.listAll();
        assertEquals(3, eventList.size(), "There should be 3 events in the database");

        Event e1 = eventList.get(0);
        assertEquals("posidonio", e1.getTitle());
        assertEquals(LocalDate.now().plusDays(10), e1.getDate());
        assertEquals(LocalTime.of(18, 30), e1.getTime());
        assertEquals("MetropolitanEXPO", e1.getLocation());
        assertEquals("TELEIO EVENT", e1.getDescription());
        assertEquals(EventType.CONCERT, e1.getEventType());
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testEventWithTicketZones() {
        List<Event> events = eventRepository.listAll();

        Event event = events.get(0);
        List<TicketZone> ticketZones = ticketZoneRepository.findByEventId(event.getId());

        if (event.getTitle().equals("posidonio")) {
            assertEquals(2, ticketZones.size(), "Event 'posidonio' should have 2 ticket zones");
        }
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testforDuplicateEvents() {
        Optional<Organizer> organizer = organizerRepository.findByTaxId("123456789");
        assertTrue(organizer.isPresent());

        Event duplicateEvent = new Event("Duplicate Event", LocalDate.now().plusDays(10),
                LocalTime.of(18, 30), "MetropolitanEXPO", "This should fail",
                EventType.CONCERT, organizer.get());

        assertThrows(PersistenceException.class, () -> {
            eventRepository.persist(duplicateEvent);
            eventRepository.getEntityManager().flush();
        });
    }



    @Test
    @Transactional
    @ActivateRequestContext
    public void testFindByDate() {
        // Arrange
        LocalDate tenDaysFromNow = LocalDate.now().plusDays(10);
        LocalDate fifteenDaysFromNow = LocalDate.now().plusDays(15);

        // Act
        List<Event> eventsInTenDays = eventRepository.findByDate(tenDaysFromNow);
        List<Event> eventsInFifteenDays = eventRepository.findByDate(fifteenDaysFromNow);
        List<Event> eventsToday = eventRepository.findByDate(LocalDate.now());

        // Assert
        assertEquals(1, eventsInTenDays.size(), "Should find one event in 10 days");
        assertEquals("posidonio", eventsInTenDays.get(0).getTitle());

        assertEquals(1, eventsInFifteenDays.size(), "Should find one event in 15 days");
        assertEquals("shakespeare", eventsInFifteenDays.get(0).getTitle());

        assertTrue(eventsToday.isEmpty(), "Should not find any events today");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testFindByTitleContaining() {
        // Act
        List<Event> posEvents = eventRepository.findByTitleContaining("pos");
        List<Event> techEvents = eventRepository.findByTitleContaining("tech");
        List<Event> nonexistentEvents = eventRepository.findByTitleContaining("nonexistent");

        // Assert
        assertEquals(1, posEvents.size(), "Should find one event containing 'pos'");
        assertEquals("posidonio", posEvents.get(0).getTitle());

        assertEquals(1, techEvents.size(), "Should find one event containing 'tech'");
        assertEquals("tech_conference", techEvents.get(0).getTitle());

        assertTrue(nonexistentEvents.isEmpty(), "Should not find any events with nonexistent title");
    }

    @Test
    @Transactional
    @ActivateRequestContext
    public void testFindUpcomingEvents() {
        // Act
        List<Event> upcomingEvents = eventRepository.findUpcomingEvents();

        // Assert
        assertEquals(3, upcomingEvents.size(), "Should find all three events as they are in the future");

        // Verify that all events are in the future
        LocalDate today = LocalDate.now();
        for (Event event : upcomingEvents) {
            assertTrue(event.getDate().isAfter(today) || event.getDate().isEqual(today),
                    "Event date should be today or in the future");
        }

        // Verify specific events are present
        assertTrue(upcomingEvents.stream().anyMatch(e -> e.getTitle().equals("posidonio")),
                "Should find posidonio event");
        assertTrue(upcomingEvents.stream().anyMatch(e -> e.getTitle().equals("shakespeare")),
                "Should find shakespeare event");
        assertTrue(upcomingEvents.stream().anyMatch(e -> e.getTitle().equals("tech_conference")),
                "Should find tech_conference event");
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.*;
//import jakarta.persistence.PersistenceException;
//import org.junit.jupiter.api.Assertions;
//import jakarta.persistence.RollbackException;
//import jakarta.persistence.EntityTransaction;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import static io.smallrye.common.constraint.Assert.assertNotNull;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class EventJPATest extends JPATest{
//
//    @Test
//    public void listEvent() {
//        List<Event> eventList = em.createQuery("SELECT e FROM Event e", Event.class).getResultList();
//        assertEquals(3, eventList.size(), "There should be 3 events in the database");
//
//        Event e1 = eventList.get(0);
//        assertEquals("posidonio", e1.getTitle());
//        assertEquals(LocalDate.now().plusDays(10), e1.getDate());
//        assertEquals(LocalTime.of(18, 30), e1.getTime());
//        assertEquals("MetropolitanEXPO", e1.getLocation());
//        assertEquals("TELEIO EVENT", e1.getDescription());
//        assertEquals(EventType.CONCERT, e1.getEventType());
//    }
//
//    @Test
//    public void testEventWithTicketZones() {
//        List<Event> events = em.createQuery("SELECT e FROM Event e", Event.class).getResultList();
//        assertEquals(3, events.size(), "There should be 1 event in the database");
//
//        Event event = events.get(0);
//
//        List<TicketZone> ticketZones = em.createQuery("SELECT t FROM TicketZone t WHERE t.event = :event", TicketZone.class)
//                .setParameter("event", event)
//                .getResultList();
//        if (event.getTitle().equals("posidonio")) {
//            assertEquals(2, ticketZones.size(), "Event 'posidonio' should have 2 ticket zones");
//        }
//    }
//
//    @Test
//    public void testforDuplicateEvents() {
//        Organizer organizer = em.createQuery("SELECT o FROM Organizer o WHERE o.taxId = :taxId", Organizer.class)
//                .setParameter("taxId", "123456789")
//                .getSingleResult();
//
//        Event duplicateEvent = new Event("Duplicate Event", LocalDate.now().plusDays(10), LocalTime.of(18, 30),
//                "MetropolitanEXPO", "This should fail", EventType.CONCERT, organizer);
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        assertThrows(PersistenceException.class, () -> {
//            em.persist(duplicateEvent);
//            tx.commit();
//        });
//
//        tx.rollback();
//    }
//    @Test
//    public void testCascadeRemoveEventWithTicketZones() {
//        // Fetch the event and related ticket zones
//        Event event = em.createQuery("SELECT e FROM Event e WHERE e.title = :title", Event.class)
//                .setParameter("title", "posidonio")
//                .getSingleResult();
//        assertNotNull(event);
//
//        List<TicketZone> ticketZones = em.createQuery("SELECT t FROM TicketZone t WHERE t.event = :event", TicketZone.class)
//                .setParameter("event", event)
//                .getResultList();
//        assertFalse(ticketZones.isEmpty(), "Ticket zones should exist for the event");
//
//        // Begin transaction
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        // Remove the event
//        em.remove(event);
//        tx.commit();
//
//        // Verify that the related ticket zones are also removed
//        long ticketZoneCount = em.createQuery("SELECT COUNT(t) FROM TicketZone t WHERE t.event = :event", Long.class)
//                .setParameter("event", event)
//                .getSingleResult();
//        assertEquals(0, ticketZoneCount, "All ticket zones should be deleted when event is removed");
//
//        // Verify the event is removed
//        long eventCount = em.createQuery("SELECT COUNT(e) FROM Event e WHERE e.title = :title", Long.class)
//                .setParameter("title", "posidonio")
//                .getSingleResult();
//        assertEquals(0, eventCount, "The event should be deleted");
//    }
//}
