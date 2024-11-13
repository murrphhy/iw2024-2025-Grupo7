package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Applicant")
public class Applicant extends Users {

    private String unit;

    public Applicant() {}

    public Applicant(String name, String email, String password, boolean isAdmin, String unit) {
        super(name, email, password, isAdmin);
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