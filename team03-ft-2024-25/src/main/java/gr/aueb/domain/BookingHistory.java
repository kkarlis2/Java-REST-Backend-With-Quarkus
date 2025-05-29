package gr.aueb.domain;

import java.util.List;

public class BookingHistory {
    private final Visitor visitor;
    private final List<Reservation> reservations;

    public BookingHistory(Visitor visitor, List<Reservation> reservations) {
        this.visitor = visitor;
        this.reservations = reservations;
    }

    public List<Reservation> getReservations() {
        Account account = visitor.getAccount();
        if (account == null) {
            throw new IllegalStateException("Only registered visitors can access booking history");
        }
        return reservations;
    }

    public void cancelReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation not found in visitor's history");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        // Update available seats in ticket zone
        TicketZone ticketZone = reservation.getTicketZone();
        ticketZone.setAvailableSeats(ticketZone.getAvailableSeats() + reservation.getReservedSeats());
    }
}