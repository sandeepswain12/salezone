package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Primary key for Role
     * Example: ROLE_USER, ROLE_ADMIN
     */
    @Id
    @Column(name = "r_id")
    private String roleId;

    /**
     * Human readable role name
     * Example: USER, ADMIN
     */
    @Column(name = "r_name", nullable = false, unique = true)
    private String roleName;
}
