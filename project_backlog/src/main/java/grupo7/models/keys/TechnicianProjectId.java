package grupo7.models.keys;

import java.io.Serializable;
import java.util.Objects;

public class TechnicianProjectId implements Serializable {

    private Long user_id;
    private Long project_id;

    // Getters and Setters

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechnicianProjectId that = (TechnicianProjectId) o;
        return Objects.equals(user_id, that.user_id) && Objects.equals(project_id, that.project_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, project_id);
    }
}
