package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class VrpInstanceDTO extends BaseIdDTO<Long>{
    private Long userId;
    private Long depotId;
    private GeographicPointDTO depot;
    private Instant createdOn;
    private User user;
    private Instant preferredDepartureTime;
    private Instant suggestedDepartureTime;
    private Long totalCost;
    @Builder.Default
    private Set<VrpDeliveryPointDTO> deliveryPoints = new HashSet<>();
    @Builder.Default
    private Set<RouteDTO> routes = new HashSet<>();
}
