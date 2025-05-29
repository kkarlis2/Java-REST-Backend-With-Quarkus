package gr.aueb.persistence;

import gr.aueb.domain.Category;
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
public class TicketZoneJPATest2 {

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
    public void testAvailableSeatsUpdateAfterReservation() {
        List<TicketZone> simpleZones = ticketZoneRepository.findByCategory(Category.SIMPLE);
        assertFalse(simpleZones.isEmpty());

        TicketZone ticketZone = simpleZones.get(0);
        int initialAvailableSeats = ticketZone.getAvailableSeats();
        assertTrue(initialAvailableSeats > 0, "Initial available seats should be greater than zero");

        ticketZone.setAvailableSeats(initialAvailableSeats - 3);
        ticketZoneRepository.persist(ticketZone);
        ticketZoneRepository.getEntityManager().flush();

        Optional<TicketZone> updatedZoneOpt = ticketZoneRepository.findByIdWithEvent(ticketZone.getId());
        assertTrue(updatedZoneOpt.isPresent());
        assertEquals(initialAvailableSeats - 3, updatedZoneOpt.get().getAvailableSeats());
    }
}
