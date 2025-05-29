package gr.aueb.representation;

import gr.aueb.domain.Category;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Map;
import java.util.HashMap;
import gr.aueb.domain.*;
import gr.aueb.domain.TicketZone;
import gr.aueb.persistence.OrganizerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;
@ApplicationScoped
@Mapper(componentModel = "jakarta")
public abstract class EventStatisticsMapper {

    public EventStatisticsRepresentation toRepresentation(EventStatistics statistics) {
        EventStatisticsRepresentation repr = new EventStatisticsRepresentation();
        repr.eventId = statistics.getEvent().getId();
        repr.totalAvailableSeats = statistics.getTotalAvailableSeats();
        repr.totalReservedSeats = statistics.getTotalReservedSeats();
        repr.occupancyRate = statistics.getOccupancyRate();

        repr.categoriesStats = new HashMap<>();
        for (Category category : Category.values()) {
            EventStatisticsRepresentation.CategoryStatistics catStats = new EventStatisticsRepresentation.CategoryStatistics();
            catStats.availableSeats = statistics.getAvailableSeatsPerCategory().getOrDefault(category, 0);
            catStats.reservedSeats = statistics.getReservedSeatsPerCategory().getOrDefault(category, 0);
            catStats.occupancyRate = statistics.getOccupancyRateForCategory(category);
            repr.categoriesStats.put(category.name(), catStats);
        }

        return repr;
    }
}