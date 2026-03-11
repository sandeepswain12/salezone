package com.ecom.salezone.dtos;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoleDto implements Serializable {

    /**
     * Unique role identifier
     * Example: ROLE_USER, ROLE_ADMIN
     */
    private String roleId;

    /**
     * Human readable role name
     * Example: USER, ADMIN
     */
    private String roleName;
}

