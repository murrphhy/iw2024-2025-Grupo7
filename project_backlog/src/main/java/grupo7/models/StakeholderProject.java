package grupo7.models;

import grupo7.models.keys.StakeholderProjectId;

import jakarta.persistence.*;

@Entity
@IdClass(StakeholderProjectId.class)
public class StakeholderProject {

    @Id
    private Long userId;

    @Id
    private Long projectId;

    private Double financing; // Campo adicional para la relación

    // Constructor vacío
    public StakeholderProject() {
    }

    // Constructor completo
    public StakeholderProject(Long userId, Long projectId, Double financing) {
        this.userId = userId;
        this.projectId = projectId;
        this.financing = financing;
    }

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Double getFinancing() {
        return financing;
    }

    public void setFinancing(Double financing) {
        this.financing = financing;
    }
}
