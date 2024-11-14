package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String academic_position;
    private boolean isAdmin;

    // Constructor vacío (para JPA)
    public User() {}

    // Constructor con parámetros
    public User(String nombre, String email, String password) {
        this.name = nombre;
        this.email = email;
        this.password = password;
        this.academic_position = academic_position;
        this.isAdmin = isAdmin;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }

    public String getAcademicPosition() {
        return academic_position;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAcademicPosition(String academicPosition) {
        this.academic_position = academic_position;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}