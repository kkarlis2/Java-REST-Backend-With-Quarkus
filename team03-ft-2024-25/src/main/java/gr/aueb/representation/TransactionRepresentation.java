package gr.aueb.representation;

import gr.aueb.domain.TransactionStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

@RegisterForReflection
public class TransactionRepresentation {
    public Integer id;
    public Integer reservationId;
    public LocalDateTime transactionDate;
    public Double amount;
    public String status; // Enum value as a string
    public String type; // "Payment" or "Refund"



    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.type = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

