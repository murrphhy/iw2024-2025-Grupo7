package grupo7.models;

import grupo7.models.keys.TechnicianProjectId;

import jakarta.persistence.*;

@Entity
@IdClass(TechnicianProjectId.class)  // Utiliza una clave compuesta
public class TechnicianProject {

    @Id
    private Long user_id;

    @Id
    @Column(unique = true, nullable = false)
    private Long project_id;

    @Column(nullable = false)
    private Integer projectAppraisal;


    // Constructor vacío
    public TechnicianProject() {
    }
    
    // Constructor completo
    public TechnicianProject(Long user_id, Long project_id, Integer projectAppraisal) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.projectAppraisal = projectAppraisal;
    }

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

    public void setProject(Long project_id) {
        this.project_id = project_id;
    }

    public Integer getProjectAppraisal() {
        return projectAppraisal;
    }

    public void setProjectAppraisal(Integer projectAppraisal) {
        this.projectAppraisal = projectAppraisal;
    }
}
