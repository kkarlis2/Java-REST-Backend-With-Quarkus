//package gr.aueb.persistence;
//
//import jakarta.inject.Inject;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Scanner;
//
//public class JPATest {
//    @Inject
//    EntityManager em;
//
//    @Transactional
//    @BeforeEach
//    public void initDb() {
//        // Φόρτωση του import.sql
//        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("import.sql");
//        String sql = convertStreamToString(in);
//        try {
//            in.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Εκτέλεση καθαρισμού πινάκων με τα σωστά ονόματα
//        em.createNativeQuery("DELETE FROM refunds").executeUpdate();
//        em.createNativeQuery("DELETE FROM payments").executeUpdate();
//        em.createNativeQuery("DELETE FROM transactions").executeUpdate();
//        em.createNativeQuery("DELETE FROM reservations").executeUpdate();
//        em.createNativeQuery("DELETE FROM TicketZone").executeUpdate(); // Διορθωμένο όνομα πίνακα
//        em.createNativeQuery("DELETE FROM Event").executeUpdate();
//        em.createNativeQuery("DELETE FROM organizers").executeUpdate();
//        em.createNativeQuery("DELETE FROM visitors").executeUpdate();
//        em.createNativeQuery("DELETE FROM zip_code").executeUpdate();
//
//        // Εκτέλεση των INSERT statements
//        for (String statement : sql.split(";")) {
//            if (!statement.trim().isEmpty() && statement.trim().toUpperCase().startsWith("INSERT")) {
//                em.createNativeQuery(statement.trim()).executeUpdate();
//            }
//        }
//    }
//
//    private String convertStreamToString(InputStream in) {
//        @SuppressWarnings("resource")
//        Scanner s = new Scanner(in, "UTF-8").useDelimiter("\\A");
//        return s.hasNext() ? s.next() : "";
//    }
//}