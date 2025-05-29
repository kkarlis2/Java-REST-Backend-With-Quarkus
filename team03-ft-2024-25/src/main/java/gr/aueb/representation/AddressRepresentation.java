package gr.aueb.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AddressRepresentation {
    public String street;
    public String number;
    public String zipCode; // Representing the zip code as a String

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
}

