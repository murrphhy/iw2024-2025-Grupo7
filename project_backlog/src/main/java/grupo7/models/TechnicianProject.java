package grupo7.models;

import grupo7.models.keys.TechnicianProjectId;

import jakarta.persistence.*;

/**
 * Represents the relationship between a technician (user) and a project.
 * This entity includes information about the technician's appraisal of the project.
 */
@Entity
@Table(name = "technician_project")
@IdClass(TechnicianProjectId.class) // Indicates the use of a composite key
public class TechnicianProject {

    /**
     * ID of the technician (user) associated with the project.
     */
    @Id
    private Long user_id;

    /**
     * ID of the project associated with the technician.
     * This ID is unique and cannot be null.
     */
    @Id
    @Column(unique = true, nullable = false)
    private Long project_id;

    /**
     * Appraisal value provided by the technician for the project.
     * Cannot be null.
     */
    @Column(nullable = false)
    private Double projectAppraisal;

    /**
     * Default constructor required by JPA.
     */
    public TechnicianProject() {
    }

    /**
     * Constructs a new TechnicianProject instance with the specified details.
     *
     * @param user_id          the ID of the technician (user)
     * @param project_id       the ID of the project
     * @param projectAppraisal the appraisal value given by the technician
     */
    public TechnicianProject(Long user_id, Long project_id, Double projectAppraisal) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.projectAppraisal = projectAppraisal;
    }

    /**
     * Gets the ID of the technician (user) associated with the project.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return user_id;
    }

    /**
     * Sets the ID of the technician (user) associated with the project.
     *
     * @param user_id the new user ID
     */
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets the ID of the project associated with the technician.
     *
     * @return the project ID
     */
    public Long getProjectId() {
        return project_id;
    }

    /**
     * Sets the ID of the project associated with the technician.
     *
     * @param project_id the new project ID
     */
    public void setProject(Long project_id) {
        this.project_id = project_id;
    }

    /**
     * Gets the appraisal value provided by the technician for the project.
     *
     * @return the appraisal value
     */
    public Double getProjectAppraisal() {
        return projectAppraisal;
    }

    /**
     * Sets the appraisal value provided by the technician for the project.
     *
     * @param projectAppraisal the new appraisal value
     */
    public void setProjectAppraisal(Double projectAppraisal) {
        this.projectAppraisal = projectAppraisal;
    }
}
