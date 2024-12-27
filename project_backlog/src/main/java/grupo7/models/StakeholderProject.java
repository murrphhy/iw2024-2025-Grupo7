package grupo7.models;

import grupo7.models.keys.StakeholderProjectId;

import jakarta.persistence.*;

@Entity
@Table(name = "stakeholder_project")
@IdClass(StakeholderProjectId.class) // Utiliza una clave compuesta
public class StakeholderProject {

    @Id
    private Long user_id;

    @Id
    private Long project_id;

    @Column(nullable = false)
    private Double financing;

    
    // Constructor vacío
    public StakeholderProject() {
    }

    // Constructor completo
    public StakeholderProject(Long user_id, Long project_id, Double financing) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.financing = financing;
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

    public Double getFinancing() {
        return financing;
    }

    public void setFinancing(Double financing) {
        this.financing = financing;
    }
}
