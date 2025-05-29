package gr.aueb.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZipCodeTest {

    private ZipCode zipCode;

    @BeforeEach
    public void setUpZipCode(){
        zipCode = new ZipCode();
    }

    @Test
    public void testGetZipCode(){
        zipCode.setZipCode("17456");
        assertEquals("17456", zipCode.getZipCode(), "Zip code is correct");
    }

    @Test
    public void testSetZipCode(){
        zipCode = new ZipCode("17456");
        zipCode.setZipCode("65471");
        assertEquals("65471", zipCode.getZipCode(), "Zip code was set correctly");
    }

    @Test
    public void testZipCodeExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> new ZipCode(null), "ZipCode cannot be null");
    }

    @Test
    public void testZipCodeExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> new ZipCode(""), "ZipCode cannot be empty");
    }

    @Test
    public void testSetZipCodeExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> zipCode.setZipCode(null), "ZipCode cannot be set to null");
    }

    @Test
    public void testSetZipCodeExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> zipCode.setZipCode(""), "ZipCode cannot be set to empty");
    }
}
