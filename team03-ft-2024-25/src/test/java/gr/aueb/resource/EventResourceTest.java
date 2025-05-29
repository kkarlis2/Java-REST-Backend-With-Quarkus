package gr.aueb.resource;

import gr.aueb.domain.Event;
import gr.aueb.domain.EventType;
import gr.aueb.domain.Organizer;
import gr.aueb.domain.ZipCode;
import gr.aueb.persistence.EventRepository;
import gr.aueb.representation.EventMapper;
import gr.aueb.representation.EventRepresentation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class EventResourceTest {

    @InjectMock
    EventRepository eventRepository;

    @Inject
    EventResource eventResource;


    @InjectMock(convertScopes = true)
    EventMapper eventMapper;

    private Event testEvent;
    private EventRepresentation testEventRep;
    private LocalDate testDate;
    private LocalTime testTime;
    private Organizer testOrganizer;

    public EventResourceTest() {}

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now();
        testTime = LocalTime.of(20, 0);

        // Setup test Organizer
        testOrganizer = new Organizer(
                "111111111",         // taxId
                "Test Brand",        // brandName
                "6988888888",        // phoneNumber
                "tesuser",          // username
                "password123",       // password
                "event@resource.com",     // email
                "TestE Street",       // street
                "123",              // number
                new ZipCode("11143") // zipCode
        );
        testOrganizer.setId(1);

        // Setup test Event
        testEvent = new Event(
                "Test Event",
                testDate,
                testTime,
                "Test Location",
                "Test Description",
                EventType.CONCERT,
                testOrganizer  // προσθήκη του organizer
        );
        testEvent.setId(1);
    }



    @Test
    void getAllEvents_ReturnsEventsList() {
        when(eventRepository.listAll()).thenReturn(Arrays.asList(testEvent));
        when(eventMapper.toRepresentationList(any())).thenReturn(Arrays.asList(new EventRepresentation()));

        Response response = eventResource.getAllEvents();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getEventById_WhenExists_ReturnsEvent() {
        // Arrange
        EventRepresentation expectedRepresentation = new EventRepresentation();
        expectedRepresentation.title = "Test Event";
        expectedRepresentation.date = testDate;
        expectedRepresentation.time = testTime;
        expectedRepresentation.location = "Test Location";
        expectedRepresentation.description = "Test Description";
        expectedRepresentation.eventType = EventType.CONCERT;
        expectedRepresentation.organizerId = testOrganizer.getId();

        when(eventRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toRepresentation(testEvent)).thenReturn(expectedRepresentation);

        // Act
        Response response = eventResource.getEventById(1);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(expectedRepresentation, response.getEntity());
    }

    @Test
    void getEventById_WhenNotExists_Returns404() {
        when(eventRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = eventResource.getEventById(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void searchEvents_WithValidTitle_ReturnsFilteredEvents() {
        String searchTitle = "Test";
        when(eventRepository.findByTitleContaining(searchTitle)).thenReturn(Arrays.asList(testEvent));

        Response response = eventResource.searchEvents(searchTitle);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getEventsByDate_WithValidDate_ReturnsEvents() {
        String dateStr = testDate.toString();
        when(eventRepository.findByDate(testDate)).thenReturn(Arrays.asList(testEvent));

        Response response = eventResource.getEventsByDate(dateStr);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void getEventsByDate_WithInvalidDate_ReturnsBadRequest() {
        Response response = eventResource.getEventsByDate("invalid-date");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void getUpcomingEvents_ReturnsEvents() {
        when(eventRepository.findUpcomingEvents()).thenReturn(Arrays.asList(testEvent));

        Response response = eventResource.getUpcomingEvents();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void deleteEvent_WhenExists_ReturnsNoContent() {
        when(eventRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEvent));

        Response response = eventResource.deleteEvent(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(eventRepository).delete(testEvent);
    }

    @Test
    void deleteEvent_WhenNotExists_Returns404() {
        when(eventRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        Response response = eventResource.deleteEvent(999);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(eventRepository, never()).delete(any(Event.class));
    }
    @Test
    void createEvent_WithInvalidData_ReturnsBadRequest() {
        // Arrange
        EventRepresentation eventRep = new EventRepresentation();
        // Δεν θέτουμε κανένα πεδίο για να προκαλέσουμε IllegalArgumentException

        when(eventMapper.toEntity(eventRep)).thenThrow(new IllegalArgumentException("Missing required fields"));

        // Act
        Response response = eventResource.createEvent(eventRep);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(eventRepository, never()).persist(any(Event.class));
    }

    @Test
    void updateEvent_WithValidData_ReturnsOk() {
        // Arrange
        EventRepresentation eventRep = new EventRepresentation();
        eventRep.title = "Updated Event";
        eventRep.date = testDate;
        eventRep.time = testTime;
        eventRep.location = "Updated Location";
        eventRep.description = "Updated Description";
        eventRep.eventType = EventType.CONCERT;
        eventRep.organizerId = testOrganizer.getId();

        when(eventRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toRepresentation(any(Event.class))).thenReturn(eventRep);

        // Act
        Response response = eventResource.updateEvent(1, eventRep);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void updateEvent_WithInvalidData_ReturnsBadRequest() {
        // Arrange
        EventRepresentation eventRep = new EventRepresentation();
        // Αφήνουμε το eventRep κενό για να προκαλέσει IllegalArgumentException

        when(eventRepository.findByIdOptional(1L)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toRepresentation(any(Event.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        Response response = eventResource.updateEvent(1, eventRep);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void updateEvent_WithNonExistentId_Returns404() {
        // Arrange
        EventRepresentation eventRep = new EventRepresentation();
        eventRep.title = "Test Event";

        when(eventRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        // Act
        Response response = eventResource.updateEvent(999, eventRep);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(eventRepository, never()).persist(any(Event.class));
    }
    @Test
    void createEvent_WithValidData_ReturnsCreated() {
        // Arrange
        EventRepresentation eventRep = new EventRepresentation();
        eventRep.title = "New Event";
        eventRep.date = testDate;
        eventRep.time = testTime;
        eventRep.location = "New Location";
        eventRep.description = "New Description";
        eventRep.eventType = EventType.CONCERT;
        eventRep.organizerId = testOrganizer.getId();

        Event newEvent = new Event(
                eventRep.title,
                eventRep.date,
                eventRep.time,
                eventRep.location,
                eventRep.description,
                eventRep.eventType,
                testOrganizer
        );
        newEvent.setId(1);

        when(eventMapper.toEntity(eventRep)).thenReturn(newEvent);
        when(eventMapper.toRepresentation(any(Event.class))).thenReturn(eventRep);

        // Act
        Response response = eventResource.createEvent(eventRep);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(eventRepository).persist(newEvent);
        verify(eventMapper).toRepresentation(newEvent);
    }
}