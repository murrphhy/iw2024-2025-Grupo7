package grupo7.models;

import grupo7.models.keys.TechnicianProjectId;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "technician_project")
@IdClass(TechnicianProjectId.class)
public class Technician_Project implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "project_appraisal")
    private Integer projectAppraisal;

    // Getters and Setters

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

    public Integer getProjectAppraisal() {
        return projectAppraisal;
    }

    public void setProjectAppraisal(Integer projectAppraisal) {
        this.projectAppraisal = projectAppraisal;
    }
}
