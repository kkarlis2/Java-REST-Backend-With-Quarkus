package gr.aueb.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "refunds")
public class Refund extends Transaction{

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Column(name = "fee_percentage", nullable = false)
    private Double feePercentage;

    public Refund() {}

    public Refund(Reservation reservation, LocalDateTime transactionDate,  TransactionStatus status,
                   Double feePercentage) {
        super(reservation, transactionDate,status);
        setFeePercentage(feePercentage);
        CalculateRefundAmount();
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void CalculateRefundAmount() {
        refundAmount=calculateAmount();
    }

    public Double getFeePercentage() {
        return feePercentage;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public void setFeePercentage(Double feePercentage) {
        if (feePercentage == null || feePercentage < 0 || feePercentage > 100) {
            throw new IllegalArgumentException("Fee percentage must be between 0 and 100!");
        }
        this.feePercentage = feePercentage;
        CalculateRefundAmount();

    }

    @Override
    public Double calculateAmount() {
        double totalCost = getReservation().calculateTotalCost();
        return totalCost * (1 - feePercentage / 100.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Refund)) return false;
        if (!super.equals(o)) return false; // Ελέγχει τα κοινά πεδία μέσω της Transaction

        Refund refund = (Refund) o;
        return Objects.equals(feePercentage, refund.feePercentage) &&
                Objects.equals(refundAmount, refund.refundAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feePercentage, refundAmount);
    }



}
