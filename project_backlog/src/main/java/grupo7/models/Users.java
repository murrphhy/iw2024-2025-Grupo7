package grupo7.models;

import javax.persistence.*;

@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private boolean isAdmin;

    public Users() {}

    public Users(String name, String email, String password, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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
