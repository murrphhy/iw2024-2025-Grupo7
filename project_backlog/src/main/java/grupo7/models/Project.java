package grupo7.models;

import javax.persistence.*;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Clave foránea en la tabla de proyectos
    private Support support;

    // Constructores, getters y setters
    public Project() {}

    public Project(String name, String description, Support support) {
        this.name = name;
        this.description = description;
        this.support = support;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Support getApplicant() {
        return support;
    }

    public void setApplicant(Support support) {
        this.support = support;
    }
}

