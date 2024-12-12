package grupo7.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class StakeholderProjectId implements Serializable {

    private Long user_id;   // user_id de la relación
    private Long project_id; // project_id de la relación

    // Constructor vacío (requerido para Serializable)
    public StakeholderProjectId() {
    }

    // Constructor completo
    public StakeholderProjectId(Long user_id, Long project_id) {
        this.user_id = user_id;
        this.project_id = project_id;
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

    // Métodos equals y hashCode (requeridos para claves compuestas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StakeholderProjectId that = (StakeholderProjectId) o;
        return Objects.equals(user_id, that.user_id) && Objects.equals(project_id, that.project_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, project_id);
    }
}
