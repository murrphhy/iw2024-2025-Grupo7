package grupo7.models;

import javax.persistence.*;

@Entity
@Table(name = "User")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String academicPosition;
    private boolean isAdmin;

    public User() {}

    public User(String name, String email, String password, String academicPosition, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.academicPosition = academicPosition;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail(){
        return email;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public String getPassword() {
        return password;
    }
    public String getAcademicPosition() {
        return academicPosition;
    }


    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setAcademicPosition(String academicPosition){
        this.academicPosition = academicPosition;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
