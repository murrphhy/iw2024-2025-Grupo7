package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Technician")
public class Technician extends User {

    private String technicalArea;

    public Technician() {}

    public Technician(String name, String email, String password, String position, boolean isAdmin, String technicalArea) {
        super(name, email, password, position, isAdmin);
        this.technicalArea = technicalArea;
    }

    // Getters y Setters
    public String getTechnicalArea() {
        return technicalArea;
    }

    public void setTechnicalArea(String technicalArea) {
        this.technicalArea = technicalArea;
    }
}
