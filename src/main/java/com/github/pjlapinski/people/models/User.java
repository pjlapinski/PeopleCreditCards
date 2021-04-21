package com.github.pjlapinski.people.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue
    private long id;
    private String username;
    private String email;
    private String password;
    private boolean isActive;
    private boolean requestedAccountRemoved;
    private String role;

    @OneToMany(mappedBy = "createdBy", orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Person> people = new ArrayList<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        isActive = false;
        role = "ROLE_USER";
    }

    public User(String username, String email, String password, boolean isActive, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.role = role;
    }
}
