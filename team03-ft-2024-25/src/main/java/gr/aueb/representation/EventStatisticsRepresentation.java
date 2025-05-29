package gr.aueb.representation;

import gr.aueb.domain.Category;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Map;
import java.util.HashMap;


@RegisterForReflection
public class EventStatisticsRepresentation {
    public Integer eventId;
    public int totalAvailableSeats;
    public int totalReservedSeats;
    public double occupancyRate;
    public Map<String, CategoryStatistics> categoriesStats;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getTotalAvailableSeats() {
        return totalAvailableSeats;
    }

    public void setTotalAvailableSeats(int totalAvailableSeats) {
        this.totalAvailableSeats = totalAvailableSeats;
    }

    public int getTotalReservedSeats() {
        return totalReservedSeats;
    }

    public void setTotalReservedSeats(int totalReservedSeats) {
        this.totalReservedSeats = totalReservedSeats;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public Map<String, CategoryStatistics> getCategoriesStats() {
        return categoriesStats;
    }

    public void setCategoriesStats(Map<String, CategoryStatistics> categoriesStats) {
        this.categoriesStats = categoriesStats;
    }

    public static class CategoryStatistics {
        public int availableSeats;
        public int reservedSeats;
        public double occupancyRate;

        public int getAvailableSeats() {
            return availableSeats;
        }

        public void setAvailableSeats(int availableSeats) {
            this.availableSeats = availableSeats;
        }

        public int getReservedSeats() {
            return reservedSeats;
        }

        public void setReservedSeats(int reservedSeats) {
            this.reservedSeats = reservedSeats;
        }

        public double getOccupancyRate() {
            return occupancyRate;
        }

        public void setOccupancyRate(double occupancyRate) {
            this.occupancyRate = occupancyRate;
        }
    }
}