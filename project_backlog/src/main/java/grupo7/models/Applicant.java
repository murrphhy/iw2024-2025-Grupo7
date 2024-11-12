package grupo7.models;

import javax.persistence.Entity;

@Entity
public class Applicant extends User{

    private String unit;

    public Applicant() {}

    public Applicant(String name, String email, String password, boolean isAdmin, String unit) {
        super(name, email, password, isAdmin);
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
