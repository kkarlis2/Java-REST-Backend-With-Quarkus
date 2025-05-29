package gr.aueb.representation;


import gr.aueb.domain.Account;
import gr.aueb.representation.AccountMapper;
import gr.aueb.representation.AccountRepresentation;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AccountMapperTest {

    @jakarta.inject.Inject
    AccountMapper accountMapper;

    @Test
    public void testToEntity() {
        // Δημιουργία AccountRepresentation
        AccountRepresentation representation = new AccountRepresentation();
        representation.username = "testuser";
        representation.password = "testpass";

        // Χρήση του mapper για μετατροπή σε Account entity
        Account account = accountMapper.toEntity(representation);

        // Έλεγχοι
        assertNotNull(account);
        assertEquals(representation.username, account.getUsername());
        assertEquals(representation.password, account.getPassword());
    }

    @Test
    public void testToRepresentation() {
        // Δημιουργία Account entity
        Account account = new Account();
        account.setUsername("testuser");
        account.setPassword("testpass");

        // Χρήση του mapper για μετατροπή σε String (username)
        String username = accountMapper.toRepresentation(account);

        // Έλεγχοι
        assertNotNull(username);
        assertEquals(account.getUsername(), username);
    }

    @Test
    public void testToRepresentationWithNullAccount() {
        // Χρήση του mapper με null Account
        String username = accountMapper.toRepresentation(null);

        // Έλεγχοι
        assertNull(username);
    }

    @Test
    public void testAccountRepresentationSetters() {
        // Δημιουργία νέου AccountRepresentation
        AccountRepresentation representation = new AccountRepresentation();

        // Έλεγχος setter για username
        representation.setUsername("newUsername");
        assertEquals("newUsername", representation.getUsername());

        // Έλεγχος setter για password
        representation.setPassword("newPassword");
        assertEquals("newPassword", representation.getPassword());
    }
}
