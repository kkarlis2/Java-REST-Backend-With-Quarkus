package gr.aueb.persistence;

import gr.aueb.domain.Reservation;
import gr.aueb.domain.ReservationStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class ReservationRepository implements PanacheRepository<Reservation> {
    @Inject
    EntityManager em;

    public void deleteAllWithTransactions() {
        // Πρώτα διαγράφουμε τα transactions
        em.createQuery("DELETE FROM Transaction").executeUpdate();
        // Μετά τα reservations
        deleteAll();
    }
    public List<Reservation> findByVisitorId(Integer visitorId) {
        return list("visitor.id", visitorId);
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        return list("status", status);
    }

    public List<Reservation> findActiveReservationsByVisitor(Integer visitorId) {
        return list("visitor.id = ?1 AND status != ?2",
                visitorId, ReservationStatus.CANCELLED);
    }



}
