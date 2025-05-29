package gr.aueb.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
@Embeddable
public class Email {

    @Column(name = "email",unique = true, length = 50, nullable = false)
    private String email;

    public Email(){
    }

    public Email(String email){
        setEmail(email);
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        if(email == null || email.isEmpty()){
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = email;
    }
}
