package gr.aueb.persistence;

import gr.aueb.domain.Organizer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;

import java.util.Optional;

@RequestScoped
public class OrganizerRepository implements PanacheRepository<Organizer> {

    public Optional<Organizer> findByTaxId(String taxId) {
        return find("taxId", taxId).firstResultOptional();
    }

    public Optional<Organizer> findByUsername(String username) {
        return find("account.username", username).firstResultOptional();
    }
}
