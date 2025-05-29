package gr.aueb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventStatisticsTest {
    private Event event;
    private Organizer organizer;
    private EventStatistics eventStatistics;

    @BeforeEach
    void setUp() {
        // Setup basic event
        organizer = new Organizer();  // Initialize with required fields
        event = new Event(
                "Test Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );

        // Add ticket zones with different categories
        TicketZone vipZone = new TicketZone(100.0, Category.VIP,50, 50, event);
        TicketZone simpleZone = new TicketZone(50.0, Category.SIMPLE,100, 100, event);
        TicketZone arenaZone = new TicketZone(75.0, Category.ARENA,200, 200, event);

        event.addTicketZone(vipZone);
        event.addTicketZone(simpleZone);
        event.addTicketZone(arenaZone);

        eventStatistics = new EventStatistics(event);
    }

    @Test
    void constructor_InitializesCorrectly() {
        assertNotNull(eventStatistics.getAvailableSeatsPerCategory());
        assertNotNull(eventStatistics.getReservedSeatsPerCategory());
        assertEquals(event, eventStatistics.getEvent());
    }

    @Test
    void calculateStatistics_ComputesCorrectTotals() {
        assertEquals(350, eventStatistics.getTotalAvailableSeats()); // 50 + 100 + 200
        assertEquals(0, eventStatistics.getTotalReservedSeats()); // Initially all seats are available
    }

    @Test
    void getOccupancyRate_AllSeatsAvailable_ReturnsZero() {
        assertEquals(0.0, eventStatistics.getOccupancyRate());
    }

    @Test
    void getOccupancyRate_AllSeatsReserved() {
        // Simulate all seats being reserved by setting available seats to 0
        for (TicketZone zone : event.getTicketZones()) {
            zone.setAvailableSeats(0);  // Set available seats to zero
        }

        // Recalculate statistics after modifying available seats
        eventStatistics = new EventStatistics(event);

        assertEquals(00.00, eventStatistics.getOccupancyRate());
    }

    @Test
    void getOccupancyRate_MixedOccupancy() {
        // Set some seats as reserved
        for (TicketZone zone : event.getTicketZones()) {
            zone.setAvailableSeats(zone.getAvailableSeats() / 2);
        }
        eventStatistics = new EventStatistics(event);
        assertEquals(50.0, eventStatistics.getOccupancyRate());
    }

    @Test
    void getOccupancyRate_NoSeats_ReturnsZero() {
        Event emptyEvent = new Event(
                "Empty Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );
        EventStatistics emptyStats = new EventStatistics(emptyEvent);
        assertEquals(0.0, emptyStats.getOccupancyRate());
    }

    @Test
    void getOccupancyRateForCategory_ExistingCategory() {
        // First create the statistics with initial state
        EventStatistics initialStats = new EventStatistics(event);

        // Get initial available seats for VIP
        int initialVipSeats = initialStats.getAvailableSeatsPerCategory().get(Category.VIP);

        // Now set half of those seats as taken
        for (TicketZone zone : event.getTicketZones()) {
            if (zone.getCategory() == Category.VIP) {
                zone.setAvailableSeats(initialVipSeats / 2);
            }
        }

        // Create new statistics with updated seats
        eventStatistics = new EventStatistics(event);

        // Calculate expected occupancy: (initialSeats - currentAvailable) / initialSeats * 100
        double expectedOccupancy = ((double)(initialVipSeats - initialVipSeats/2) / initialVipSeats) * 100;
        assertEquals(expectedOccupancy, eventStatistics.getOccupancyRateForCategory(Category.VIP), 0.1);
    }

    @Test
    void getOccupancyRateForCategory_NonExistingCategory() {
        Event eventWithoutVIP = new Event(
                "No VIP Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );
        TicketZone simpleZone = new TicketZone(50.0, Category.SIMPLE,100, 100, eventWithoutVIP);
        eventWithoutVIP.addTicketZone(simpleZone);

        EventStatistics statsWithoutVIP = new EventStatistics(eventWithoutVIP);
        assertEquals(0.0, statsWithoutVIP.getOccupancyRateForCategory(Category.VIP));
    }

    @Test
    void getAvailableSeatsPerCategory_ReturnsCorrectMap() {
        Map<Category, Integer> availableSeats = eventStatistics.getAvailableSeatsPerCategory();
        assertEquals(50, availableSeats.get(Category.VIP));
        assertEquals(100, availableSeats.get(Category.SIMPLE));
        assertEquals(200, availableSeats.get(Category.ARENA));
    }

    @Test
    void getReservedSeatsPerCategory_InitiallyEmpty() {
        Map<Category, Integer> reservedSeats = eventStatistics.getReservedSeatsPerCategory();
        assertEquals(0, reservedSeats.getOrDefault(Category.VIP, 0));
        assertEquals(0, reservedSeats.getOrDefault(Category.SIMPLE, 0));
        assertEquals(0, reservedSeats.getOrDefault(Category.ARENA, 0));
    }

    @Test
    void calculateStatistics_HandlesMixedReservations() {
        // Set different reservation patterns for different zones
        for (TicketZone zone : event.getTicketZones()) {
            if (zone.getCategory() == Category.VIP) {
                zone.setAvailableSeats(25); // 50% reserved
            } else if (zone.getCategory() == Category.SIMPLE) {
                zone.setAvailableSeats(75); // 25% reserved
            } else if (zone.getCategory() == Category.ARENA) {
                zone.setAvailableSeats(150); // 25% reserved
            }
        }

        eventStatistics = new EventStatistics(event);

        Map<Category, Integer> reservedSeats = eventStatistics.getReservedSeatsPerCategory();
        assertEquals(25, reservedSeats.get(Category.VIP));
        assertEquals(25, reservedSeats.get(Category.SIMPLE));
        assertEquals(50, reservedSeats.get(Category.ARENA));
    }
}