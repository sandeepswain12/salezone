package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    private String id;

    // User Mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Address Fields
    private String fullName;
    private String phoneNumber;

    private String addressLine1;
    private String addressLine2;

    private String city;
    private String state;
    private String pincode;

    private String landmark;

    // Default address
    private Boolean isDefault = false;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}