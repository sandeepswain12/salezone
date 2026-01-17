package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "u_id")
    private String userId;
    @Column(name = "u_username")
    private String userName;
    @Column(name = "u_email" , unique = true)
    private String email;
    @Column(name = "u_password",length = 500)
    private String password;
    @Column(name = "u_gender")
    private String gender;
    @Column(name = "u_about",length = 1000)
    private String about;
    @Column(name = "u_imagename")
    private String imageName;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "u_id"),
            inverseJoinColumns = @JoinColumn(name = "r_id")
    )
    private Set<Role> roles = new HashSet<>();
    @OneToOne(mappedBy = "user" , cascade = CascadeType.REMOVE)
    private Cart cart = new Cart();
}
