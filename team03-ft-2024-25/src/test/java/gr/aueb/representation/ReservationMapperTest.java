package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.persistence.VisitorRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class ReservationMapperTest {

    @Inject
    ReservationMapper reservationMapper;

    @Inject
    VisitorRepository visitorRepository;

    @BeforeEach
    void setup() {
        visitorRepository.deleteAll();
    }

    @AfterEach
    void cleanup(){
        visitorRepository.deleteAll();
    }

    private Visitor createTestVisitor() {
        Visitor visitor = new Visitor(
                "John",              // firstName
                "Doe",               // lastName
                "210-1234567",       // phoneNumber
                "john@example.com",  // email
                "johndoe",           // username
                "password123"        // password
        );
        visitorRepository.persist(visitor);
        return visitor;
    }

    private TicketZone createTestTicketZone() {
        TicketZone ticketZone = new TicketZone();
        ticketZone.setId(1);
        ticketZone.setCost(50.0);
        ticketZone.setAvailableSeats(100);
        return ticketZone;
    }

    @Test
    public void testToRepresentation() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        TicketZone testTicketZone = createTestTicketZone();
        LocalDateTime now = LocalDateTime.now();

        Reservation reservation = new Reservation(
                testVisitor,
                testTicketZone,
                2,                    // reservedSeats
                now,                  // reservationDate
                ReservationStatus.PENDING,
                DiscountCat.STUDENT
        );

        // Act
        ReservationRepresentation representation = reservationMapper.toRepresentation(reservation);

        // Assert
        assertNotNull(representation);
        assertEquals(testVisitor.getId(), representation.visitorId);
        assertEquals(testTicketZone.getId(), representation.ticketZoneId);
        assertEquals(2, representation.reservedSeats);
        assertEquals(now, representation.reservationDate);
        assertEquals(ReservationStatus.PENDING, representation.status);
        assertEquals(DiscountCat.STUDENT, representation.discount);
        assertNotNull(representation.transactions);
        assertTrue(representation.transactions.isEmpty());
    }

    @Test
    public void testToEntity() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        TicketZone testTicketZone = createTestTicketZone();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = testVisitor.getId();
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = LocalDateTime.now();
        representation.status = ReservationStatus.PENDING;
        representation.discount = DiscountCat.STUDENT;
        representation.transactions = new HashSet<>();

        // Act
        Reservation reservation = reservationMapper.toEntity(representation);

        // Assert
        assertNotNull(reservation);
        assertEquals(testVisitor.getId(), reservation.getVisitor().getId());
        assertEquals(2, reservation.getReservedSeats());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(DiscountCat.STUDENT, reservation.getDiscount());
        assertTrue(reservation.getTransactions().isEmpty());
    }


    @Test
    public void testToEntityWithPaymentTransaction() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        TicketZone testTicketZone = createTestTicketZone();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = testVisitor.getId();
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = LocalDateTime.now();
        representation.status = ReservationStatus.PENDING;
        representation.discount = DiscountCat.STUDENT;

        // Add payment transaction
        TransactionRepresentation paymentRep = new TransactionRepresentation();
        paymentRep.type = "Payment";
        paymentRep.amount = 100.0;
        paymentRep.transactionDate = LocalDateTime.now();
        paymentRep.status = TransactionStatus.SUCCESS.name();

        Set<TransactionRepresentation> transactions = new HashSet<>();
        transactions.add(paymentRep);
        representation.transactions = transactions;

        // Act
        Reservation reservation = reservationMapper.toEntity(representation);

        // Set the ticketZone before checking transactions
        reservation.setTicketZone(testTicketZone);

        // Add transactions after setting ticketZone
        for (TransactionRepresentation transactionRep : representation.transactions) {
            Transaction transaction;
            if ("Payment".equals(transactionRep.type)) {
                transaction = new Payment();
            } else {
                transaction = new Refund();
            }
            transaction.setAmount(transactionRep.amount);
            transaction.setTransactionDate(transactionRep.transactionDate);
            transaction.setStatus(TransactionStatus.valueOf(transactionRep.status));
            reservation.addTransaction(transaction);
        }

        // Assert
        assertNotNull(reservation);
        assertFalse(reservation.getTransactions().isEmpty());
        assertEquals(1, reservation.getTransactions().size());
        Transaction transaction = reservation.getTransactions().iterator().next();
        assertTrue(transaction instanceof Payment);
        assertEquals(100.0, transaction.getAmount());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    public void testInvalidVisitorId() {
        // Arrange
        TicketZone testTicketZone = createTestTicketZone();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = -1; // Invalid ID
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = LocalDateTime.now();
        representation.status = ReservationStatus.PENDING;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                        reservationMapper.toEntity(representation),
                "Should throw exception for invalid visitor ID"
        );
    }

    @Test
    public void testNullVisitorId() {
        // Arrange
        TicketZone testTicketZone = createTestTicketZone();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = null;
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = LocalDateTime.now();
        representation.status = ReservationStatus.PENDING;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                        reservationMapper.toEntity(representation),
                "Should throw exception for null visitor ID"
        );
    }

    @Test
    public void testAddTransactionsWithPaymentAndRefund() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        TicketZone testTicketZone = createTestTicketZone();
        LocalDateTime now = LocalDateTime.now();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = testVisitor.getId();
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = now;
        representation.status = ReservationStatus.PENDING;

        // Create payment transaction
        TransactionRepresentation paymentRep = new TransactionRepresentation();
        paymentRep.type = "Payment";
        paymentRep.amount = 100.0;
        paymentRep.transactionDate = now;
        paymentRep.status = TransactionStatus.SUCCESS.name();

        // Create refund transaction
        RefundRepresentation refundRep = new RefundRepresentation();
        refundRep.type = "Refund";
        refundRep.amount = 50.0;
        refundRep.transactionDate = now;
        refundRep.status = TransactionStatus.SUCCESS.name();
        refundRep.refundAmount = 45.0;
        refundRep.feePercentage = 10.0;

        Set<TransactionRepresentation> transactions = new HashSet<>();
        transactions.add(paymentRep);
        transactions.add(refundRep);
        representation.transactions = transactions;

        // Act
        Reservation reservation = reservationMapper.toEntity(representation);
        reservation.setTicketZone(testTicketZone); // Σημαντικό: πρέπει να οριστεί το ticketZone
        reservationMapper.addTransactions(reservation, representation);

        // Assert
        assertNotNull(reservation.getTransactions());
        assertEquals(2, reservation.getTransactions().size());

        // Verify both types of transactions exist
        boolean hasPayment = false;
        boolean hasRefund = false;
        for (Transaction transaction : reservation.getTransactions()) {
            if (transaction instanceof Payment) {
                hasPayment = true;
                assertEquals(100.0, transaction.getAmount());
            } else if (transaction instanceof Refund) {
                hasRefund = true;
                assertEquals(50.0, transaction.getAmount());
            }
        }
        assertTrue(hasPayment, "Should have a Payment transaction");
        assertTrue(hasRefund, "Should have a Refund transaction");
    }

    @Test
    public void testAddTransactionsWithInvalidType() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        TicketZone testTicketZone = createTestTicketZone();
        LocalDateTime now = LocalDateTime.now();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = testVisitor.getId();
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = now;
        representation.status = ReservationStatus.PENDING;

        // Create invalid transaction type
        TransactionRepresentation invalidRep = new TransactionRepresentation();
        invalidRep.type = "InvalidType";
        invalidRep.amount = 100.0;
        invalidRep.transactionDate = now;
        invalidRep.status = TransactionStatus.SUCCESS.name();

        Set<TransactionRepresentation> transactions = new HashSet<>();
        transactions.add(invalidRep);
        representation.transactions = transactions;

        // Act & Assert
        Reservation reservation = reservationMapper.toEntity(representation);
        reservation.setTicketZone(testTicketZone);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationMapper.addTransactions(reservation, representation)
        );
        assertEquals("Unknown transaction type: InvalidType", exception.getMessage());
    }

    @Test
    public void testAddTransactionsWithNullTransactions() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        TicketZone testTicketZone = createTestTicketZone();
        LocalDateTime now = LocalDateTime.now();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = testVisitor.getId();
        representation.ticketZoneId = testTicketZone.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = now;  // Προσθήκη ημερομηνίας
        representation.status = ReservationStatus.PENDING;  // Προσθήκη status
        representation.transactions = null;

        // Act
        Reservation reservation = reservationMapper.toEntity(representation);
        reservation.setTicketZone(testTicketZone);
        reservationMapper.addTransactions(reservation, representation);

        // Assert
        assertTrue(reservation.getTransactions().isEmpty());
    }

    @Test
    public void testAddTransactionsWithNullTicketZone() {
        // Arrange
        Visitor testVisitor = createTestVisitor();
        LocalDateTime now = LocalDateTime.now();

        ReservationRepresentation representation = new ReservationRepresentation();
        representation.visitorId = testVisitor.getId();
        representation.reservedSeats = 2;
        representation.reservationDate = now;  // Προσθήκη ημερομηνίας
        representation.status = ReservationStatus.PENDING;  // Προσθήκη status

        TransactionRepresentation paymentRep = new TransactionRepresentation();
        paymentRep.type = "Payment";
        paymentRep.amount = 100.0;
        paymentRep.transactionDate = now;
        paymentRep.status = TransactionStatus.SUCCESS.name();

        Set<TransactionRepresentation> transactions = new HashSet<>();
        transactions.add(paymentRep);
        representation.transactions = transactions;

        // Act
        Reservation reservation = reservationMapper.toEntity(representation);
        // Δεν ορίζουμε ticketZone
        reservationMapper.addTransactions(reservation, representation);

        // Assert
        assertTrue(reservation.getTransactions().isEmpty());
    }

    @Test
    public void testReservationRepresentationGettersSetters() {
        // Arrange
        ReservationRepresentation representation = new ReservationRepresentation();
        Integer id = 1;
        Integer visitorId = 2;
        Integer ticketZoneId = 3;
        Integer reservedSeats = 4;
        LocalDateTime reservationDate = LocalDateTime.now();
        ReservationStatus status = ReservationStatus.PENDING;
        DiscountCat discount = DiscountCat.STUDENT;
        Set<TransactionRepresentation> transactions = new HashSet<>();

        // Act
        representation.setId(id);
        representation.setVisitorId(visitorId);
        representation.setTicketZoneId(ticketZoneId);
        representation.setReservedSeats(reservedSeats);
        representation.setReservationDate(reservationDate);
        representation.setStatus(status);
        representation.setDiscount(discount);
        representation.setTransactions(transactions);

        // Assert
        assertEquals(id, representation.getId());
        assertEquals(visitorId, representation.getVisitorId());
        assertEquals(ticketZoneId, representation.getTicketZoneId());
        assertEquals(reservedSeats, representation.getReservedSeats());
        assertEquals(reservationDate, representation.getReservationDate());
        assertEquals(status, representation.getStatus());
        assertEquals(discount, representation.getDiscount());
        assertEquals(transactions, representation.getTransactions());
    }

    @Test
    public void testReservationRepresentationNullValues() {
        // Arrange
        ReservationRepresentation representation = new ReservationRepresentation();

        // Assert
        assertNull(representation.getId());
        assertNull(representation.getVisitorId());
        assertNull(representation.getTicketZoneId());
        assertNull(representation.getReservedSeats());
        assertNull(representation.getReservationDate());
        assertNull(representation.getStatus());
        assertNull(representation.getDiscount());
        assertNull(representation.getTransactions());
    }
}
