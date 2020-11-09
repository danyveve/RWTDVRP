package com.marcu.vrp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class UserDTO extends BaseIdDTO<Long>{
    private String username;

    private String email;

    private String password;

    private String phone;

    private String firstName;

    private String lastName;

    private Long roleId;

    private UserRoleDTO role;
}
