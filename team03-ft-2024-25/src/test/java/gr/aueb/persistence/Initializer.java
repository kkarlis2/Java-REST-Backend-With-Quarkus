
package gr.aueb.persistence;

import gr.aueb.domain.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequestScoped
public class Initializer {

    @Inject
    VisitorRepository visitorRepository;

    @Inject
    OrganizerRepository organizerRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    TicketZoneRepository ticketZoneRepository;

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    ZipCodeRepository zipCodeRepository;

    @Transactional
    public void eraseData() {
        // The order matters due to foreign key constraints
        reservationRepository.deleteAllWithTransactions();
        ticketZoneRepository.deleteAll();
        eventRepository.deleteAll();
        organizerRepository.deleteAll();
        visitorRepository.deleteAll();
        zipCodeRepository.deleteAll();
    }

    @Transactional
    public void prepareData() {
        eraseData();

        // Create visitors
        Visitor v1 = new Visitor("kostas", "karlis", "6985486420", "karlis@aueb.gr",
                "kkarlis", "secure");
        Visitor v2 = new Visitor("vaggelis", "zygokostas", "6985486421", "zygo@aueb.gr",
                "vagzyg", "test");
        Visitor v3 = new Visitor("Tasos", "Koursos", "6985486422", "tasoskour@aueb.gr",
                "tasoskour", "12345");

        // Persist ZipCodes first
        ZipCode z1 = new ZipCode("19100");
        ZipCode z2 = new ZipCode("11471");
        zipCodeRepository.persist(z1);
        zipCodeRepository.persist(z2);

        // Create and persist organizers
        Organizer o1 = new Organizer("123456789", "TasosEvents", "210467829", "Tasos",
                "test", "tasos@aueb.gr", "lefkados", "3", z1);
        Organizer o2 = new Organizer("987654321", "VaggelisEvents", "210467830", "Vag",
                "pass123", "vag@aueb.gr", "panormou", "12", z2);

        // Persist visitors and organizers
        visitorRepository.persist(v1);
        visitorRepository.persist(v2);
        visitorRepository.persist(v3);

        organizerRepository.persist(o1);
        organizerRepository.persist(o2);

        // Create and persist events
        Event e1 = new Event("posidonio", LocalDate.now().plusDays(10), LocalTime.of(18, 30),
                "MetropolitanEXPO", "TELEIO EVENT", EventType.CONCERT, o1);
        Event e2 = new Event("shakespeare", LocalDate.now().plusDays(15), LocalTime.of(20, 0),
                "National Theater", "A classic theater play", EventType.THEATER, o2);
        Event e3 = new Event("tech_conference", LocalDate.now().plusDays(5), LocalTime.of(9, 0),
                "Athens Conference Center", "Technology and innovation", EventType.SEMINAR, o1);

        eventRepository.persist(e1);
        eventRepository.persist(e2);
        eventRepository.persist(e3);

        // Create and persist ticket zones
        TicketZone t1 = new TicketZone(20.00, Category.SIMPLE,100,100,e1);
        TicketZone t2 = new TicketZone(50.00, Category.VIP,20, 20, e1);
        TicketZone t3 = new TicketZone(15.00, Category.SIMPLE,150, 150, e2);
        TicketZone t4 = new TicketZone(30.00, Category.VIP,50, 50, e2);
        TicketZone t5 = new TicketZone(10.00, Category.SIMPLE,200, 200, e3);
        TicketZone t6 = new TicketZone(25.00, Category.ARENA,30, 30, e3);

        ticketZoneRepository.persist(t1);
        ticketZoneRepository.persist(t2);
        ticketZoneRepository.persist(t3);
        ticketZoneRepository.persist(t4);
        ticketZoneRepository.persist(t5);
        ticketZoneRepository.persist(t6);

        // Create and persist reservations with transactions
        Reservation r1 = new Reservation(v1, t1, 2, LocalDateTime.now(), ReservationStatus.PENDING, null);
        Reservation r2 = new Reservation(v2, t2, 1, LocalDateTime.now(), ReservationStatus.PENDING, DiscountCat.STUDENT);
        Reservation r3 = new Reservation(v3, t3, 4, LocalDateTime.now(), ReservationStatus.PENDING, null);
        Reservation r4 = new Reservation(v1, t5, 3, LocalDateTime.now(), ReservationStatus.PENDING, DiscountCat.PWD);

        // Create and add transactions
        Payment p1 = new Payment(r1, LocalDateTime.now(), TransactionStatus.SUCCESS, "1234567890987654",
                "KARLIS KOSTAS", LocalDateTime.now().plusYears(5), 456);
        Payment p2 = new Payment(r2, LocalDateTime.now(), TransactionStatus.SUCCESS, "9876543210123456",
                "ZYGOKOSTAS VAGGELIS", LocalDateTime.now().plusYears(3), 789);
        Refund re1 = new Refund(r1, LocalDateTime.now(), TransactionStatus.SUCCESS, 5.00);

        r1.addTransaction(p1);
        r1.addTransaction(re1);
        r2.addTransaction(p2);

        // Persist reservations (transactions will be persisted via cascade)
        reservationRepository.persist(r1);
        reservationRepository.persist(r2);
        reservationRepository.persist(r3);
        reservationRepository.persist(r4);
    }
}
//package gr.aueb.persistence;
//
//import gr.aueb.domain.*;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityTransaction;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
//public class Initializer {
//
//    private EntityManager em;
//
//    public Initializer() {
//
//        em = JPAUtil.getCurrentEntityManager();
//    }
//
//    private void eraseData() {
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        em.createNativeQuery("delete from refunds").executeUpdate();
//        em.createNativeQuery("delete from payments").executeUpdate();
//        em.createNativeQuery("delete from transactions").executeUpdate();
//        em.createNativeQuery("delete from reservations").executeUpdate();
//        em.createNativeQuery("delete from TicketZone").executeUpdate();
//        em.createNativeQuery("delete from Event").executeUpdate();
//        em.createNativeQuery("delete from organizers").executeUpdate();
//        em.createNativeQuery("delete from visitors").executeUpdate();
//        em.createNativeQuery("delete from zip_code").executeUpdate();
//
//
//
//        tx.commit();
//    }
//
//    public void prepareData() {
//
//        eraseData();
//
//
//        Visitor v1 = new Visitor("kostas", "karlis", "6985486420", "karlis@aueb.gr",
//                "kkarlis", "secure");
//        Visitor v2 = new Visitor("vaggelis", "zygokostas", "6985486421", "zygo@aueb.gr",
//                "vagzyg", "test");
//        Visitor v3 = new Visitor("Tasos", "Koursos", "6985486422", "tasoskour@aueb.gr",
//                "tasoskour", "12345");
//
//
//        Organizer o1 = new Organizer("123456789", "TasosEvents", "210467829", "Tasos",
//                "test", "tasos@aueb.gr", "lefkados", "3", new ZipCode("19100"));
//        Organizer o2 = new Organizer("987654321", "VaggelisEvents", "210467830", "Vag",
//                "pass123", "vag@aueb.gr", "panormou", "12", new ZipCode("11471"));
//
//
//        Event e1 = new Event("posidonio", LocalDate.now().plusDays(10), LocalTime.of(18, 30),
//                "MetropolitanEXPO", "TELEIO EVENT", EventType.CONCERT, o1);
//        Event e2 = new Event("shakespeare", LocalDate.now().plusDays(15), LocalTime.of(20, 0),
//                "National Theater", "A classic theater play", EventType.THEATER, o2);
//        Event e3 = new Event("tech_conference", LocalDate.now().plusDays(5), LocalTime.of(9, 0),
//                "Athens Conference Center", "Technology and innovation", EventType.SEMINAR, o1);
//
//
//        TicketZone t1 = new TicketZone(20.00, Category.SIMPLE, 100, e1);
//        TicketZone t2 = new TicketZone(50.00, Category.VIP, 20, e1);
//
//        TicketZone t3 = new TicketZone(15.00, Category.SIMPLE, 150, e2);
//        TicketZone t4 = new TicketZone(30.00, Category.VIP, 50, e2);
//
//        TicketZone t5 = new TicketZone(10.00, Category.SIMPLE, 200, e3);
//        TicketZone t6 = new TicketZone(25.00, Category.ARENA, 30, e3);
//
//
//        Reservation r1 = new Reservation(v1, t1, 2, LocalDateTime.now(), ReservationStatus.PENDING, null);
//        Reservation r2 = new Reservation(v2, t2, 1, LocalDateTime.now(), ReservationStatus.PENDING, DiscountCat.STUDENT);
//        Reservation r3 = new Reservation(v3, t3, 4, LocalDateTime.now(), ReservationStatus.PENDING, null);
//        Reservation r4 = new Reservation(v1, t5, 3, LocalDateTime.now(), ReservationStatus.PENDING, DiscountCat.PWD);
//
//
//        Payment p1 = new Payment(r1, LocalDateTime.now(), TransactionStatus.SUCCESS, "1234567890987654",
//                "KARLIS KOSTAS", LocalDateTime.now().plusYears(5), 456);
//        Payment p2 = new Payment(r2, LocalDateTime.now(), TransactionStatus.SUCCESS, "9876543210123456",
//                "ZYGOKOSTAS VAGGELIS", LocalDateTime.now().plusYears(3), 789);
//
//        Refund re1 = new Refund(r1, LocalDateTime.now(), TransactionStatus.SUCCESS, 5.00);
//
//
//
//        r1.addTransaction(p1);
//        r1.addTransaction(re1);
//
//        r2.addTransaction(p2);
//
//
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//
//        em.persist(v1);
//        em.persist(v2);
//        em.persist(v3);
//
//        em.persist(o1);
//        em.persist(o2);
//
//        em.persist(e1);
//        em.persist(e2);
//        em.persist(e3);
//
//        em.persist(t1);
//        em.persist(t2);
//        em.persist(t3);
//        em.persist(t4);
//        em.persist(t5);
//        em.persist(t6);
//
//        em.persist(r1);
//        em.persist(r2);
//        em.persist(r3);
//        em.persist(r4);
//
//        tx.commit();
//
//        em.close();
//    }
//
//}
