package gr.aueb.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Account {

    @Column(name = "username",unique = true,length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 25, nullable = false)
    private String password;

    public Account(){
    }

    public Account(String username, String password){
        setUsername(username);
        setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        if(username == null || username.isEmpty()){
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if(password == null || password.isEmpty()){
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
    }
}
