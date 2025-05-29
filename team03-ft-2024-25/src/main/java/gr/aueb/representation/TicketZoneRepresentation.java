package gr.aueb.representation;

import gr.aueb.domain.Category;

import java.util.List;

public class TicketZoneRepresentation {
    public Integer id;
    public Double cost;
    public Category category; // Αποθηκεύουμε το Enum ως String
    public Integer availableSeats;
    public Integer eventId; // Αναφορά στο event
    public List<ReservationRepresentation> reservations;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public List<ReservationRepresentation> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationRepresentation> reservations) {
        this.reservations = reservations;
    }
}
