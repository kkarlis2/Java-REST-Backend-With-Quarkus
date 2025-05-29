package gr.aueb.domain;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="Event",uniqueConstraints = {@UniqueConstraint(columnNames = {"date","time","location"})})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Column(name="title", length = 200, nullable = false)
    private String title;


    @Column(name = "date", nullable = false)
    private LocalDate date;


    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "location", length = 200,nullable = false)
    private String location;

    @Column(name="description", length = 800)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type",nullable = false)
    private EventType eventType;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private Set<TicketZone> ticketZones = new HashSet<>();

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="organizer",nullable=false)
    private Organizer organizer;


    public Event(){}

    public Event(String title, LocalDate date, LocalTime time, String location, String description, EventType eventType,Organizer organizer){
        setTitle(title);
        setDate(date);
        setTime(time);
        setLocation(location);
        setDescription(description);
        setEventType(eventType);
        setOrganizer(organizer);

    }

    public Integer getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title==null || title.trim().isEmpty()){
            throw new IllegalArgumentException("Title can't be null or empty!Enter a valid one!");
        }
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date==null){
            throw new IllegalArgumentException("Date can't be null!");
        }
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        if(time==null){
            throw new IllegalArgumentException("Time can't be null!");
        }
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if(location ==null || location.trim().isEmpty()){
            throw new IllegalArgumentException("Location can't be null or empty!Enter a valid one!");
        }
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description==null||description.trim().isEmpty()){
            throw new IllegalArgumentException("Description can't be null or empty!Enter a valid one!");
        }
        this.description = description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        if(eventType==null){
            throw new IllegalArgumentException("Event type can't be null!Pick a valid one!");
        }
        this.eventType = eventType;
    }

    public Set<TicketZone> getTicketZones() {
        return ticketZones;
    }

    public void setTicketZones(Set<TicketZone> ticketZones) {
        if(ticketZones==null){
            throw new IllegalArgumentException("Ticket zones can't be null!Pick a valid one!");
        }
        this.ticketZones = ticketZones;
    }

    public void addTicketZone(TicketZone ticketZone) {
        ticketZones.add(ticketZone);
        ticketZone.setEvent(this);
    }

    public Organizer getOrganizer(){
        return organizer;
    }

    public void setOrganizer(Organizer organizer){
        if (organizer == null) {
            throw new IllegalArgumentException("Organizer can't be null!Enter a valid organizer!");
        }
        this.organizer = organizer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return  Objects.equals(date, event.date) &&
                Objects.equals(time, event.time) &&
                Objects.equals(location, event.location) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, location);
    }


}
