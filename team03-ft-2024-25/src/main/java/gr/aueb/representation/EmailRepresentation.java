package gr.aueb.representation;

import gr.aueb.domain.Email;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class EmailRepresentation {
    public Email email;

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
}
