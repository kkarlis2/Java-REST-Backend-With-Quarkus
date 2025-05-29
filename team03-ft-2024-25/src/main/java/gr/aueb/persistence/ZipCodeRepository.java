package gr.aueb.persistence;

import gr.aueb.domain.ZipCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.RequestScoped;

import java.util.Optional;

@RequestScoped
public class ZipCodeRepository implements PanacheRepository<ZipCode> {

    public Optional<ZipCode> findByCode(String code) {
        return find("zipCode", code).firstResultOptional();
    }

    public boolean exists(String code) {
        return count("zipCode", code) > 0;
    }
}