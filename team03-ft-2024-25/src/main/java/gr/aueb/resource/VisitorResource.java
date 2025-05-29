package gr.aueb.resource;

import gr.aueb.domain.Account;
import gr.aueb.domain.Visitor;
import gr.aueb.persistence.VisitorRepository;
import gr.aueb.representation.VisitorMapper;
import gr.aueb.representation.VisitorRepresentation;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/visitors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VisitorResource {

    @Inject
    VisitorRepository visitorRepository;

    @Inject
    VisitorMapper visitorMapper;

    @GET
    public Response getAllVisitors() {
        List<Visitor> visitors = visitorRepository.listAll();
        return Response.ok(visitorMapper.toRepresentationList(visitors)).build();
    }

    @GET
    @Path("/{id}")
    public Response getVisitorById(@PathParam("id") Integer id) {
        return visitorRepository.findByIdOptional(Long.valueOf(id))
                .map(visitor -> Response.ok(visitorMapper.toRepresentation(visitor)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/username/{username}")
    public Response getVisitorByUsername(@PathParam("username") String username) {
        return visitorRepository.findByUsername(username)
                .map(visitor -> Response.ok(visitorMapper.toRepresentation(visitor)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/email/{email}")
    public Response getVisitorByEmail(@PathParam("email") String email) {
        return visitorRepository.findByEmail(email)
                .map(visitor -> Response.ok(visitorMapper.toRepresentation(visitor)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response createVisitor(VisitorRepresentation visitorRep) {
        try {
            // Έλεγχος για υπάρχον username
            if (visitorRepository.findByUsername(visitorRep.getUsername()).isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Username already exists")
                        .build();
            }

            // Έλεγχος για υπάρχον email
            if (visitorRepository.findByEmail(visitorRep.getEmail()).isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Email already exists")
                        .build();
            }

            Visitor visitor = visitorMapper.toEntity(visitorRep);
            visitorRepository.persist(visitor);
            return Response.status(Response.Status.CREATED)
                    .entity(visitorMapper.toRepresentation(visitor))
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
    public Response updateVisitor(@PathParam("id") Integer id, VisitorRepresentation visitorRep) {
        return visitorRepository.findByIdOptional(Long.valueOf(id))
                .map(existingVisitor -> {
                    try {
                        // Έλεγχος για υπάρχον username
                        visitorRepository.findByUsername(visitorRep.getUsername())
                                .filter(v -> !v.getId().equals(id))
                                .ifPresent(v -> {
                                    throw new BadRequestException("Username already exists");
                                });

                        // Έλεγχος για υπάρχον email
                        visitorRepository.findByEmail(visitorRep.getEmail())
                                .filter(v -> !v.getId().equals(id))
                                .ifPresent(v -> {
                                    throw new BadRequestException("Email already exists");
                                });

                        // Ενημέρωση πεδίων
                        existingVisitor.setFirstName(visitorRep.getFirstName());
                        existingVisitor.setLastName(visitorRep.getLastName());
                        existingVisitor.setPhoneNumber(visitorRep.getPhoneNumber());
                        existingVisitor.setEmail(visitorRep.getEmail());
                        existingVisitor.setAccount(new Account(visitorRep.getUsername(), visitorRep.getPassword()));

                        visitorRepository.persist(existingVisitor);
                        return Response.ok(visitorMapper.toRepresentation(existingVisitor)).build();
                    } catch (BadRequestException e) {
                        return Response.status(Response.Status.CONFLICT)
                                .entity(e.getMessage())
                                .build();
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
    public Response deleteVisitor(@PathParam("id") Integer id) {
        return visitorRepository.findByIdOptional(Long.valueOf(id))
                .map(visitor -> {
                    visitorRepository.delete(visitor);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}