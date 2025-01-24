package grupo7.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a call for project submissions within the system.
 * Each call has a budget, a unique identifier, and is associated with multiple projects.
 */
@Entity
@Table(name = "calls")
public class Calls {

    /**
     * Unique identifier for the call. Generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the call. Cannot be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Detailed description of the call.
     */
    @Column(nullable = false)
    private String description;

    /**
     * Total budget of the call. Cannot be null.
     */
    @Column(nullable = false)
    private Double totalBudget;

    /**
     * Status of the call (open or closed). Cannot be null.
     */
    @Column(nullable = false)
    private String state;

    /**
     * List of projects associated with the call.
     * The relationship is mapped by the callId field in the Project entity.
     */
    @OneToMany(mappedBy = "call")
    private List<Project> projects = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Calls() {}

    /**
     * Constructs a new Call with the specified details.
     *
     * @param name          the name of the call
     * @param totalBudget the total budget of the call
     * @param state          the status of the call (open or closed)
     * @param description the description of the call
     */
    public Calls(String name, Double totalBudget, String state, String description) {
        this.name = name;
        this.totalBudget = totalBudget;
        this.state = state;
        this.description = description;
    }

    /**
     * Gets the unique identifier of the call.
     *
     * @return the call ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the call.
     *
     * @param id the new ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the call.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the call.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the call.
     *
     * @return the name
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the call.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }



    /**
     * Gets the total budget of the call.
     *
     * @return the total budget
     */
    public Double getTotalBudget() {
        return totalBudget;
    }

    /**
     * Sets the total budget of the call.
     *
     * @param totalBudget the new total budget
     */
    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    /**
     * Gets the status of the call (open or closed).
     *
     * @return the status
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the status of the call (open or closed).
     *
     * @param state the new status
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the list of projects associated with the call.
     *
     * @return the list of projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * Sets the list of projects associated with the call.
     *
     * @param projects the new list of projects
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
