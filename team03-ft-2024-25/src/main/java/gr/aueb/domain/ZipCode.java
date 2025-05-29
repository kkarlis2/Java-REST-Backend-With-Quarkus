package gr.aueb.domain;

import jakarta.persistence.*;

@Entity
@Table(name="zip_code")
public class ZipCode {

    @Id
    @Column(name = "zipCode", length = 15, nullable = false)
    private String zipCode;

    public ZipCode(){
    }

    public ZipCode(String zipCode){
        setZipCode(zipCode);
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        if(zipCode == null || zipCode.isEmpty()){
            throw new IllegalArgumentException("ZipCode cannot be null or empty");
        }
        this.zipCode = zipCode;
    }
}
