package grupo7.models;

import grupo7.models.keys.SupportId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@IdClass(SupportId.class)  // Indica que esta clase utiliza una clave compuesta
public class Support {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private int rating;

    // Getters y Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
