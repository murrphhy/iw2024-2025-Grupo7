package grupo7.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String shortTitle;
    private String memory;
    private String state;
    private String scope;
    private String projectRegulations;
    private String technicalSpecification;
    private Long applicantId;
    private Long promoterId;

    // Getters, Setters, and Constructors
}
