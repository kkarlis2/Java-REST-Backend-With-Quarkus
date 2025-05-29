package gr.aueb.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "transactions")
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    public Transaction() {}

    public Transaction(Reservation reservation, LocalDateTime transactionDate,  TransactionStatus status) {
        setReservation(reservation);
        setTransactionDate(transactionDate);
        setStatus(status);
    }


    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null!");
        }
        this.reservation = reservation;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null!");
        }
        this.transactionDate = transactionDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero!");
        }
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Transaction status cannot be null!");
        }
        this.status = status;
    }

    public abstract Double calculateAmount();

    @PrePersist
    public void prePersist() {
        this.amount = calculateAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Transaction)) return false;

        Transaction that = (Transaction) o;

        return  Objects.equals(reservation, that.reservation) &&
                Objects.equals(transactionDate, that.transactionDate) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservation, transactionDate, status);
    }




}
