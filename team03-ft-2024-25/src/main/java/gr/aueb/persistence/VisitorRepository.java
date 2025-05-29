package gr.aueb.persistence;

import gr.aueb.domain.Reservation;
import gr.aueb.domain.Visitor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class VisitorRepository implements PanacheRepository<Visitor> {


    public Optional<Visitor> findByUsername(String username) {
        return find("account.username", username).firstResultOptional();
    }

    public Optional<Visitor> findByEmail(String email) {
        return find("email.email", email).firstResultOptional();
    }

    public Optional<Visitor> findByPhoneNumber(String phoneNumber) {
        return find("phoneNumber", phoneNumber).firstResultOptional();
    }


}

