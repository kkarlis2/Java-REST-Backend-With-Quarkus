package gr.aueb.representation;

import gr.aueb.domain.EventType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@RegisterForReflection
public class EventRepresentation {
    public Integer id;
    public String title;
    public LocalDate date;
    public LocalTime time;
    public String location;
    public String description;
    public EventType eventType;
    public Set<TicketZoneRepresentation> ticketZones;
    public Integer organizerId;

    public EventRepresentation() {}
}