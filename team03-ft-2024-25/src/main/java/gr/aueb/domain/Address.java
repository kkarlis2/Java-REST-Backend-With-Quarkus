package gr.aueb.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Address {

    @Column(name = "street", length = 50, nullable = false)
    private String street;

    @Column(name = "number", length = 50, nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "zipCode", nullable = false)
    private ZipCode zipCode;


    public Address(){
    }

    public Address(String street, String number, ZipCode zipCode){
        setStreet(street);
        setNumber(number);
        setZipCode(zipCode);
    }

    public String getStreet(){
        return street;
    }

    public String getNumber(){
        return number;
    }

    public ZipCode getZipCode(){
        return zipCode;
    }

    public void setStreet(String street){
        if(street == null || street.isEmpty()){
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        this.street = street;
    }

    public void setNumber(String number){
        if(number == null || number.isEmpty()){
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        this.number = number;
    }

    public void setZipCode(ZipCode zipCode) {
        if(zipCode == null){
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        this.zipCode = zipCode;
    }
}
