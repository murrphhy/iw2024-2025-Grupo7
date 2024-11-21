package grupo7.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class SupportId implements Serializable {
    
    private Long userId;
    private Long projectId;

    // Constructor vacío (necesario para JPA)
    public SupportId() {
    }

    // Constructor con los dos campos
    public SupportId(Long userId, Long projectId) {
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

    // hashCode y equals para que funcione correctamente con claves compuestas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupportId supportId = (SupportId) o;
        return Objects.equals(userId, supportId.userId) && 
               Objects.equals(projectId, supportId.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId);
    }
}
