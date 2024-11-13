package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CIO")
public class Cio extends Users {

    private String position;

    public Cio() {}

    public Cio(String name, String email, String password, boolean isAdmin, String position) {
        super(name, email, password, isAdmin);
        this.position = position;
    }

    // Getters y Setters
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
