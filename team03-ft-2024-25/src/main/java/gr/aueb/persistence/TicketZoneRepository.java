package gr.aueb.persistence;

import gr.aueb.domain.TicketZone;
import gr.aueb.domain.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class TicketZoneRepository implements PanacheRepository<TicketZone> {

    @Inject
    EntityManager em;

    public List<TicketZone> findByEventId(Integer eventId) {
        return em.createQuery(
                        "SELECT DISTINCT tz FROM TicketZone tz " +
                                "LEFT JOIN FETCH tz.event e " +
                                "WHERE e.id = :eventId", TicketZone.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    public Optional<TicketZone> findByIdWithEvent(Integer id) {
        return em.createQuery(
                        "SELECT tz FROM TicketZone tz " +
                                "LEFT JOIN FETCH tz.event " +
                                "WHERE tz.id = :id", TicketZone.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<TicketZone> findByCategory(Category category) {
        return list("category", category);
    }


}
