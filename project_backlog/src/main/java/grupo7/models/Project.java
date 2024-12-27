package grupo7.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "short_title", nullable = false, length = 50)
    private String shortTitle;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private AppUser applicantId;

    @Column(name = "promoter_id")
    private Long promoterId;

    @Column(nullable = false)
    private String state;

    @Column(columnDefinition = "TEXT")
    private String memory;

    @Column(name = "project_regulations", columnDefinition = "TEXT")
    private String projectRegulations;

    @Column(nullable = false)
    private String scope;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;



    @Column(name = "technical_specifications", columnDefinition = "TEXT")
    private String technicalSpecifications;



    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(AppUser applicantId) {
        this.applicantId = applicantId;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getProjectRegulations() {
        return projectRegulations;
    }

    public void setProjectRegulations(String projectRegulations) {
        this.projectRegulations = projectRegulations;
    }

    public Long getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Long promoterId) {
        this.promoterId = promoterId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTechnicalSpecifications() {
        return technicalSpecifications;
    }

    public void setTechnicalSpecifications(String technicalSpecifications) {
        this.technicalSpecifications = technicalSpecifications;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
