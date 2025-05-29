package gr.aueb.representation;

import gr.aueb.domain.Category;
import io.quarkus.runtime.annotations.RegisterForReflection;
import gr.aueb.domain.*;
import gr.aueb.persistence.OrganizerRepository;
import gr.aueb.representation.EventRepresentation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RegisterForReflection
public class BookingHistoryRepresentation {
    public List<ReservationInfo> reservations;


    public List<ReservationInfo> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationInfo> reservations) {
        this.reservations = reservations;
    }

    public static class ReservationInfo {
        public Integer reservationId;
        public String eventTitle;
        public LocalDate eventDate;
        public LocalTime eventTime;
        public int ticketCount;
        public String ticketCategory;
        public double totalAmount;
        public ReservationStatus status;

        public Integer getReservationId() {
            return reservationId;
        }

        public void setReservationId(Integer reservationId) {
            this.reservationId = reservationId;
        }

        public String getEventTitle() {
            return eventTitle;
        }

        public void setEventTitle(String eventTitle) {
            this.eventTitle = eventTitle;
        }

        public LocalDate getEventDate() {
            return eventDate;
        }

        public void setEventDate(LocalDate eventDate) {
            this.eventDate = eventDate;
        }

        public LocalTime getEventTime() {
            return eventTime;
        }

        public void setEventTime(LocalTime eventTime) {
            this.eventTime = eventTime;
        }

        public int getTicketCount() {
            return ticketCount;
        }

        public void setTicketCount(int ticketCount) {
            this.ticketCount = ticketCount;
        }

        public String getTicketCategory() {
            return ticketCategory;
        }

        public void setTicketCategory(String ticketCategory) {
            this.ticketCategory = ticketCategory;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public ReservationStatus getStatus() {
            return status;
        }

        public void setStatus(ReservationStatus status) {
            this.status = status;
        }
    }
}