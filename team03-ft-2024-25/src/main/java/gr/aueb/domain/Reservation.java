package gr.aueb.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_zone_id", nullable = false)
    private TicketZone ticketZone;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    @Column(name = "reserved_seats", nullable = false)
    private Integer reservedSeats;

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount", nullable = true) // Μπορεί να είναι null αν δεν υπάρχει έκπτωση
    private DiscountCat discount;

    public Reservation() {}

    public Reservation(Visitor visitor, TicketZone ticketZone, Integer reservedSeats, LocalDateTime reservationDate, ReservationStatus status,DiscountCat discount) {
        setVisitor(visitor);
        setTicketZone(ticketZone);
        setReservedSeats(reservedSeats);
        setReservationDate(reservationDate);
        setStatus(status);
        setDiscount(discount);
    }


    public Integer getId(){
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null!");
        }
        this.visitor = visitor;
    }

    public TicketZone getTicketZone() {
        return ticketZone;
    }

    public void setTicketZone(TicketZone ticketZone) {
        if (ticketZone == null) {
            throw new IllegalArgumentException("TicketZone cannot be null!");
        }
        this.ticketZone = ticketZone;
    }

    public Integer getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(Integer reservedSeats) {
        if (reservedSeats == null || reservedSeats <= 0) {
            throw new IllegalArgumentException("Reserved seats must be greater than zero!");
        }
        this.reservedSeats = reservedSeats;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        if (reservationDate == null) {
            throw new IllegalArgumentException("Reservation date cannot be null!");
        }
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Reservation status cannot be null!");
        }
        this.status = status;
    }

    public DiscountCat getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountCat discount) {
        this.discount = discount;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction){
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null!");
        }

        int paymentCount = 0;
        int refundCount = 0;

        for (Transaction t : transactions) {
            if (t instanceof Payment) {
                paymentCount++;
            } else if (t instanceof Refund) {
                refundCount++;
            }
        }



        if (transaction instanceof Payment && paymentCount >= 1) {
            throw new IllegalArgumentException("A reservation can only have one payment!");
        }
        if (transaction instanceof Refund && refundCount >= 1) {
            throw new IllegalArgumentException("A reservation can only have one refund!");
        }

        if (transaction instanceof Payment) {
            paymentCount++;
        } else if (transaction instanceof Refund) {
            refundCount++;
        }

        transactions.add(transaction);
        transaction.setReservation(this);


        if (refundCount >= 1) {
            this.status = ReservationStatus.CANCELLED;
            increaseAvailableSeats();
        } else if (paymentCount >= 0 && transaction instanceof Payment) {
            this.status = ReservationStatus.CONFIRMED;
            decreaseAvailableSeats();
        }
    }

    private void decreaseAvailableSeats() {
        ticketZone.setAvailableSeats(ticketZone.getAvailableSeats() - reservedSeats);
    }

    private void increaseAvailableSeats() {
        ticketZone.setAvailableSeats(ticketZone.getAvailableSeats() + reservedSeats);
    }

    public double calculateTotalCost() {
        double baseCost = ticketZone.getCost() * reservedSeats;
        if (discount == null) {
            return baseCost;
        }else if(discount== DiscountCat.STUDENT){
            return baseCost*0.8;
        }else{
            return baseCost*0.5;
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(visitor, that.visitor) &&
                Objects.equals(ticketZone, that.ticketZone) &&
                Objects.equals(reservationDate, that.reservationDate) &&
                Objects.equals(reservedSeats, that.reservedSeats) &&
                status == that.status &&
                discount==that.discount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitor, ticketZone, reservationDate, reservedSeats, status,discount);
    }

}
