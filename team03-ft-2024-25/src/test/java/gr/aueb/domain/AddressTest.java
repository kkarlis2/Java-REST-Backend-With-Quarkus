package gr.aueb.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressTest {

    private Address address;
    private ZipCode zipCode;

    @BeforeEach
    public void setUpAddress(){
        address = new Address();
        zipCode = new ZipCode("17456");
    }

    @Test
    public void testGetAddress() {
        address = new Address("Patision", "10", zipCode);
        assertEquals("Patision", address.getStreet(), "Street is correct");
        assertEquals("10", address.getNumber(), "Number correct");
        assertEquals(zipCode, address.getZipCode(), "ZipCode is correct");
    }

    @Test
    public void testSetStreet() {
        address = new Address("Old Street", "B2", zipCode);
        address.setStreet("New Street");
        assertEquals("New Street", address.getStreet(), "Street was set correctly");
    }

    @Test
    public void testSetNumber() {
        address = new Address("Patision", "10", zipCode);
        address.setNumber("B2");
        assertEquals("B2", address.getNumber(), "Number was set correctly");
    }

    @Test
    public void testSetZipCode() {
        address = new Address("Patision", "A1", zipCode);
        ZipCode newZipCode = new ZipCode("54321"); // Replace with appropriate ZipCode initialization
        address.setZipCode(newZipCode);
        assertEquals(newZipCode, address.getZipCode(), "Zip code was set correctly");
    }

    @Test
    public void testStreetExceptionNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address(null, "A1", zipCode),
                "Street cannot be null");
    }

    @Test
    public void testStreetExceptionEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Address("", "8", zipCode),
                "Street cannot be empty");
    }

    @Test
    public void testNumberExceptionNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address("Ermou", null, zipCode),
                "Number cannot be null");
    }

    @Test
    public void testNumberThrowsExceptionEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Address("Ermou", "", zipCode),
                "Number cannot be empty");
    }

    @Test
    public void testZipCodeExceptionNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address("Patision", "15", null),
                "Zip code cannot be null");
    }

    @Test
    public void testSetStreetExceptionNull() {
        address = new Address("Ermou", "A1", zipCode);
        assertThrows(IllegalArgumentException.class, () -> address.setStreet(null),
                "Street cannot be set to null");
    }

    @Test
    public void testSetStreetExceptionEmpty() {
        address = new Address("Patision", "B2", zipCode);
        assertThrows(IllegalArgumentException.class, () -> address.setStreet(""),
                "Street cannot be set to empty");
    }

    @Test
    public void testSetNumberExceptionNull() {
        address = new Address("Patision", "9", zipCode);
        assertThrows(IllegalArgumentException.class, () -> address.setNumber(null),
                "Number cannot be set to null");
    }

    @Test
    public void testSetNumberExceptionEmpty() {
        address = new Address("Ermou", "5B", zipCode);
        assertThrows(IllegalArgumentException.class, () -> address.setNumber(""),
                "Number cannot be set to empty");
    }

    @Test
    public void testSetZipCodeExceptionNull() {
        address = new Address("Vouliagmenis Avenue", "72", zipCode);
        assertThrows(IllegalArgumentException.class, () -> address.setZipCode(null),
                "Zip code cannot be set to null");
    }

}
