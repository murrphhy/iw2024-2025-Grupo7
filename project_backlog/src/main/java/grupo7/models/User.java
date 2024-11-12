package grupo7.models;

import javax.persistence.*;

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String email;
    private boolean isAdmin;

    public User(){}

    public User(String name, String password, String email, boolean isAdmin) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return name;
    }
    public String getEmail(){
        return email;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public String getPassword() {
        return password;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
