package grupo7.models;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Represents a project in the system.
 * Each project is associated with an applicant and may involve a promoter.
 */
@Entity
@Table(name = "project")
public class Project {

    /**
     * Unique identifier for the project. Generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the project. Cannot be null and has a maximum length of 100 characters.
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Short title of the project. Cannot be null and has a maximum length of 50 characters.
     */
    @Column(name = "short_title", nullable = false, length = 50)
    private String shortTitle;

    /**
     * The applicant (user) who created the project.
     * This is a many-to-one relationship.
     */
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private AppUser applicantId;

    /**
     * The promoter ID associated with the project.
     * This field is optional and may be null.
     */
    @Column(name = "promoter_id")
    private String promoterId;

    /**
     * Current state of the project (e.g., "presented", "approved"). Cannot be null.
     */
    @Column(nullable = false)
    private String state;

    /**
     * Detailed memory or description of the project. This field can hold large text values.
     */
    @Column(columnDefinition = "TEXT")
    private String memory;

    /**
     * Project-specific regulations. This field can hold large text values.
     */
    @Column(name = "project_regulations", columnDefinition = "TEXT")
    private String projectRegulations;

    /**
     * Scope of the project (e.g., "local", "regional", "international"). Cannot be null.
     */
    @Column(nullable = false)
    private String scope;

    /**
     * Start date of the project. Cannot be null.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    /**
     * Technical specifications of the project. This field can hold large text values.
     */
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


    /**
     * Gets the unique identifier of the project.
     *
     * @return the project's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the project.
     *
     * @param id the new ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the applicant (user) who created the project.
     *
     * @return the applicant user
     */
    public AppUser getApplicantId() {
        return applicantId;
    }

    /**
     * Sets the applicant (user) who created the project.
     *
     * @param applicantId the new applicant user
     */
    public void setApplicantId(AppUser applicantId) {
        this.applicantId = applicantId;
    }

    /**
     * Gets the detailed memory or description of the project.
     *
     * @return the project's memory
     */
    public String getMemory() {
        return memory;
    }

    /**
     * Sets the detailed memory or description of the project.
     *
     * @param memory the new memory
     */
    public void setMemory(String memory) {
        this.memory = memory;
    }

    /**
     * Gets the project-specific regulations.
     *
     * @return the project's regulations
     */
    public String getProjectRegulations() {
        return projectRegulations;
    }

    /**
     * Sets the project-specific regulations.
     *
     * @param projectRegulations the new regulations
     */
    public void setProjectRegulations(String projectRegulations) {
        this.projectRegulations = projectRegulations;
    }

    /**
     * Gets the promoter ID associated with the project.
     *
     * @return the promoter ID
     */
    public String getPromoterId() {
        return promoterId;
    }

    /**
     * Sets the promoter ID associated with the project.
     *
     * @param promoterId the new promoter ID
     */
    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    /**
     * Gets the scope of the project.
     *
     * @return the project's scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the scope of the project.
     *
     * @param scope the new scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Gets the short title of the project.
     *
     * @return the project's short title
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * Sets the short title of the project.
     *
     * @param shortTitle the new short title
     */
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    /**
     * Gets the start date of the project.
     *
     * @return the project's start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the project.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the current state of the project.
     *
     * @return the project's state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the current state of the project.
     *
     * @param state the new state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the technical specifications of the project.
     *
     * @return the project's technical specifications
     */
    public String getTechnicalSpecifications() {
        return technicalSpecifications;
    }

    /**
     * Sets the technical specifications of the project.
     *
     * @param technicalSpecifications the new technical specifications
     */
    public void setTechnicalSpecifications(String technicalSpecifications) {
        this.technicalSpecifications = technicalSpecifications;
    }

    /**
     * Gets the title of the project.
     *
     * @return the project's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the project.
     *
     * @param title the new title
     */
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
