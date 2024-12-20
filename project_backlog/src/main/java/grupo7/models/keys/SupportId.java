package grupo7.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class SupportId implements Serializable {
    
    private Long user_id;
    private Long project_id;

    // Constructor vacío (necesario para JPA)
    public SupportId() {
    }

    // Constructor con los dos campos
    public SupportId(Long user_id, Long project_id) {
        this.user_id = user_id;
        this.project_id = project_id;
    }

    // Getters y Setters
    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long userId) {
        this.user_id = userId;
    }

    public Long getProjectId() {
        return project_id;
    }

    public void setProjectId(Long projectId) {
        this.project_id = projectId;
    }

    // hashCode y equals para que funcione correctamente con claves compuestas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupportId supportId = (SupportId) o;
        return Objects.equals(user_id, supportId.user_id) && 
               Objects.equals(project_id, supportId.project_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, project_id);
    }
}
