package gr.aueb.representation;

import gr.aueb.domain.Address;
import gr.aueb.domain.ZipCode;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AddressMapperTest {

    @Inject
    private AddressMapper addressMapper;

    @Test
    void testToEntity() {
        // Given: a sample AddressRepresentation
        AddressRepresentation representation = new AddressRepresentation();
        representation.street = "Main St";
        representation.number = "123";
        representation.zipCode = "12345";

        // When: converting AddressRepresentation to Address
        Address address = addressMapper.toEntity(representation);

        // Then: validate the mapping
        assertNotNull(address, "Address should not be null");
        assertEquals(representation.street, address.getStreet(), "Street should match");
        assertEquals(representation.number, address.getNumber(), "Street number should match");
        assertNotNull(address.getZipCode(), "ZipCode should not be null");
        assertEquals(representation.zipCode, address.getZipCode().getZipCode(), "ZipCode should match");
    }

    @Test
    void testToRepresentation() {
        // Given: a sample Address
        Address address = new Address("Main St", "123", new ZipCode("12345"));

        // When: converting Address to AddressRepresentation
        AddressRepresentation representation = addressMapper.toRepresentation(address);

        // Then: validate the representation
        assertNotNull(representation, "Representation should not be null");
        assertEquals(address.getStreet(), representation.street, "Street should match");
        assertEquals(address.getNumber(), representation.number, "Street number should match");
        assertEquals(address.getZipCode().getZipCode(), representation.zipCode, "ZipCode should match");
    }

    @Test
    void testToEntityNullRepresentation() {
        // Given: a null AddressRepresentation
        AddressRepresentation nullRepresentation = null;

        // When: converting null AddressRepresentation to Address
        Address address = addressMapper.toEntity(nullRepresentation);

        // Then: validate that the result is null
        assertNull(address, "Address should be null");
    }

    @Test
    void testToRepresentationNullAddress() {
        // Given: a null Address
        Address nullAddress = null;

        // When: converting null Address to AddressRepresentation
        AddressRepresentation representation = addressMapper.toRepresentation(nullAddress);

        // Then: validate that the result is null
        assertNull(representation, "Representation should be null");
    }
    @Test
    void testAddressRepresentationGetters() {
        // Given: Create a new AddressRepresentation with initial values
        AddressRepresentation representation = new AddressRepresentation();
        representation.setStreet("Σταδίου");
        representation.setNumber("10");
        representation.setZipCode("10562");

        // When & Then: Test each getter
        assertEquals("Σταδίου", representation.getStreet(), "Street getter should return correct value");
        assertEquals("10", representation.getNumber(), "Number getter should return correct value");
        assertEquals("10562", representation.getZipCode(), "ZipCode getter should return correct value");
    }
}
