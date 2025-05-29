package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.persistence.OrganizerRepository;
import gr.aueb.representation.EventRepresentation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;
import java.util.stream.Collectors;
import java.util.List;

@ApplicationScoped
@Mapper(componentModel = "jakarta")
public abstract class BookingHistoryMapper {

    public BookingHistoryRepresentation toRepresentation(BookingHistory history) {
        BookingHistoryRepresentation repr = new BookingHistoryRepresentation();
        repr.reservations = history.getReservations().stream()
                .map(this::toReservationInfo)
                .collect(Collectors.toList());
        return repr;
    }

    private BookingHistoryRepresentation.ReservationInfo toReservationInfo(Reservation reservation) {
        BookingHistoryRepresentation.ReservationInfo info = new BookingHistoryRepresentation.ReservationInfo();
        info.reservationId = reservation.getId();
        info.eventTitle = reservation.getTicketZone().getEvent().getTitle();
        info.eventDate = reservation.getTicketZone().getEvent().getDate();
        info.eventTime = reservation.getTicketZone().getEvent().getTime();
        info.ticketCount = reservation.getReservedSeats();
        info.ticketCategory = reservation.getTicketZone().getCategory().name();
        info.totalAmount = reservation.calculateTotalCost();
        info.status = reservation.getStatus();
        return info;
    }
}