package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Applicant")
public class Applicant extends User {

    private String unit;

    public Applicant() {}

    public Applicant(String name, String email, String password, String position, boolean isAdmin, String unit) {
        super(name, email, password, position, isAdmin);
        this.unit = unit;
    }

    // Getters y Setters
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}