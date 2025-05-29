package gr.aueb.representation;

import gr.aueb.domain.DiscountCat;
import gr.aueb.domain.ReservationStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;
import java.util.Set;

@RegisterForReflection
public class ReservationRepresentation {
    public Integer id;
    public Integer visitorId;
    public Integer ticketZoneId;
    public Integer reservedSeats;
    public LocalDateTime reservationDate;
    public ReservationStatus status;
    public DiscountCat discount;
    public Set<TransactionRepresentation> transactions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(Integer visitorId) {
        this.visitorId = visitorId;
    }

    public Integer getTicketZoneId() {
        return ticketZoneId;
    }

    public void setTicketZoneId(Integer ticketZoneId) {
        this.ticketZoneId = ticketZoneId;
    }

    public Integer getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(Integer reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public DiscountCat getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountCat discount) {
        this.discount = discount;
    }

    public Set<TransactionRepresentation> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<TransactionRepresentation> transactions) {
        this.transactions = transactions;
    }
}
