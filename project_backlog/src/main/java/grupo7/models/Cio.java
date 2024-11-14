package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CIO")
public class Cio extends User {

    private String cioPosition;

    public Cio() {}

    public Cio(String name, String email, String password, String userPosition, boolean isAdmin, String cioPosition) {
        super(name, email, password, userPosition, isAdmin);
        this.cioPosition = cioPosition;
    }

    // Getters y Setters
    public String getPosition() {
        return cioPosition;
    }

    public void setPosition(String position) {
        this.cioPosition = position;
    }
}
