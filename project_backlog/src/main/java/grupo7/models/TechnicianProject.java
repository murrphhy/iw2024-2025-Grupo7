package grupo7.models;

import grupo7.models.keys.TechnicianProjectId;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Represents the relationship between a technician (user) and a project.
 * This entity includes information about the technician's appraisal of the project.
 */
@Entity
@Table(name = "technician_project")
@IdClass(TechnicianProjectId.class) // Indicates the use of a composite key
public class TechnicianProject {

    /**
     * ID of the technician (user) associated with the project.
     */
    @Id
    private Long user_id;

    /**
     * ID of the project associated with the technician.
     * This ID is unique and cannot be null.
     */
    @Id
    @Column(unique = true, nullable = false)
    private Long project_id;

    /**
     * Appraisal value provided by the technician for the project.
     * Cannot be null.
     */
    @Column(nullable = false)
    private Double projectAppraisal;

    /**
     * Number of human resources estimated for the project.
     * Indicates the required personnel for successful execution.
     */
    @Column(nullable = false)
    private int humanResources;

    /**
     * Financial resources estimated for the project.
     * Represents the monetary budget required.
     * Stored as a {@link BigDecimal} to ensure precision for financial calculations.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal financialResources;

    /**
     * Description of the technical resources required for the project.
     * Could include hardware, software, or specialized tools.
     */
    @Column(nullable = false)
    private String technicalResources;

    /**
     * Default constructor required by JPA.
     */
    public TechnicianProject() {
    }

    /**
     * Constructs a new TechnicianProject instance with the specified details.
     *
     * @param user_id            the ID of the technician (user)
     * @param project_id         the ID of the project
     * @param projectAppraisal   the appraisal value given by the technician
     * @param humanResources     the estimated human resources required
     * @param financialResources the estimated financial resources required
     * @param technicalResources the description of technical resources required
     */
    public TechnicianProject(Long user_id, Long project_id, Double projectAppraisal, int humanResources, BigDecimal financialResources, String technicalResources) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.projectAppraisal = projectAppraisal;
        this.humanResources = humanResources;
        this.financialResources = financialResources;
        this.technicalResources = technicalResources;
    }

    /**
     * Gets the ID of the technician (user) associated with the project.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return user_id;
    }

    /**
     * Sets the ID of the technician (user) associated with the project.
     *
     * @param user_id the new user ID
     */
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets the ID of the project associated with the technician.
     *
     * @return the project ID
     */
    public Long getProjectId() {
        return project_id;
    }

    /**
     * Sets the ID of the project associated with the technician.
     *
     * @param project_id the new project ID
     */
    public void setProject(Long project_id) {
        this.project_id = project_id;
    }

    /**
     * Gets the appraisal value provided by the technician for the project.
     *
     * @return the appraisal value
     */
    public Double getProjectAppraisal() {
        return projectAppraisal;
    }

    /**
     * Sets the appraisal value provided by the technician for the project.
     *
     * @param projectAppraisal the new appraisal value
     */
    public void setProjectAppraisal(Double projectAppraisal) {
        this.projectAppraisal = projectAppraisal;
    }

    /**
     * Gets the estimated number of human resources required for the project.
     *
     * @return the number of human resources
     */
    public int getHumanResources() {
        return humanResources;
    }

    /**
     * Sets the estimated number of human resources required for the project.
     *
     * @param humanResources the new number of human resources
     */
    public void setHumanResources(int humanResources) {
        this.humanResources = humanResources;
    }

    /**
     * Gets the estimated financial resources required for the project.
     *
     * @return the financial resources as a {@link BigDecimal}
     */
    public BigDecimal getFinancialResources() {
        return financialResources;
    }

    /**
     * Sets the estimated financial resources required for the project.
     *
     * @param financialResources the new financial resources value
     */
    public void setFinancialResources(BigDecimal financialResources) {
        this.financialResources = financialResources;
    }

    /**
     * Gets the description of technical resources required for the project.
     *
     * @return the technical resources description
     */
    public String getTechnicalResources() {
        return technicalResources;
    }

    /**
     * Sets the description of technical resources required for the project.
     *
     * @param technicalResources the new technical resources description
     */
    public void setTechnicalResources(String technicalResources) {
        this.technicalResources = technicalResources;
    }
}
