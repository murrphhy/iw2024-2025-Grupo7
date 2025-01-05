package grupo7.models;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Represents a project in the system.
 * Each project is associated with an applicant and may involve a promoter.
 * The memory of the project is stored as a binary large object (BLOB) to accommodate PDF files.
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
    @Column(name = "state")
    private String state;

    /**
     * Detailed memory or description of the project, stored as a binary large object (BLOB).
     * This field is designed to accommodate PDF files.
     */
    @Lob
    @Column(name = "memory", columnDefinition = "LONGBLOB")
    private byte[] memory;

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

    /**
     * Strategic alignment score of the project.
     */
    @Column(name = "strategic_alignment")
    private Double strategicAlignment;

    /**
     * Technical suitability score of the project.
     */
    @Column(name = "technical_suitability")
    private Double technicalSuitability;

    /**
     * Available resources score for the project.
     */
    @Column(name = "available_resources")
    private Double availableResources;

    // Getters and Setters with detailed documentation

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
     * Gets the detailed memory or description of the project as a binary file.
     *
     * @return the memory as a byte array
     */
    public byte[] getMemory() {
        return memory;
    }

    /**
     * Sets the detailed memory or description of the project as a binary file.
     *
     * @param memory the new memory as a byte array
     */
    public void setMemory(byte[] memory) {
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
     * Gets the strategic alignment score of the project.
     *
     * @return the strategic alignment score
     */
    public Double getStrategicAlignment() {
        return strategicAlignment;
    }

    /**
     * Sets the strategic alignment score of the project.
     *
     * @param strategicAlignment the new strategic alignment score
     */
    public void setStrategicAlignment(Double strategicAlignment) {
        this.strategicAlignment = strategicAlignment;
    }

    /**
     * Gets the technical suitability score of the project.
     *
     * @return the technical suitability score
     */
    public Double getTechnicalSuitability() {
        return technicalSuitability;
    }

    /**
     * Sets the technical suitability score of the project.
     *
     * @param technicalSuitability the new technical suitability score
     */
    public void setTechnicalSuitability(Double technicalSuitability) {
        this.technicalSuitability = technicalSuitability;
    }

    /**
     * Gets the available resources score of the project.
     *
     * @return the available resources score
     */
    public Double getAvailableResources() {
        return availableResources;
    }

    /**
     * Sets the available resources score of the project.
     *
     * @param availableResources the new available resources score
     */
    public void setAvailableResources(Double availableResources) {
        this.availableResources = availableResources;
    }
}
