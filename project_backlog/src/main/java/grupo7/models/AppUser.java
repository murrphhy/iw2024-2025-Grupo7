package grupo7.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique= true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String academicPosition;

    @Column(nullable = false)
    private String center;

    private String technical_area;
    private boolean isAdmin;

    public AppUser() {}

    public AppUser(String username, String email, String password, String academicPosition, String center, String technical_area, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.academicPosition = academicPosition;
        this.center = center;
        this.technical_area = technical_area;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
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
    public String getCenter() { return center; }
    public String getTechnicalArea() { return technical_area; }


    public void setId(Long id) {
        this.id = id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setAcademicPosition(String academicPosition){
        this.academicPosition = academicPosition;
    }
    public void setCenter(String center){ this.center = center; }
    public void setTechnical_area(String technical_area){ this.technical_area = technical_area; }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

}
