package grupo7.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private AppUser applicantId;

    @Column(columnDefinition = "TEXT")
    private String memory;

    @Column(name = "project_regulations", columnDefinition = "TEXT")
    private String projectRegulations;

    @Column(name = "promoter_id", nullable = false)
    private Long promoterId;

    @Column(nullable = false)
    private String scope;

    @Column(name = "short_title", nullable = false, length = 50)
    private String shortTitle;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private String state;

    @Column(name = "technical_specifications", columnDefinition = "TEXT")
    private String technicalSpecifications;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "strategic_alignment")
    private Double strategicAlignment;

    @Column(name = "technical_suitability")
    private Double technicalSuitability;

    @Column(name = "available_resources")
    private Double availableResources;

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

    public Double getStrategicAlignment() {
        return strategicAlignment;
    }

    public void setStrategicAlignment(Double strategicAlignment) {
        this.strategicAlignment = strategicAlignment;
    }

    public Double getTechnicalSuitability() {
        return technicalSuitability;
    }

    public void setTechnicalSuitability(Double technicalSuitability) {
        this.technicalSuitability = technicalSuitability;
    }

    public Double getAvailableResources() {
        return availableResources;
    }

    public void setAvailableResources(Double availableResources) {
        this.availableResources = availableResources;
    }

}
