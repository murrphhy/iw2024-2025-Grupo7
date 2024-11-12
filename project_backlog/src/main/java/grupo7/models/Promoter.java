package grupo7.models;

public class Promoter extends User {
    private int importance;

    public Promoter() {}
    public Promoter(String name, String email, String password, boolean isAdmin, int importance) {
        super(name, email, password, isAdmin);
        this.importance = importance;
    }
}
