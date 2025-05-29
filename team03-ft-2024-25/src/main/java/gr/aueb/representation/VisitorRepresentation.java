package gr.aueb.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Set;

@RegisterForReflection
public class VisitorRepresentation {
    public Integer id;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String username;
    public String password;
    public String email;
    public Set<ReservationRepresentation> reservations;

    // Constructors
    public VisitorRepresentation() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<ReservationRepresentation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<ReservationRepresentation> reservations) {
        this.reservations = reservations;
    }
}