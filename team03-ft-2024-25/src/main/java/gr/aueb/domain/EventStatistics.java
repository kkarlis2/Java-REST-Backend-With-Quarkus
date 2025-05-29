package gr.aueb.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventStatistics {
    private final Event event;
    private int totalAvailableSeats;
    private int totalReservedSeats;
    private Map<Category, Integer> availableSeatsPerCategory;
    private Map<Category, Integer> reservedSeatsPerCategory;

    public EventStatistics(Event event) {
        this.event = event;
        this.availableSeatsPerCategory = new HashMap<>();
        this.reservedSeatsPerCategory = new HashMap<>();
        calculateStatistics();
    }

    private void calculateStatistics() {
        // Reset totals before recalculating
        totalAvailableSeats = 0;
        totalReservedSeats = 0;
        availableSeatsPerCategory.clear();
        reservedSeatsPerCategory.clear();

        for (TicketZone zone : event.getTicketZones()) {
            Category category = zone.getCategory();
            int availableSeats = zone.getAvailableSeats();
            int reservedSeats = calculateReservedSeats(zone);


            availableSeatsPerCategory.merge(category, availableSeats, Integer::sum);
            reservedSeatsPerCategory.merge(category, reservedSeats, Integer::sum);

            totalAvailableSeats += availableSeats;
            totalReservedSeats += reservedSeats;
        }
    }

    private int calculateReservedSeats(TicketZone zone) {
        int totalCapacity = zone.getTotalCapacity();  // Assuming the zone has this method to get total seats
        int currentAvailable = zone.getAvailableSeats();
        return totalCapacity - currentAvailable;
    }

    public double getOccupancyRate() {
        if (totalAvailableSeats == 0) return 0;
        return (double) totalReservedSeats / (totalReservedSeats + totalAvailableSeats) * 100;
    }

    public double getOccupancyRateForCategory(Category category) {
        int available = availableSeatsPerCategory.getOrDefault(category, 0);
        int reserved = reservedSeatsPerCategory.getOrDefault(category, 0);
        if (available + reserved == 0) return 0;
        return (double) reserved / (available + reserved) * 100;
    }

    public int getTotalAvailableSeats() {
        return totalAvailableSeats;
    }

    public int getTotalReservedSeats() {
        return totalReservedSeats;
    }

    public Event getEvent() {
        return event;
    }

    public Map<Category, Integer> getAvailableSeatsPerCategory() {
        return availableSeatsPerCategory;
    }

    public Map<Category, Integer> getReservedSeatsPerCategory() {
        return reservedSeatsPerCategory;
    }
}
