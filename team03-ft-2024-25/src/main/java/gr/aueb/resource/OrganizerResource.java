package gr.aueb.resource;

import gr.aueb.domain.Organizer;
import gr.aueb.persistence.OrganizerRepository;
import gr.aueb.representation.OrganizerMapper;
import gr.aueb.representation.OrganizerRepresentation;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/organizers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizerResource {

    @Inject
    OrganizerRepository organizerRepository;

    @Inject
    OrganizerMapper organizerMapper;

    @GET
    public Response getAllOrganizers() {
        List<Organizer> organizers = organizerRepository.listAll();
        return Response.ok(organizerMapper.toRepresentationList(organizers)).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrganizerById(@PathParam("id") Integer id) {
        return organizerRepository.findByIdOptional(Long.valueOf(id))
                .map(organizer -> Response.ok(organizerMapper.toRepresentation(organizer)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/taxId/{taxId}")
    public Response getOrganizerByTaxId(@PathParam("taxId") String taxId) {
        return organizerRepository.findByTaxId(taxId)
                .map(organizer -> Response.ok(organizerMapper.toRepresentation(organizer)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response createOrganizer(OrganizerRepresentation organizerRep) {
        try {
            // Έλεγχος αν υπάρχει ήδη organizer με το ίδιο taxId
            if (organizerRepository.findByTaxId(organizerRep.getTaxId()).isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Organizer with tax ID " + organizerRep.getTaxId() + " already exists")
                        .build();
            }

            // Έλεγχος αν υπάρχει ήδη organizer με το ίδιο username
            if (organizerRepository.findByUsername(organizerRep.getUsername()).isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Username " + organizerRep.getUsername() + " is already taken")
                        .build();
            }

            Organizer organizer = organizerMapper.toEntity(organizerRep);
            organizerRepository.persist(organizer);
            return Response.status(Response.Status.CREATED)
                    .entity(organizerMapper.toRepresentation(organizer))
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
    public Response updateOrganizer(@PathParam("id") Integer id, OrganizerRepresentation organizerRep) {
        return organizerRepository.findByIdOptional(Long.valueOf(id))
                .map(existingOrganizer -> {
                    try {
                        // Έλεγχος αν το νέο taxId υπάρχει ήδη σε άλλον organizer
                        organizerRepository.findByTaxId(organizerRep.getTaxId())
                                .filter(org -> !org.getId().equals(id))
                                .ifPresent(org -> {
                                    throw new BadRequestException("Tax ID already exists");
                                });

                        // Έλεγχος αν το νέο username υπάρχει ήδη σε άλλον organizer
                        organizerRepository.findByUsername(organizerRep.getUsername())
                                .filter(org -> !org.getId().equals(id))
                                .ifPresent(org -> {
                                    throw new BadRequestException("Username already exists");
                                });

                        // Ενημέρωση των πεδίων
                        existingOrganizer.setTaxId(organizerRep.getTaxId());
                        existingOrganizer.setBrandName(organizerRep.getBrandName());
                        existingOrganizer.setPhoneNumber(organizerRep.getPhoneNumber());
                        existingOrganizer.setEmail(organizerRep.getEmail());

                        // Ενημέρωση του embedded Address
                        existingOrganizer.setAddress(organizerMapper.toEntity(organizerRep).getAddress());

                        // Ενημέρωση του embedded Account
                        existingOrganizer.setAccount(organizerMapper.toEntity(organizerRep).getAccount());

                        organizerRepository.persist(existingOrganizer);
                        return Response.ok(organizerMapper.toRepresentation(existingOrganizer)).build();
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
    public Response deleteOrganizer(@PathParam("id") Integer id) {
        return organizerRepository.findByIdOptional(Long.valueOf(id))
                .map(organizer -> {
                    organizerRepository.delete(organizer);
                    return Response.noContent().build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}