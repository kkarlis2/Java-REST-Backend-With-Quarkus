package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.domain.Refund;
import gr.aueb.domain.TransactionStatus;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.inject.Inject;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
public class TransactionMapperTest {

    @Inject
    TransactionMapper transactionMapper;

    private LocalDateTime testDate;
    private LocalDateTime expiryDate;
    private TicketZone ticketZone;
    private Visitor visitor;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        expiryDate = testDate.plusYears(2);

        ticketZone = new TicketZone();
        ticketZone.setCost(100.0);
        ticketZone.setAvailableSeats(50);

        visitor = new Visitor();
        visitor.setId(1);

        reservation = new Reservation();
        reservation.setId(1);
        reservation.setVisitor(visitor);
        reservation.setTicketZone(ticketZone);
        reservation.setReservedSeats(2);
        reservation.setReservationDate(testDate);
        reservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void testPaymentToRepresentation() {
        Payment payment = new Payment();
        payment.setAmount(100.0);
        payment.setTransactionDate(testDate);
        payment.setStatus(TransactionStatus.SUCCESS);
        payment.setCardNumber("1234567890123456");
        payment.setCardHolderName("John Doe");
        payment.setExpiryDate(expiryDate);
        payment.setCvv(123);
        payment.setReservation(reservation);

        PaymentRepresentation representation = transactionMapper.paymentToRepresentation(payment);

        assertNotNull(representation);
        assertEquals(payment.getAmount(), representation.amount);
        assertEquals(payment.getTransactionDate(), representation.transactionDate);
        assertEquals(payment.getStatus().name(), representation.status);
        assertEquals("Payment", representation.type);
        assertEquals(payment.getCardNumber(), representation.getCardNumber());
        assertEquals(payment.getCardHolderName(), representation.getCardHolderName());
        assertEquals(payment.getExpiryDate(), representation.getExpiryDate());
        assertEquals(payment.getCvv(), representation.getCvv());
        assertEquals(payment.getReservation().getId().intValue(), representation.reservationId);
    }

    @Test
    void testRefundToRepresentation() {
        // Setup
        Refund refund = new Refund();
        refund.setReservation(reservation);
        refund.setTransactionDate(testDate);
        refund.setStatus(TransactionStatus.SUCCESS);
        refund.setFeePercentage(10.0);
        refund.setAmount(180.0);

        // Act
        RefundRepresentation representation = transactionMapper.refundToRepresentation(refund);

        // Debug
        System.out.println("Type: " + representation.type);
        System.out.println("Status: " + representation.status);

        // Assert
        assertNotNull(representation, "Representation should not be null");
        assertEquals("Refund", representation.type, "Type should be 'Refund'");
        // Υπόλοιπα assertions...
    }

    @Test
    void testToRepresentationForPayment() {
        Payment payment = new Payment();
        payment.setAmount(200.0);
        payment.setTransactionDate(testDate);
        payment.setStatus(TransactionStatus.FAILED);
        payment.setReservation(reservation);

        TransactionRepresentation representation = transactionMapper.toRepresentation(payment);

        assertNotNull(representation);
        assertEquals(payment.getAmount(), representation.amount);
        assertEquals(payment.getTransactionDate(), representation.transactionDate);
        assertEquals(payment.getStatus().name(), representation.status);
        assertEquals("Payment", representation.type);
        assertEquals(payment.getReservation().getId().intValue(), representation.reservationId);
    }

    @Test
    void testToRepresentationForRefund() {
        Refund refund = new Refund();
        refund.setAmount(75.0);
        refund.setTransactionDate(testDate);
        refund.setStatus(TransactionStatus.SUCCESS);
        refund.setReservation(reservation);
        refund.setFeePercentage(10.0);

        TransactionRepresentation representation = transactionMapper.toRepresentation(refund);

        assertNotNull(representation);
        assertEquals(refund.getAmount(), representation.amount);
        assertEquals(refund.getTransactionDate(), representation.transactionDate);
        assertEquals(refund.getStatus().name(), representation.status);
        assertEquals("Refund", representation.type);
        assertEquals(refund.getReservation().getId().intValue(), representation.reservationId);
    }

    @Test
    void testNullTransaction() {
        TransactionRepresentation representation = transactionMapper.toRepresentation(null);
        assertNull(representation);
    }

    @Test
    void testTransactionRepresentationGettersSetters() {
        // Arrange
        TransactionRepresentation representation = new TransactionRepresentation();
        LocalDateTime now = LocalDateTime.now();

        // Test ReservationId
        representation.setReservationId(123);
        assertEquals(123, representation.getReservationId());

        // Test Amount
        representation.setAmount(150.0);
        assertEquals(150.0, representation.getAmount());

        // Test TransactionDate
        representation.setTransactionDate(now);
        assertEquals(now, representation.getTransactionDate());


        representation.status = "SUCCESS";  // Απευθείας ανάθεση στο public πεδίο
        assertEquals("SUCCESS", representation.getStatus());


        // Test Type
        representation.setType("Payment");
        assertEquals("Payment", representation.getType());
    }

    @Test
    void testRefundRepresentationGettersSetters() {
        // Arrange
        RefundRepresentation refundRepresentation = new RefundRepresentation();

        // Test refundAmount
        refundRepresentation.setRefundAmount(100.0);
        assertEquals(100.0, refundRepresentation.getRefundAmount());

        // Test feePercentage
        refundRepresentation.setFeePercentage(10.0);
        assertEquals(10.0, refundRepresentation.getFeePercentage());
    }


    @Test
    void testAfterMappingWithInvalidReservationId() {
        // Arrange
        TransactionRepresentation representation = new TransactionRepresentation();
        representation.setReservationId(999); // ID που δεν υπάρχει
        representation.setAmount(100.0);      // Προσθήκη έγκυρου amount
        representation.setTransactionDate(testDate);
        representation.status = "SUCCESS";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transactionMapper.mapToPayment(representation)
        );

        assertEquals("Reservation not found with ID: 999", exception.getMessage());
    }

    @Test
    void testAfterMappingWithNullReservationId() {
        // Arrange
        TransactionRepresentation representation = new TransactionRepresentation();
        representation.setReservationId(null);
        representation.setAmount(100.0);      // Προσθήκη έγκυρου amount
        representation.setTransactionDate(testDate);
        representation.status = "SUCCESS";

        // Act
        Payment mappedPayment = transactionMapper.mapToPayment(representation);

        // Assert
        assertNull(mappedPayment.getReservation());
        assertEquals(100.0, mappedPayment.getAmount());
    }
}