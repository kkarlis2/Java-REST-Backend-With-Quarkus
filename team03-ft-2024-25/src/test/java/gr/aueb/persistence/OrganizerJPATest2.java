package gr.aueb.persistence;

import gr.aueb.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrganizerJPATest2 {

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
    public void testCascadeRemoveOrganizerWithEventsAndRelatedEntities() {
        Optional<Organizer> organizerOpt = organizerRepository.findByTaxId("123456789");
        assertTrue(organizerOpt.isPresent(), "Organizer should exist");

        Organizer organizer = organizerOpt.get();
        organizerRepository.delete(organizer);
        organizerRepository.getEntityManager().flush();

        // Verify deletion
        List<Event> remainingEvents = eventRepository.find("organizer", organizer).list();
        assertTrue(remainingEvents.isEmpty(), "All events should be deleted when organizer is removed");

    }
}
