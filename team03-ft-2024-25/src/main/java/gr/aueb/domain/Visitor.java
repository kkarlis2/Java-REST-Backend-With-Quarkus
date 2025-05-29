    package gr.aueb.domain;

    import jakarta.persistence.*;

    import java.util.HashSet;
    import java.util.Objects;
    import java.util.Set;

    @Entity
    @Table(name="visitors")
    public class Visitor {

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;

        @Column(name = "first_name", length = 50, nullable = false)
        private String firstName;

        @Column(name = "last_name", length = 50, nullable = false)
        private String lastName;

        @Column(name = "phone_number", unique = true,length = 20, nullable = false)
        private String phoneNumber;

        @Embedded
        private Account account;

        @Embedded
        private Email email;

        @OneToMany(mappedBy = "visitor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Set<Reservation> reservations = new HashSet<>();


        public Visitor(){

        }

        public Visitor(String firstName, String lastName, String phoneNumber, String email, String username, String password){

            setFirstName(firstName);
            setLastName(lastName);
            setPhoneNumber(phoneNumber);
            this.email = new Email(email);
            this.account = new Account(username, password);
        }

        public Integer getId(){
            return id;
        }

        public void setId(Integer id){
            this.id=id;
        }
        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public Account getAccount(){
            return account==null? null:new Account(getUsername(),getPassword());
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public void setEmail(String email) {
            if (this.email == null) {
                this.email = new Email(email);
            } else {
                this.email.setEmail(email);
            }
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getUsername() {
            return account.getUsername();
        }

        public String getPassword(){
            return account.getPassword();
        }

        public String getEmail() {
            return email.getEmail();
        }

        public Set<Reservation> getReservations() {
            return reservations;
        }


        public void addReservation(Reservation reservation) {
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation cannot be null!");
            }
            reservations.add(reservation);
            reservation.setVisitor(this);
        }

        public void removeReservation(Reservation reservation) {
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation cannot be null!");
            }
            reservations.remove(reservation);

        }

       public void setFirstName(String firstName) {
            if(firstName == null || firstName.isEmpty()){
                throw new IllegalArgumentException("First name cannot be null or empty");
            }
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            if(lastName == null || lastName.isEmpty()){
                throw new IllegalArgumentException("Last name cannot be null or empty");
            }
            this.lastName = lastName;
        }

        public void setPhoneNumber(String phoneNumber) {
            if(phoneNumber == null || phoneNumber.isEmpty()){
                throw new IllegalArgumentException("Phone number cannot be null or empty");
            }
            this.phoneNumber = phoneNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Visitor that = (Visitor) o;
            return  getPhoneNumber().equals(that.getPhoneNumber()) && getEmail().equals(that.getEmail())
                    && getUsername().equals(that.getUsername()) && getPassword().equals(that.getPassword());
        }

        @Override
        public int hashCode(){
            return Objects.hash(getPhoneNumber(), getEmail(), getUsername(), getPassword());
        }


    }
