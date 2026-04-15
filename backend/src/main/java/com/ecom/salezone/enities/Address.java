package com.ecom.salezone.enities;

import com.ecom.salezone.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @Column(name = "a_id")
    private String id;

    @Column(name = "a_name")
    private String name;

    @Column(name = "a_mobile")
    private String mobile;

    @Column(name = "a_pincode")
    private String pincode;

    @Column(name = "a_city")
    private String city;

    @Column(name = "a_state")
    private String state;

    @Column(name = "a_full_address")
    private String fullAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "a_address_type")
    private AddressType addressType;

    @Column(name = "a_is_default")
    private Boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "u_id")
    private User user;

    @Column(name = "a_created_at")
    private LocalDateTime createdAt;

    @Column(name = "a_updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}