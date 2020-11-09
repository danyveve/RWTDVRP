package com.marcu.vrp.backend.dto;

import com.marcu.vrp.backend.model.GeographicPoint;
import com.marcu.vrp.backend.model.Route;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Builder
public class GeographicPointToRouteAssignmentDTO implements Serializable {
    private GeographicPointDTO geographicPoint;
    private RouteDTO route;
    private Long indexInRoute;
}
