package gr.aueb.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Set;

@RegisterForReflection
public class OrganizerRepresentation {
    public Integer id;
    public String taxId;
    public String brandName;
    public String phoneNumber;
    public String username;
    public String password;
    public String email;
    public String street;
    public String number;
    public String zipCode;
    public Set<EventRepresentation> events;

    // Constructors
    public OrganizerRepresentation() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Set<EventRepresentation> getEvents() {
        return events;
    }

    public void setEvents(Set<EventRepresentation> events) {
        this.events = events;
    }
    @Override
    public String toString() {
        return "OrganizerRepresentation{" +
                "id=" + id +
                ", taxId='" + taxId + '\'' +
                ", brandName='" + brandName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
