package grupo7.models;

import grupo7.models.keys.SupportId;

import jakarta.persistence.*;

@Entity
@IdClass(SupportId.class)  // Significa que esta clase utiliza una clave compuesta
public class Support {

    @Id
    private Long user_id;

    @Id
    private Long project_id;

    private int rating;

    // Constructor vacío
    public Support() {
    }

    // Constructor completo
    public Support(Long user_id, Long project_id, int rating) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.rating = rating;
    }

    // Getters y Setters
    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public Long getProjectId() {
        return project_id;
    }

    public void setProjectId(Long project_id) {
        this.project_id = project_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}