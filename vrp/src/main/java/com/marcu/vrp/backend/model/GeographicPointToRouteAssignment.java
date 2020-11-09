package com.marcu.vrp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "geographic_point_to_route_assignment")
@IdClass(GeographicPointToRouteAssignment.GeographicPointToRouteAssignmentPK.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GeographicPointToRouteAssignment implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "geographic_point_id", referencedColumnName = "id", nullable = false)
    private GeographicPoint geographicPoint;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", referencedColumnName = "id", nullable = false)
    private Route route;

    @Column(name = "index_in_route", nullable = false)
    private Long indexInRoute;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeographicPointToRouteAssignment)) return false;
        GeographicPointToRouteAssignment that = (GeographicPointToRouteAssignment) o;
        return Objects.equals(getGeographicPoint().getId(), that.getGeographicPoint().getId()) &&
                Objects.equals(getRoute().getId(), that.getRoute().getId()) &&
                Objects.equals(getIndexInRoute(), that.getIndexInRoute());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeographicPoint().getId(), getRoute().getId(), getIndexInRoute());
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class GeographicPointToRouteAssignmentPK implements Serializable {
        GeographicPoint geographicPoint;
        Route route;
    }
}
