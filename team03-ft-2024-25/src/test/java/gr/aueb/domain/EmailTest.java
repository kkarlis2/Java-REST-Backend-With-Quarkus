package gr.aueb.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {

    private Email email;

    @BeforeEach
    public void setUpEmail(){
        email = new Email();
    }

    @Test
    public void testGetEmail(){
        email = new Email("test@example.com");
        assertEquals("test@example.com", email.getEmail(), "Email is correct");
    }

    @Test
    public void testSetEmail(){
        email = new Email("test@example.com");
        email.setEmail("vaggelisz@example.com");
        assertEquals("vaggelisz@example.com", email.getEmail(), "Email was set correctly");
    }

    @Test
    public void testConstructorExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> new Email(null), "Email cannot be null");
    }

    @Test
    public void testConstructorExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> new Email(""), "Email cannot be empty");
    }

    @Test
    public void testEmailSetExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> email.setEmail(null), "Email cannot be set to null");
    }

    @Test
    public void testEmailSetExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> email.setEmail(""), "Email cannot be set empty");
    }
}
