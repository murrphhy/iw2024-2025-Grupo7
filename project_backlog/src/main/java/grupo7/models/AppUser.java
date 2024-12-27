package grupo7.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String academicPosition;

    @Column(nullable = false)
    private String center;

    @Column(unique= true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String technical_area;

    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "applicantId")
    private List<Project> projects = new ArrayList<>();

    // Constructor vacío
    public AppUser() {}

    // Constructor con parámetros
    public AppUser(String username, String email, String password, String academicPosition, String center, String technical_area, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.academicPosition = academicPosition;
        this.center = center;
        this.technical_area = technical_area;
        this.role = role;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAcademicPosition() {
        return academicPosition;
    }

    public void setAcademicPosition(String academicPosition) {
        this.academicPosition = academicPosition;
    }

    public String getCenter() { 
        return center; 
    }

    public void setCenter(String center){ 
        this.center = center; 
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTechnicalArea() { 
        return technical_area; 
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public void setTechnicalArea(String technical_area){
        this.technical_area = technical_area; 
    }

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }
}