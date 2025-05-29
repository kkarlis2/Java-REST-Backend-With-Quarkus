package gr.aueb.representation;

import gr.aueb.domain.ZipCode;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ZipCodeMapperTest {

    @Inject
    private ZipCodeMapper zipCodeMapper;

    @Test
    void testToRepresentation() {
        // Given: a sample ZipCode
        ZipCode zipCode = new ZipCode("12345");

        // When: converting ZipCode to String
        String zipCodeRepresentation = zipCodeMapper.toRepresentation(zipCode);

        // Then: validate the representation
        assertNotNull(zipCodeRepresentation, "Representation should not be null");
        assertEquals(zipCode.getZipCode(), zipCodeRepresentation, "ZipCode should match");
    }

    @Test
    void testToEntity() {
        // Given: a sample zip code string
        String zipCodeString = "12345";

        // When: converting String to ZipCode
        ZipCode zipCode = zipCodeMapper.toEntity(zipCodeString);

        // Then: validate the entity
        assertNotNull(zipCode, "ZipCode should not be null");
        assertEquals(zipCodeString, zipCode.getZipCode(), "ZipCode should match");
    }

    @Test
    void testToRepresentationNullZipCode() {
        // Given: a null ZipCode
        ZipCode nullZipCode = null;

        // When: converting null ZipCode to String
        String zipCodeRepresentation = zipCodeMapper.toRepresentation(nullZipCode);

        // Then: validate that the result is null
        assertNull(zipCodeRepresentation, "Representation should be null");
    }

    @Test
    void testToEntityNullString() {
        // Given: a null String
        String nullString = null;

        // When: converting null String to ZipCode
        ZipCode zipCode = zipCodeMapper.toEntity(nullString);

        // Then: validate that the result is null
        assertNull(zipCode, "ZipCode should be null");
    }

    @Test
    void testZipCodeRepresentationGetterSetter() {
        // Arrange
        ZipCodeRepresentation representation = new ZipCodeRepresentation();

        // Act
        representation.setZipCode("12345");

        // Assert
        assertEquals("12345", representation.getZipCode(), "ZipCode getter should return the value set by setter");

        // Test with null value
        representation.setZipCode(null);
        assertNull(representation.getZipCode(), "ZipCode getter should return null when set to null");
    }
}

