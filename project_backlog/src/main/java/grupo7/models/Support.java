package grupo7.models;

import grupo7.models.keys.SupportId;

import jakarta.persistence.*;

/**
 * Represents the support relationship between a user and a project.
 * Each support includes a user ID, a project ID, and a rating.
 */
@Entity
@Table(name = "support")
@IdClass(SupportId.class) // Indicates the use of a composite key
public class Support {

    /**
     * ID of the user providing support.
     */
    @Id
    private Long user_id;

    /**
     * ID of the project being supported.
     */
    @Id
    private Long project_id;

    /**
     * Rating given by the user to the project. Cannot be null.
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Default constructor required by JPA.
     */
    public Support() {
    }

    /**
     * Constructs a new Support instance with the specified details.
     *
     * @param user_id    the ID of the user providing support
     * @param project_id the ID of the project being supported
     * @param rating     the rating given by the user
     */
    public Support(Long user_id, Long project_id, int rating) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.rating = rating;
    }

    /**
     * Gets the ID of the user providing support.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return user_id;
    }

    /**
     * Sets the ID of the user providing support.
     *
     * @param user_id the new user ID
     */
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets the ID of the project being supported.
     *
     * @return the project ID
     */
    public Long getProjectId() {
        return project_id;
    }

    /**
     * Sets the ID of the project being supported.
     *
     * @param project_id the new project ID
     */
    public void setProjectId(Long project_id) {
        this.project_id = project_id;
    }

    /**
     * Gets the rating given by the user to the project.
     *
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating given by the user to the project.
     *
     * @param rating the new rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }
}