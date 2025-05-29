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
public class EventJPATest2 {

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
    public void testCascadeRemoveEventWithTicketZones() {
        Optional<Event> eventOpt = eventRepository.find("title", "posidonio").firstResultOptional();
        assertTrue(eventOpt.isPresent(), "Event should exist");
        Event event = eventOpt.get();

        List<TicketZone> ticketZones = ticketZoneRepository.findByEventId(event.getId());
        assertFalse(ticketZones.isEmpty(), "Ticket zones should exist for the event");

        // Remove the event
        eventRepository.delete(event);
        eventRepository.getEntityManager().flush();

        // Verify that the related ticket zones are also removed
        List<TicketZone> remainingTicketZones = ticketZoneRepository.findByEventId(event.getId());
        assertTrue(remainingTicketZones.isEmpty(), "All ticket zones should be deleted when event is removed");

        // Verify the event is removed
        assertTrue(eventRepository.find("title", "posidonio").firstResultOptional().isEmpty(),
                "The event should be deleted");
    }
}
