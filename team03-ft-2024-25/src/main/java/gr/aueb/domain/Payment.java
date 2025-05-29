package gr.aueb.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payments")
public class Payment extends Transaction{

    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "cvv", nullable = false, length = 3)
    private Integer cvv;

    public Payment() {}

    public Payment(Reservation reservation, LocalDateTime transactionDate,  TransactionStatus status,
                   String cardNumber, String cardHolderName, LocalDateTime expiryDate, Integer cvv) {
        super(reservation, transactionDate, status);
        setCardNumber(cardNumber);
        setCardHolderName(cardHolderName);
        setExpiryDate(expiryDate);
        setCvv(cvv);
        calculateAmount();
    }

    @Override
    public Double calculateAmount() {
        return getReservation().calculateTotalCost();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            throw new IllegalArgumentException("Card number must be 16 digits!");
        }
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        if (cardHolderName == null || cardHolderName.isEmpty()) {
            throw new IllegalArgumentException("Card holder name cannot be null or empty!");
        }
        this.cardHolderName = cardHolderName;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        if (expiryDate == null || expiryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiry date cannot be null or in the past!");
        }
        this.expiryDate = expiryDate;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        if (cvv == null || cvv < 100 || cvv > 999) {
            throw new IllegalArgumentException("CVV must be a 3-digit number!");
        }
        this.cvv = cvv;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        return Objects.equals(cardNumber, payment.cardNumber) &&
                Objects.equals(cardHolderName, payment.cardHolderName) &&
                Objects.equals(expiryDate, payment.expiryDate) &&
                Objects.equals(cvv, payment.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, cardHolderName, expiryDate, cvv);
    }

}