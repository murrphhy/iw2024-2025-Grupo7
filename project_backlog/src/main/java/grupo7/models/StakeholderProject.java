package grupo7.models;

import grupo7.models.keys.StakeholderProjectId;
import jakarta.persistence.*;

/**
 * Represents the association between a stakeholder (user) and a project.
 * This entity includes information about the stakeholder's financing contribution.
 */
@Entity
@Table(name = "stakeholder_project")
@IdClass(StakeholderProjectId.class) // Utilizes a composite key
public class StakeholderProject {

    /**
     * ID of the stakeholder (user) associated with the project.
     */
    @Id
    private Long user_id;

    /**
     * ID of the project associated with the stakeholder.
     */
    @Id
    private Long project_id;

    /**
     * The amount of financing contributed by the stakeholder to the project.
     * Cannot be null.
     */
    @Column(nullable = false)
    private Double financing;

    /**
     * Default constructor required by JPA.
     */
    public StakeholderProject() {
    }

    /**
     * Constructs a new StakeholderProject instance with the specified details.
     *
     * @param user_id    the ID of the stakeholder (user)
     * @param project_id the ID of the project
     * @param financing  the amount of financing contributed
     */
    public StakeholderProject(Long user_id, Long project_id, Double financing) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.financing = financing;
    }

    /**
     * Gets the ID of the stakeholder (user).
     *
     * @return the user ID
     */
    public Long getUserId() {
        return user_id;
    }

    /**
     * Sets the ID of the stakeholder (user).
     *
     * @param user_id the new user ID
     */
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets the ID of the project.
     *
     * @return the project ID
     */
    public Long getProjectId() {
        return project_id;
    }

    /**
     * Sets the ID of the project.
     *
     * @param project_id the new project ID
     */
    public void setProjectId(Long project_id) {
        this.project_id = project_id;
    }

    /**
     * Gets the amount of financing contributed by the stakeholder.
     *
     * @return the financing amount
     */
    public Double getFinancing() {
        return financing;
    }

    /**
     * Sets the amount of financing contributed by the stakeholder.
     *
     * @param financing the new financing amount
     */
    public void setFinancing(Double financing) {
        this.financing = financing;
    }
}
