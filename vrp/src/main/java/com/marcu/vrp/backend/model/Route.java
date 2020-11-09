package com.marcu.vrp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "route",
        uniqueConstraints = {@UniqueConstraint(name = "uniq_driver_and_vrpinstance", columnNames = {"driver_id", "vrp_instance_id"})}
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Route extends BaseIdEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vrp_instance_id", referencedColumnName = "id", nullable = false)
    private VrpInstance vrpInstance;

    @Column(name = "cost")
    private Long cost;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "route", fetch = FetchType.EAGER)
    private Set<GeographicPointToRouteAssignment> geographicPointToRouteAssignments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        if (!super.equals(o)) return false;
        Route route = (Route) o;
        return Objects.equals(getDriver().getId(), route.getDriver().getId()) &&
                Objects.equals(getVrpInstance().getId(), route.getVrpInstance().getId()) &&
                Objects.equals(getCost(), route.getCost()) &&
                Objects.equals(getGeographicPointToRouteAssignments(), route.getGeographicPointToRouteAssignments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDriver().getId(), getVrpInstance().getId(), getCost(), getGeographicPointToRouteAssignments());
    }
}
