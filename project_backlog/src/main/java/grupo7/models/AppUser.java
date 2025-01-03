package grupo7.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an application user in the system.
 * Each user has a unique email and is associated with multiple projects.
 */
@Entity
@Table(name = "users")
public class AppUser {

    /**
     * Unique identifier for the user. Generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String username;

    /**
     * Academic position of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String academicPosition;

    /**
     * Center or institution to which the user belongs. Cannot be null.
     */
    @Column(nullable = false)
    private String center;

    /**
     * Unique email address of the user. Cannot be null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Encrypted password of the user. Cannot be null.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Technical area of expertise for the user. Cannot be null.
     */
    @Column(nullable = false)
    private String technical_area;

    /**
     * Role of the user within the system (e.g., ADMIN, USER). Cannot be null.
     */
    @Column(nullable = false)
    private Role role;

    /**
     * List of projects associated with the user as an applicant.
     * The relationship is mapped by the applicantId field in the Project entity.
     */
    @OneToMany(mappedBy = "applicantId")
    private List<Project> projects = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public AppUser() {}

    /**
     * Constructs a new AppUser with the specified details.
     *
     * @param username        the username of the user
     * @param email           the email address of the user
     * @param password        the password of the user
     * @param academicPosition the academic position of the user
     * @param center          the center or institution of the user
     * @param technical_area  the technical area of the user
     * @param role            the role of the user within the system
     */
    public AppUser(String username, String email, String password, String academicPosition, String center, String technical_area, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.academicPosition = academicPosition;
        this.center = center;
        this.technical_area = technical_area;
        this.role = role;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the new ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the academic position of the user.
     *
     * @return the academic position
     */
    public String getAcademicPosition() {
        return academicPosition;
    }

    /**
     * Sets the academic position of the user.
     *
     * @param academicPosition the new academic position
     */
    public void setAcademicPosition(String academicPosition) {
        this.academicPosition = academicPosition;
    }

    /**
     * Gets the center or institution of the user.
     *
     * @return the center
     */
    public String getCenter() {
        return center;
    }

    /**
     * Sets the center or institution of the user.
     *
     * @param center the new center
     */
    public void setCenter(String center) {
        this.center = center;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the technical area of the user.
     *
     * @return the technical area
     */
    public String getTechnicalArea() {
        return technical_area;
    }

    /**
     * Sets the technical area of the user.
     *
     * @param technical_area the new technical area
     */
    public void setTechnicalArea(String technical_area) {
        this.technical_area = technical_area;
    }

    /**
     * Gets the role of the user within the system.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user within the system.
     *
     * @param role the new role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the list of projects associated with the user.
     *
     * @return the list of projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * Sets the list of projects associated with the user.
     *
     * @param projects the new list of projects
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
