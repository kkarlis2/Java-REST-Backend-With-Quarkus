package gr.aueb.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {

    private Visitor visitor,visitor1,visitor2,visitor3;
    private Account account;
    private Email email;
    private Reservation reservation;
    private TicketZone ticketZone;

    @BeforeEach
    public void setUpVisitor() {
        visitor = new Visitor();
        account = new Account();
        email = new Email();
        ticketZone =new TicketZone();
    }

    @Test
    public void testGetVisitor() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(), account.getUsername(), account.getPassword());
        reservation = new Reservation(visitor,ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);

        assertEquals("Vagg", visitor.getFirstName(), "First name is correct");
        assertEquals("Zygo", visitor.getLastName(), "Last name is correct");
        assertEquals("6934567898", visitor.getPhoneNumber(), "Phone number is correct");
        assertEquals("test@example.gr", visitor.getEmail(), "Email is correct");
        assertEquals("newuser", visitor.getUsername(), "Username is correct");
        assertEquals("pass123", visitor.getPassword(), "Password is correct");

    }

    @Test
    public void testSetVisitor() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                 account.getUsername(), account.getPassword());

        visitor.setFirstName("Vaggelis");
        visitor.setLastName("Zygokostas");
        visitor.setPhoneNumber("6978526978");

        assertEquals("Vaggelis", visitor.getFirstName(), "First name was set correctly");
        assertEquals("Zygokostas", visitor.getLastName(), "Last name was set correctly");
        assertEquals("6978526978", visitor.getPhoneNumber(), "Phone number was set correctly");
    }

    @Test
    public void testFirstNameExceptionNull() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        assertThrows(IllegalArgumentException.class, () -> new Visitor(null, "Zygo",
                "6934567898", email.getEmail(), account.getUsername(), account.getPassword()),
                "First name cannot be null");
    }

    @Test
    public void testFirstNameExceptionEmpty() {

        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        assertThrows(IllegalArgumentException.class, () -> new Visitor("", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword()), "First name cannot be empty");
    }

    @Test
    public void testLastNameExceptionNull() {

        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        assertThrows(IllegalArgumentException.class, () -> new Visitor("Vagg", null, "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword()), "Last name cannot be null");
    }

    @Test
    public void testLastNameExceptionEmpty() {

        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        assertThrows(IllegalArgumentException.class, () -> new Visitor("Vagg", "", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword()), "Last name cannot be empty");
    }

    @Test
    public void testPhoneNumberExceptionNull() {

        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        assertThrows(IllegalArgumentException.class, () -> new Visitor("Vagg", "Zygo", null, email.getEmail(),
                account.getUsername(), account.getPassword()), "Phone number cannot be null");
    }

    @Test
    public void testPhoneNumberExceptionEmpty() {

        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        assertThrows(IllegalArgumentException.class, () -> new Visitor("Vagg", "Zygo", "", email.getEmail(),
                account.getUsername(), account.getPassword()), "Last name cannot be empty");
    }

    @Test
    public void testSetFirstNameExceptionNull() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        assertThrows(IllegalArgumentException.class, () -> visitor.setFirstName(null),
                "First name cannot be set to null");
    }

    @Test
    public void testSetFirstNameExceptionEmpty() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        assertThrows(IllegalArgumentException.class, () -> visitor.setFirstName(""),
                "First name cannot be set empty");
    }

    @Test
    public void testSetLastNameExceptionNull() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        assertThrows(IllegalArgumentException.class, () -> visitor.setLastName(null),
                "Last name cannot be set to null");
    }

    @Test
    public void testSetLastNameExceptionEmpty() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        assertThrows(IllegalArgumentException.class, () -> visitor.setLastName(""),
                "Last name cannot be set empty");
    }

    @Test
    public void testSetPhoneNumberExceptionNull() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        assertThrows(IllegalArgumentException.class, () -> visitor.setPhoneNumber(null),
                "Phone number cannot be set to null");
    }

    @Test
    public void testSetPhoneNumberExceptionEmpty() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        assertThrows(IllegalArgumentException.class, () -> visitor.setPhoneNumber(""),
                "Phone number cannot be set empty");
    }

    @Test
    public void testAddReservation() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        reservation = new Reservation(visitor,ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);


        visitor.addReservation(reservation);

        Set<Reservation> reservations = visitor.getReservations();
        assertEquals(1, reservations.size());
        assertTrue(reservations.contains(reservation));

        assertEquals(visitor, reservation.getVisitor());
    }

    @Test
    public void testRemoveReservation() {
        email = new Email("test@example.gr");
        account = new Account("newuser", "pass123");
        visitor = new Visitor("Vagg", "Zygo", "6934567898", email.getEmail(),
                account.getUsername(), account.getPassword());
        reservation = new Reservation(visitor,ticketZone, 2, LocalDateTime.now(), ReservationStatus.PENDING,null);


        visitor.addReservation(reservation);
        visitor.removeReservation(reservation);

        Set<Reservation> reservations = visitor.getReservations();
        assertTrue(reservations.isEmpty());

    }

    @Test
    public void testAddNullReservation() {
        assertThrows(IllegalArgumentException.class, () -> visitor.addReservation(null));
    }

    @Test
    public void testRemoveNullReservation() {
        assertThrows(IllegalArgumentException.class, () -> visitor.removeReservation(null));
    }

    @Test
    public void testEqualandHashcode(){

        visitor1 = new Visitor("KOSTAS", "KARLIS", "1234567890", "konkarlis@aueb.gr", "konkarlis", "password123");
        visitor2 = new Visitor("KOSTAS", "KARLIS", "1234567890", "konkarlis@aueb.gr", "konkarlis", "password123");
        visitor3 = new Visitor("TASOS", "KOURSOS", "0987654321", "tasoskour@aueb.gr", "tasoskour", "password321");
        Visitor visitor4 = new Visitor("KOSTAS","KARLIS","1234567890","konstantinoskarlis@aueb.gr","konkarlis","password123");
        Visitor visitor5 = new Visitor("KOSTAS", "KARLIS", "1234567890", "konkarlis@aueb.gr", "konkarlis2", "password123");



        assertTrue(visitor1.equals(visitor1));
        assertTrue(visitor1.equals(visitor2));
        assertFalse(visitor1.equals(visitor3));
        assertFalse(visitor1.equals(null));
        assertFalse(visitor2.equals(""));

        assertFalse(visitor1.equals(visitor4));
        assertFalse(visitor1.equals(visitor5));

        assertEquals(visitor1.hashCode(), visitor2.hashCode());
        assertNotEquals(visitor1.hashCode(), visitor3.hashCode());
    }

}

