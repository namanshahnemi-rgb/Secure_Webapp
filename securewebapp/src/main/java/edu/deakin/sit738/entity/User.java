package edu.deakin.sit738.entity;

import javax.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;
    private String email;

    public User() {}
    public User(String username, String password, String role, String email){
        this.username=username; this.password=password; this.role=role; this.email=email;
    }

    public Long getId(){return id;}
    public String getUsername(){return username;}
    public void setUsername(String u){this.username=u;}
    public String getPassword(){return password;}
    public void setPassword(String p){this.password=p;}
    public String getRole(){return role;}
    public void setRole(String r){this.role=r;}
    public String getEmail(){return email;}
    public void setEmail(String e){this.email=e;}
}
