package gr.aueb.representation;

import gr.aueb.domain.*;
import gr.aueb.persistence.ReservationRepository;
import jakarta.inject.Inject;
import org.mapstruct.*;

@Mapper(componentModel = "jakarta",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class TransactionMapper {

    @Inject
    protected ReservationRepository reservationRepository;

    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionDate", source = "transactionDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "type", expression = "java(transaction.getClass().getSimpleName())")
    public abstract TransactionRepresentation toRepresentation(Transaction transaction);

    @BeforeMapping
    protected void beforeMapping(Transaction source, @MappingTarget TransactionRepresentation target) {
        if (source != null) {
            target.status = source.getStatus().name();
            target.type = source.getClass().getSimpleName();
        }
    }

    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionDate", source = "transactionDate")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())")
    @Mapping(target = "type", constant = "Payment")
    @Mapping(target = "cardNumber", source = "cardNumber")
    @Mapping(target = "cardHolderName", source = "cardHolderName")
    @Mapping(target = "expiryDate", source = "expiryDate")
    @Mapping(target = "cvv", source = "cvv")
    public abstract PaymentRepresentation paymentToRepresentation(Payment payment);

    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionDate", source = "transactionDate")
    @Mapping(target = "status", expression = "java(refund.getStatus().name())")
    @Mapping(target = "type", constant = "Refund")
    @Mapping(target = "refundAmount", source = "refundAmount")
    @Mapping(target = "feePercentage", source = "feePercentage")
    public abstract RefundRepresentation refundToRepresentation(Refund refund);

    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(target = "cardHolderName", ignore = true)
    @Mapping(target = "expiryDate", ignore = true)
    @Mapping(target = "cvv", ignore = true)
    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionDate", source = "transactionDate")
    @Mapping(target = "status", expression = "java(gr.aueb.domain.TransactionStatus.valueOf(representation.status))")
    protected abstract Payment mapToPayment(TransactionRepresentation representation);

    @Mapping(target = "feePercentage", ignore = true)
    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionDate", source = "transactionDate")
    @Mapping(target = "status", expression = "java(gr.aueb.domain.TransactionStatus.valueOf(representation.status))")
    protected abstract Refund mapToRefund(TransactionRepresentation representation);

    @AfterMapping
    protected void afterToModel(TransactionRepresentation representation, @MappingTarget Transaction transaction) {
        if (representation.reservationId != null) {
            Reservation reservation = reservationRepository.findById(representation.reservationId.longValue());
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found with ID: " + representation.reservationId);
            }
            transaction.setReservation(reservation);
        }
    }
}