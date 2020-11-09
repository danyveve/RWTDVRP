package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.BaseIdEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class DriverDTO extends BaseIdDTO<Long> {
    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String car;
}
