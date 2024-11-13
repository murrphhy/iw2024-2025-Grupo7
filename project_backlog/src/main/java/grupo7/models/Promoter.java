package grupo7.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Promoter")
public class Promoter extends Users {

    private int importance;

    public Promoter() {}

    public Promoter(String name, String email, String password, boolean isAdmin, int importance) {
        super(name, email, password, isAdmin);
        this.importance = importance;
    }

    // Getters y Setters
    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}