package grupo7.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class StakeholderProjectId implements Serializable {

    private Long userId;   // user_id de la relación
    private Long projectId; // project_id de la relación

    // Constructor vacío (requerido para Serializable)
    public StakeholderProjectId() {
    }

    // Constructor completo
    public StakeholderProjectId(Long userId, Long projectId) {
        this.userId = userId;
        this.projectId = projectId;
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

    // Métodos equals y hashCode (requeridos para claves compuestas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StakeholderProjectId that = (StakeholderProjectId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId);
    }
}
