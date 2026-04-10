package com.ecom.salezone.enities;

import com.ecom.salezone.enums.Provider;
import jakarta.persistence.*;
//import lombok.*;
import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
@Entity
//@ToString
@Builder
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
    @Column(name = "u_is_active")
    private Boolean isActive = true;

    @Column(name = "u_provider")
    @Enumerated(EnumType.STRING)
    private Provider provider = Provider.LOCAL;

    @Column(name = "u_provider_id")
    private  String providerId;

    /**
     * Indicates whether email is verified
     */
    @Column(name = "u_email_verified")
    private Boolean emailVerified = false;

    /**
     * Timestamp when user was created
     */
    @Column(name = "u_created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when user was last updated
     */
    @Column(name = "u_updated_at")
    private LocalDateTime updatedAt;

    /**
     * One user can place multiple orders
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    /**
     * User roles (ADMIN, USER, SELLER, etc.)
     */
    @Builder.Default
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

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Address> addresses = new ArrayList<>();

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

    public User() {
    }

    public User(String userId, String userName, String email, String password, String gender, String about, String imageName, String phoneNumber, Boolean isActive, Provider provider, String providerId, Boolean emailVerified, LocalDateTime createdAt, LocalDateTime updatedAt, List<Order> orders, Set<Role> roles, Cart cart, List<RefreshToken> refreshTokens, List<Address> addresses) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.about = about;
        this.imageName = imageName;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.provider = provider;
        this.providerId = providerId;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orders = orders;
        this.roles = roles;
        this.cart = cart;
        this.refreshTokens = refreshTokens;
        this.addresses = addresses;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    public List<RefreshToken> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
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

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", about='" + about + '\'' +
                ", imageName='" + imageName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isActive=" + isActive +
                ", provider=" + provider +
                ", providerId='" + providerId + '\'' +
                ", emailVerified=" + emailVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", orders=" + orders +
                ", roles=" + roles +
                ", cart=" + cart +
                ", refreshTokens=" + refreshTokens +
                ", addresses=" + addresses +
                '}';
    }
}
