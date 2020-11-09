package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.GeographicPoint;
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
public class RWTDVRPRequestObject implements Serializable {
    private String id;
    private GeographicPointDTO depot;
    private List<GeographicPointDTO> deliveryPoints;
    private Integer numberOfDrivers;
    private Instant preferredDepartureTime;
}
