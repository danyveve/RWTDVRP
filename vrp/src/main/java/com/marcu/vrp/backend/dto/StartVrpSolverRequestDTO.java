package com.marcu.vrp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class StartVrpSolverRequestDTO implements Serializable {
    private Long userId;
    private GeographicPointDTO depot;
    private List<GeographicPointDTO> deliveryPoints;
    private List<DriverDTO> drivers;
    private Instant preferredDepartureTime;
}
