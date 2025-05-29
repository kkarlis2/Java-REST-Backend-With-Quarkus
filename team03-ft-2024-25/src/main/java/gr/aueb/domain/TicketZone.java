package gr.aueb.domain;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TicketZone")
public class TicketZone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cost", length = 10, nullable = false)
    private Double cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;  // Track the total capacity

    @OneToMany(mappedBy = "ticketZone", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public TicketZone() {}

    public TicketZone(Double cost, Category category, Integer availableSeats, Integer totalCapacity, Event event) {
        setCost(cost);
        setCategory(category);
        setAvailableSeats(availableSeats);
        setTotalCapacity(totalCapacity);  // Initialize the total capacity
        setEvent(event);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        if (cost == null) {
            throw new IllegalArgumentException("Cost can't be null! Enter a valid one!");
        }
        this.cost = cost;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category can't be null! Pick a valid one!");
        }
        this.category = category;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        if (availableSeats == null || availableSeats < 0) {
            throw new IllegalArgumentException("Available seats can't be null or negative! Enter a valid number!");
        }
        this.availableSeats = availableSeats;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        if (totalCapacity == null || totalCapacity < 0) {
            throw new IllegalArgumentException("Total capacity can't be null or negative! Enter a valid number!");
        }
        this.totalCapacity = totalCapacity;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event can't be null! Pick a valid one!");
        }
        this.event = event;
    }

    // Method to calculate the reserved seats based on the total capacity and available seats
    public int getReservedSeats() {
        return totalCapacity - availableSeats;
    }
}
