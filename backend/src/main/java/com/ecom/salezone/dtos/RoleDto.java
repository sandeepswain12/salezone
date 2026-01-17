package com.ecom.salezone.dtos;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {

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

