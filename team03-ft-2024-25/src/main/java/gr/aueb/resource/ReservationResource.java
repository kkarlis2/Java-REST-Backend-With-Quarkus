package gr.aueb.resource;

import gr.aueb.domain.Reservation;
import gr.aueb.domain.ReservationStatus;
import gr.aueb.persistence.ReservationRepository;
import gr.aueb.representation.ReservationMapper;
import gr.aueb.representation.ReservationRepresentation;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    ReservationMapper reservationMapper;

    @GET
    public Response getAllReservations() {
        List<Reservation> reservations = reservationRepository.listAll();
        return Response.ok(reservationMapper.toRepresentationList(reservations)).build();
    }

    @GET
    @Path("/{id}")
    public Response getReservationById(@PathParam("id") Integer id) {
        return reservationRepository.findByIdOptional(Long.valueOf(id))
                .map(reservation -> Response.ok(reservationMapper.toRepresentation(reservation)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/visitor/{visitorId}")
    public Response getReservationsByVisitor(@PathParam("visitorId") Integer visitorId) {
        List<Reservation> reservations = reservationRepository.findByVisitorId(visitorId);
        return Response.ok(reservationMapper.toRepresentationList(reservations)).build();
    }

    @GET
    @Path("/status/{status}")
    public Response getReservationsByStatus(@PathParam("status") ReservationStatus status) {
        List<Reservation> reservations = reservationRepository.findByStatus(status);
        return Response.ok(reservationMapper.toRepresentationList(reservations)).build();
    }

    @POST
    @Transactional
    public Response createReservation(ReservationRepresentation reservationRep) {
        try {
            Reservation reservation = reservationMapper.toEntity(reservationRep);
            reservationRepository.persist(reservation);
            return Response.status(Response.Status.CREATED)
                    .entity(reservationMapper.toRepresentation(reservation))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateReservation(@PathParam("id") Integer id, ReservationRepresentation reservationRep) {
        return reservationRepository.findByIdOptional(Long.valueOf(id))
                .map(existingReservation -> {
                    try {
                        // Ενημέρωση των πεδίων
                        existingReservation.setReservedSeats(reservationRep.reservedSeats);
                        existingReservation.setReservationDate(reservationRep.reservationDate);
                        existingReservation.setStatus(reservationRep.status);
                        existingReservation.setDiscount(reservationRep.discount);

                        reservationRepository.persist(existingReservation);
                        return Response.ok(reservationMapper.toRepresentation(existingReservation)).build();
                    } catch (IllegalArgumentException e) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(e.getMessage())
                                .build();
                    }
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteReservation(@PathParam("id") Integer id) {
        return reservationRepository.findByIdOptional(Long.valueOf(id))
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}/cancel")
    @Transactional
    public Response cancelReservation(@PathParam("id") Integer id) {
        return reservationRepository.findByIdOptional(Long.valueOf(id))
                .map(reservation -> {
                    try {
                        reservation.setStatus(ReservationStatus.CANCELLED);
                        reservationRepository.persist(reservation);
                        return Response.ok(reservationMapper.toRepresentation(reservation)).build();
                    } catch (IllegalArgumentException e) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(e.getMessage())
                                .build();
                    }
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}