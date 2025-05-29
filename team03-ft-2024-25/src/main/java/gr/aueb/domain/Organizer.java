package gr.aueb.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "organizers")
public class Organizer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    //AFM
    @Column(name = "tax_id",unique = true, length = 25, nullable = false)
    private String taxId;

    @Column(name = "brand_name", length = 50, nullable = false)
    private String brandName;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Embedded
    private Account account;

    @Embedded
    private Email email;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "organizer",cascade = CascadeType.ALL,fetch=FetchType.LAZY,orphanRemoval = true)
    private Set<Event> events=new HashSet<>();


    public Organizer(){
    }

    public Organizer(String taxId, String brandName, String phoneNumber, String userName, String password,
                     String email, String street, String number, ZipCode zipCode) {

        setTaxId(taxId);
        setBrandName(brandName);
        setPhoneNumber(phoneNumber);

        this.account = new Account(userName, password);
        this.email = new Email(email);
        this.address = new Address(street, number, zipCode);

    }

    public Account getAccount(){
        return account==null? null:new Account(getUserName(),getPassword());
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Address getAddress(){
        return address==null? null:new Address(getStreet(),getNumber(),address.getZipCode());
    }

    public void setAddress(Address address) {
        this.address = address;
    }

//    public Email getEmail() {
//        return email;
//    }

    public void setEmail(String email) {
        if (this.email == null) {
            this.email = new Email(email);
        } else {
            this.email.setEmail(email);
        }
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        if (taxId == null || taxId.isEmpty()){
            throw new IllegalArgumentException("Tax id cannot be set to null or empty");
        }
        this.taxId = taxId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        if(brandName == null || brandName.isEmpty()){
            throw new IllegalArgumentException("Brand name cannot be null or empty");
        }

        this.brandName = brandName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty()){
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        this.phoneNumber = phoneNumber;
    }

    public Integer getId(){
        return Id;
    }

    public void setId(int id){
        this.Id=id;
    }
    public String getUserName(){
        return account.getUsername();
    }

    public String getPassword(){
        return account.getPassword();
    }

    public String getEmail(){
        return email.getEmail();
    }

    public String getStreet(){
        return address.getStreet();
    }

    public String getNumber(){
        return address.getNumber();
    }

    public String getZipCode(){
        return address.getZipCode().getZipCode();
    }

    public Set<Event> getEvents(){
        return events;
    }

    public void addEvent(Event event){
        if (event == null) {
            throw new IllegalArgumentException("Event can't be null!Add a valid one!");
        }
        events.add(event);

        event.setOrganizer(this);
    }
    public void removeEvent(Event event){
        if(event==null) {
            throw new IllegalArgumentException("Event can't be null! Remove a valid one!");
        }
        events.remove(event);

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organizer organizer = (Organizer) o;
        return Objects.equals(taxId, organizer.taxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxId);
    }
}
