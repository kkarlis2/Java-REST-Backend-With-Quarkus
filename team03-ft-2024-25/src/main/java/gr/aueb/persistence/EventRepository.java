package gr.aueb.persistence;

import gr.aueb.domain.Event;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequestScoped
public class EventRepository implements PanacheRepository<Event> {

    public List<Event> findByDate(LocalDate date) {
        return list("date", date);
    }

    public List<Event> findByTitleContaining(String titlePart) {
        return list("title like ?1", "%" + titlePart + "%");
    }

    public List<Event> findUpcomingEvents() {
        return list("date >= ?1", LocalDate.now());
    }


}
