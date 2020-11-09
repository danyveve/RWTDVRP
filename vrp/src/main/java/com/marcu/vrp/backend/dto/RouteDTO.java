package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class RouteDTO extends BaseIdDTO<Long> {
    private DriverDTO driver;
    private VrpInstanceDTO vrpInstance;
    private Long cost;
    @Builder.Default
    private List<GeographicPointToRouteAssignmentDTO> geographicPointToRouteAssignments = new ArrayList<>();
}
