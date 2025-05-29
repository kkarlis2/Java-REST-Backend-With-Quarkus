package gr.aueb.domain;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    private Account account;

    @BeforeEach
    public void setUpAccount(){
        account = new Account();
    }

    @Test
    public void testGetAccount(){
        account = new Account("test", "test_pass");
        assertEquals("test", account.getUsername(), "Username is correct");
        assertEquals("test_pass", account.getPassword(), "Password is correct");
    }

    @Test
    public void testSetAccount(){
        account.setUsername("test");
        account.setPassword("test_pass");
        assertEquals("test", account.getUsername(), "Username is correct");
        assertEquals("test_pass", account.getPassword(), "Password is correct");
    }

    @Test
    public void testUsernameExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> new Account(null, "password123"),
                "Username cannot be null");
    }

    @Test
    public void testUsernameExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> new Account("", "password123"),
                "Username cannot be empty");
    }

    @Test
    public void testPasswordExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> new Account("newuser", null),
                "Password cannot be null");
    }

    @Test
    public void testPasswordExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> new Account("newuser", ""),
                "Password cannot be empty");
    }

    @Test
    public void testSetUsernameExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> account.setUsername(null), "Username cannot be set to null");
    }

    @Test
    public void testSetUsernameExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> account.setUsername(""), "Username cannot be set to empty");
    }

    @Test
    public void testSetPasswordExceptionNull(){
        assertThrows(IllegalArgumentException.class, () -> account.setPassword(null), "Password cannot be set to null");
    }

    @Test
    public void testSetPasswordExceptionEmpty(){
        assertThrows(IllegalArgumentException.class, () -> account.setPassword(""), "Password cannot be set to empty");
    }
}
