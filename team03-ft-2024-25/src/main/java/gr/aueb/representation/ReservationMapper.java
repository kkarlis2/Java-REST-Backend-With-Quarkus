package gr.aueb.representation;


import gr.aueb.domain.*;
import gr.aueb.persistence.VisitorRepository;
import gr.aueb.representation.ReservationRepresentation;
import gr.aueb.representation.TransactionRepresentation;
import jakarta.inject.Inject;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Mapper(componentModel = "jakarta",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {TransactionMapper.class})
public abstract class ReservationMapper {

    @Inject
    protected VisitorRepository visitorRepository;

    @Mapping(target = "visitorId", source = "visitor.id")
    @Mapping(target = "ticketZoneId", source = "ticketZone.id")
    @Mapping(target = "transactions", source = "transactions")
    public abstract ReservationRepresentation toRepresentation(Reservation reservation);

    @Mapping(target = "visitor", expression = "java(findVisitor(representation))")
    @Mapping(target = "ticketZone", ignore = true)
    @Mapping(target = "transactions", expression = "java(createEmptyTransactionSet())")
    public abstract Reservation toEntity(ReservationRepresentation representation);

    public abstract List<ReservationRepresentation> toRepresentationList(List<Reservation> reservations);

    protected Set<Transaction> createEmptyTransactionSet() {
        return new HashSet<>();
    }

    protected Visitor findVisitor(ReservationRepresentation representation) {
        if (representation.visitorId == null) {
            throw new IllegalArgumentException("Visitor ID is required");
        }

        Visitor visitor = visitorRepository.findById(Long.valueOf(representation.visitorId));
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor not found with ID: " + representation.visitorId);
        }
        return visitor;
    }

    @AfterMapping
    protected void addTransactions(@MappingTarget Reservation reservation, ReservationRepresentation representation) {
        // Περιμένουμε να οριστεί το ticketZone πριν προσθέσουμε τα transactions
        if (representation.transactions != null && reservation.getTicketZone() != null) {
            for (TransactionRepresentation transactionRep : representation.transactions) {
                Transaction transaction;
                switch (transactionRep.type) {
                    case "Payment":
                        transaction = new Payment();
                        break;
                    case "Refund":
                        transaction = new Refund();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown transaction type: " + transactionRep.type);
                }

                transaction.setAmount(transactionRep.amount);
                transaction.setTransactionDate(transactionRep.transactionDate);
                transaction.setStatus(TransactionStatus.valueOf(transactionRep.status));
                reservation.addTransaction(transaction);
            }
        }
    }
}
