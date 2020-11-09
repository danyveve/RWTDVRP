package com.marcu.vrp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class RegisterUserDTO  implements Serializable {
    private String username;

    private String email;

    private String password;

    private String passwordConfirmation;

    private String phone;

    private String firstName;

    private String lastName;

    private Long companyId;
}
