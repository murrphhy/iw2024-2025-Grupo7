package grupo7.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

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
    private Boolean isAdmin;

    // Constructor vacío
    public AppUser() {}

    // Constructor con parámetros
    public AppUser(String username, String email, String password, String academicPosition, String center, String technical_area, Boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.academicPosition = academicPosition;
        this.center = center;
        this.technical_area = technical_area;
        this.isAdmin = isAdmin;
    }

    // Getters y setters
    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAcademicPosition() {
        return academicPosition;
    }

    public String getCenter() { 
        return center; 
    }

    public String getTechnicalArea() { 
        return technical_area; 
    }

    public boolean getisAdmin() {
        return isAdmin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAcademicPosition(String academicPosition) {
        this.academicPosition = academicPosition;
    }

    public void setCenter(String center){ 
        this.center = center; 
    }

    public void setTechnical_area(String technical_area){ 
        this.technical_area = technical_area; 
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}