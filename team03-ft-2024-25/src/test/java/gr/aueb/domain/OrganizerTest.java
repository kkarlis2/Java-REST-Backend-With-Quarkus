package gr.aueb.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizerTest {

    private Organizer organizer, organizer1;
    private Address address;
    private ZipCode zipCode;

    @BeforeEach
    public void setUpOrganizer(){
        zipCode = new ZipCode("17456");
        address= new Address("Patision", "10", zipCode);
        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123",
                "test@example.gr", address.getStreet(), address.getNumber(), address.getZipCode());

    }

    @Test
    public void testGetOrganizer(){

        assertEquals("8564123089", organizer.getTaxId(), "Tax id is correct");
        assertEquals("TestBrand", organizer.getBrandName(), "Brand name is correct");
        assertEquals("6934567898", organizer.getPhoneNumber(), "Phone number is correct");
        assertEquals("userTest", organizer.getUserName(), "Username is correct");
        assertEquals("pass123", organizer.getPassword(), "Password is correct");
        assertEquals("test@example.gr", organizer.getEmail(), "Email is correct");
        assertEquals("Patision", organizer.getStreet(), "Street is correct");
        assertEquals("10", organizer.getNumber(), "Number is correct");
        assertEquals("17456", organizer.getZipCode(), "Zip code is correct");
    }

    @Test
    public void testSetOrganizer(){

        organizer.setTaxId("0014568520");
        organizer.setBrandName("BrandTest");
        organizer.setPhoneNumber("6978901806");

        assertEquals("0014568520", organizer.getTaxId(), "Tax id is correct");
        assertEquals("BrandTest", organizer.getBrandName(), "Brand name is correct");
        assertEquals("6978901806", organizer.getPhoneNumber(), "Phone number is correct");
    }

    @Test
    public void testTaxIdExceptionNull(){


        assertThrows(IllegalArgumentException.class, () -> organizer = new Organizer(null, "BrandTest",
                "6934567898", "userTest", "pass123", "test@example.gr",
                address.getStreet(), address.getNumber(), address.getZipCode()), "Tax id cannot be null");


    }

    @Test
    public void testTaxIdExceptionEmpty(){

        assertThrows(IllegalArgumentException.class, () -> organizer = new Organizer("", "BrandTest",
                "6934567898", "userTest", "pass123", "test@example.gr",
                address.getStreet(), address.getNumber(), address.getZipCode()));
    }

    @Test
    public void testBrandNameExceptionNull(){

        assertThrows(IllegalArgumentException.class, () -> organizer = new Organizer("8564123089", null,
                "6934567898", "userTest", "pass123", "test@example.gr",
                address.getStreet(), address.getNumber(), address.getZipCode()), "Brand name cannot be null");
    }

    @Test
    public void testBrandNameExceptionEmpty(){

        assertThrows(IllegalArgumentException.class, () -> organizer = new Organizer("8564123089", "",
                "6934567898", "userTest", "pass123", "test@example.gr",
                address.getStreet(), address.getNumber(), address.getZipCode()), "Brand name cannot be empty");
    }

    @Test
    public void testPhoneNumberExceptionNull(){

        assertThrows(IllegalArgumentException.class, () -> organizer = new Organizer("8564123089", "BrandTest",
                null, "userTest", "pass123", "test@example.gr",
                address.getStreet(), address.getNumber(), address.getZipCode()), "Phone number cannot be null");
    }

    @Test
    public void testPhoneNumberExceptionEmpty(){

        assertThrows(IllegalArgumentException.class, () -> organizer = new Organizer("8564123089", "BrandTest",
                "", "userTest", "pass123", "test@example.gr",
                address.getStreet(), address.getNumber(), address.getZipCode()), "Phone number cannot be empty");
    }

    @Test
    public void testSetTaxIdExceptionNull(){

        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertThrows(IllegalArgumentException.class, () -> organizer.setTaxId(null), "Tax id cannot be set to null");
    }

    @Test
    public void testSetTaxIdExceptionEmpty(){

        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertThrows(IllegalArgumentException.class, () -> organizer.setTaxId(null), "Tax id cannot be set to empty");
    }

    @Test
    public void testSetBrandNameExceptionNull(){

        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertThrows(IllegalArgumentException.class, () -> organizer.setBrandName(null),
                "Brand name cannot be set to null");
    }

    @Test
    public void testSetBrandNameExceptionEmpty(){

        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertThrows(IllegalArgumentException.class, () -> organizer.setBrandName(""),
                "Brand name cannot be set empty");
    }

    @Test
    public void testSetPhoneNumberExceptionNull(){

        organizer = new Organizer("8564123089", "TestBrand", "6934567898","userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertThrows(IllegalArgumentException.class, () -> organizer.setPhoneNumber(null),
                "Phone number cannot be set to null");
    }

    @Test
    public void testSetPhoneNumberExceptionEmpty(){

        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertThrows(IllegalArgumentException.class, () -> organizer.setPhoneNumber(""),
                "Phone number cannot be set empty");
    }


    @Test
    public void testAddEvents() {
        Event Vissi = new Event("Anna Vissi Concert", java.time.LocalDate.now().plusDays(1),
                java.time.LocalTime.now(), "Kallimarmaro Arena", "Live show", EventType.CONCERT, organizer);

        organizer.addEvent(Vissi);

        assertEquals(1, organizer.getEvents().size());
        assertTrue(organizer.getEvents().contains(Vissi));
        assertEquals(organizer, Vissi.getOrganizer());

        assertThrows(IllegalArgumentException.class,()->organizer.addEvent(null));
    }

    @Test
    public void testRemoveEvent(){
        Event theaterEvent = new Event("Amfitrywn", java.time.LocalDate.now().plusDays(2),
                java.time.LocalTime.now(), "Epidavros", "Drama theater", EventType.THEATER, organizer);

        organizer.addEvent(theaterEvent);
        assertTrue(organizer.getEvents().contains(theaterEvent));

        organizer.removeEvent(theaterEvent);
        assertFalse(organizer.getEvents().contains(theaterEvent));

        assertThrows(IllegalArgumentException.class,()->organizer.removeEvent(null));
    }

    @Test
    public void testEqualsAndHashCode(){
        organizer = new Organizer("8564123089", "TestBrand", "6934567898", "userTest", "pass123", "test@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());
        organizer1 = new Organizer("1234554332", "Gucci", "6934758698", "Test", "123pass", "test1@example.gr", address.getStreet(), address.getNumber(),
                address.getZipCode());

        assertTrue(organizer.equals(organizer), "Organizer object is equal to itself");
        assertFalse(organizer.equals(organizer1), "Organizer and organizer1 are different");
        assertFalse(organizer.equals(null), "Organizer is not null");
        assertFalse(organizer.equals(""), "Organizer is not empty");
        assertEquals(organizer.hashCode(), organizer.hashCode());
        assertNotEquals(organizer.hashCode(), organizer1.hashCode());
    }
}
