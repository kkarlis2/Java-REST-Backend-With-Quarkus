package gr.aueb.representation;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ZipCodeRepresentation {
    public String zipCode;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
