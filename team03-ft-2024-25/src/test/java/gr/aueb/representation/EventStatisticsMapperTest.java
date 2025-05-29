package gr.aueb.representation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import gr.aueb.domain.*;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EventStatisticsMapperTest {

    @Inject
    EventStatisticsMapper mapper;

    private Event event;
    private EventStatistics statistics;
    private Organizer organizer;

    @BeforeEach
    void setUp() {
        // Setup basic event
        organizer = new Organizer();
        event = new Event(
                "Test Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );
        event.setId(1); // Set a known ID for testing

        // Add ticket zones with different categories and occupancy levels
        TicketZone vipZone = new TicketZone(100.0, Category.VIP, 40, 50, event);  // 10 seats reserved
        TicketZone simpleZone = new TicketZone(50.0, Category.SIMPLE, 75, 100, event);  // 25 seats reserved
        TicketZone arenaZone = new TicketZone(75.0, Category.ARENA, 150, 200, event);  // 50 seats reserved

        event.addTicketZone(vipZone);
        event.addTicketZone(simpleZone);
        event.addTicketZone(arenaZone);

        statistics = new EventStatistics(event);
    }

    @Test
    void whenMappingEventStatistics_thenAllFieldsAreMappedCorrectly() {
        // Act
        EventStatisticsRepresentation result = mapper.toRepresentation(statistics);

        // Assert
        // Check basic fields
        assertEquals(event.getId(), result.eventId);
        assertEquals(statistics.getTotalAvailableSeats(), result.totalAvailableSeats);
        assertEquals(statistics.getTotalReservedSeats(), result.totalReservedSeats);
        assertEquals(statistics.getOccupancyRate(), result.occupancyRate, 0.01);

        // Verify that all categories are present
        for (Category category : Category.values()) {
            assertTrue(result.categoriesStats.containsKey(category.name()));
        }
    }

    @Test
    void whenMappingCategories_thenStatisticsAreCorrect() {
        // Act
        EventStatisticsRepresentation result = mapper.toRepresentation(statistics);

        // Assert VIP category
        EventStatisticsRepresentation.CategoryStatistics vipStats = result.categoriesStats.get(Category.VIP.name());
        assertEquals(40, vipStats.availableSeats);
        assertEquals(10, vipStats.reservedSeats);
        assertEquals(20.0, vipStats.occupancyRate, 0.01);  // 10/50 * 100

        // Assert SIMPLE category
        EventStatisticsRepresentation.CategoryStatistics simpleStats = result.categoriesStats.get(Category.SIMPLE.name());
        assertEquals(75, simpleStats.availableSeats);
        assertEquals(25, simpleStats.reservedSeats);
        assertEquals(25.0, simpleStats.occupancyRate, 0.01);  // 25/100 * 100

        // Assert ARENA category
        EventStatisticsRepresentation.CategoryStatistics arenaStats = result.categoriesStats.get(Category.ARENA.name());
        assertEquals(150, arenaStats.availableSeats);
        assertEquals(50, arenaStats.reservedSeats);
        assertEquals(25.0, arenaStats.occupancyRate, 0.01);  // 50/200 * 100
    }

    @Test
    void whenEventHasNoZonesForCategory_thenStatisticsAreZero() {
        // Arrange
        Event emptyEvent = new Event(
                "Empty Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );
        emptyEvent.setId(2);

        // Only add VIP zone
        TicketZone vipZone = new TicketZone(100.0, Category.VIP, 50, 50, emptyEvent);
        emptyEvent.addTicketZone(vipZone);

        EventStatistics emptyStats = new EventStatistics(emptyEvent);

        // Act
        EventStatisticsRepresentation result = mapper.toRepresentation(emptyStats);

        // Assert
        // Check SIMPLE category stats (should be zero)
        EventStatisticsRepresentation.CategoryStatistics simpleStats = result.categoriesStats.get(Category.SIMPLE.name());
        assertEquals(0, simpleStats.availableSeats);
        assertEquals(0, simpleStats.reservedSeats);
        assertEquals(0.0, simpleStats.occupancyRate, 0.01);
    }

    @Test
    void whenAllSeatsAreReserved_thenOccupancyRateIs100Percent() {
        // Arrange
        Event fullEvent = new Event(
                "Full Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );
        fullEvent.setId(3);

        // Add zone with all seats reserved
        TicketZone fullZone = new TicketZone(100.0, Category.VIP, 0, 100, fullEvent);
        fullEvent.addTicketZone(fullZone);

        EventStatistics fullStats = new EventStatistics(fullEvent);

        // Act
        EventStatisticsRepresentation result = mapper.toRepresentation(fullStats);

        // Assert
        EventStatisticsRepresentation.CategoryStatistics vipStats = result.categoriesStats.get(Category.VIP.name());
        assertEquals(0, vipStats.availableSeats);
        assertEquals(100, vipStats.reservedSeats);
        assertEquals(100.0, vipStats.occupancyRate, 0.01);
    }

    @Test
    void whenEventIsEmpty_thenAllStatisticsAreZero() {
        // Arrange
        Event emptyEvent = new Event(
                "Empty Event",
                LocalDate.now(),
                LocalTime.now(),
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                organizer
        );
        emptyEvent.setId(4);
        EventStatistics emptyStats = new EventStatistics(emptyEvent);

        // Act
        EventStatisticsRepresentation result = mapper.toRepresentation(emptyStats);

        // Assert
        assertEquals(0, result.totalAvailableSeats);
        assertEquals(0, result.totalReservedSeats);
        assertEquals(0.0, result.occupancyRate, 0.01);

        for (Category category : Category.values()) {
            EventStatisticsRepresentation.CategoryStatistics stats = result.categoriesStats.get(category.name());
            assertEquals(0, stats.availableSeats);
            assertEquals(0, stats.reservedSeats);
            assertEquals(0.0, stats.occupancyRate, 0.01);
        }
    }

    @Test
    void testEventStatisticsRepresentationGettersSetters() {
        // Arrange
        EventStatisticsRepresentation statsRep = new EventStatisticsRepresentation();

        // Test data
        Integer eventId = 1;
        int totalAvailableSeats = 100;
        int totalReservedSeats = 50;
        double occupancyRate = 50.0;
        Map<String, EventStatisticsRepresentation.CategoryStatistics> categoriesStats = new HashMap<>();

        // Act
        statsRep.setEventId(eventId);
        statsRep.setTotalAvailableSeats(totalAvailableSeats);
        statsRep.setTotalReservedSeats(totalReservedSeats);
        statsRep.setOccupancyRate(occupancyRate);
        statsRep.setCategoriesStats(categoriesStats);

        // Assert
        assertEquals(eventId, statsRep.getEventId());
        assertEquals(totalAvailableSeats, statsRep.getTotalAvailableSeats());
        assertEquals(totalReservedSeats, statsRep.getTotalReservedSeats());
        assertEquals(occupancyRate, statsRep.getOccupancyRate(), 0.001);
        assertEquals(categoriesStats, statsRep.getCategoriesStats());
    }

    @Test
    void testCategoryStatisticsGettersSetters() {
        // Arrange
        EventStatisticsRepresentation.CategoryStatistics categoryStats =
                new EventStatisticsRepresentation.CategoryStatistics();

        // Test data
        int availableSeats = 80;
        int reservedSeats = 20;
        double occupancyRate = 20.0;

        // Act
        categoryStats.setAvailableSeats(availableSeats);
        categoryStats.setReservedSeats(reservedSeats);
        categoryStats.setOccupancyRate(occupancyRate);

        // Assert
        assertEquals(availableSeats, categoryStats.getAvailableSeats());
        assertEquals(reservedSeats, categoryStats.getReservedSeats());
        assertEquals(occupancyRate, categoryStats.getOccupancyRate(), 0.001);
    }
}
