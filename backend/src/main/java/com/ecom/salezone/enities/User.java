package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "users")
public class User implements UserDetails {
    /**
     * Primary key for User
     * UUID or custom generated ID is preferred
     */
    @Id
    @Column(name = "u_id")
    private String userId;

    /**
     * User display name / full name
     */
    @Column(name = "u_username")
    private String userName;

    /**
     * Email used for login
     * Must be unique across the system
     */
    @Column(name = "u_email", unique = true)
    private String email;

    /**
     * Encrypted password (BCrypt recommended)
     * Length increased to store hashed value
     */
    @Column(name = "u_password", length = 500)
    private String password;

    /**
     * Gender of user (MALE / FEMALE / OTHER)
     * Can later be converted to enum
     */
    @Column(name = "u_gender")
    private String gender;

    /**
     * Short bio/about section for user profile
     */
    @Column(name = "u_about", length = 1000)
    private String about;

    /**
     * Profile image file name stored in server / cloud
     */
    @Column(name = "u_imagename")
    private String imageName;

    /**
     * Phone number for contact & OTP verification
     */
    @Column(name = "u_phone", length = 15)
    private String phoneNumber;

    /**
     * Indicates whether user account is active or blocked
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Indicates whether email is verified
     */
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    /**
     * Timestamp when user was created
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when user was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One user can place multiple orders
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    /**
     * User roles (ADMIN, USER, SELLER, etc.)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "u_id"),
            inverseJoinColumns = @JoinColumn(name = "r_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Each user has exactly one cart
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    /**
     * Automatically sets createdAt before insert
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically updates updatedAt before update
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();
    }

    @Override
    public String getUsername() {
        // Spring Security uses this for login
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // you are not tracking expiry
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(this.isActive);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.isActive);
    }

}
