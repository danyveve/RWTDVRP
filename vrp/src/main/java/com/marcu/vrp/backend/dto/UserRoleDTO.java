package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.UserRoleName;
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
public class UserRoleDTO extends BaseIdDTO<Long> {
    private UserRoleName name;
}
