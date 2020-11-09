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
@Table(name = "delivery_point")
@IdClass(VrpDeliveryPoint.VrpDeliveryPointPK.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VrpDeliveryPoint implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "geographic_point_id", referencedColumnName = "id", nullable = false)
    private GeographicPoint geographicPoint;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vrp_instance_id", referencedColumnName = "id", nullable = false)
    private VrpInstance vrpInstance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VrpDeliveryPoint)) return false;
        VrpDeliveryPoint that = (VrpDeliveryPoint) o;
        return Objects.equals(getGeographicPoint().getId(), that.getGeographicPoint().getId()) &&
                Objects.equals(getVrpInstance().getId(), that.getVrpInstance().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeographicPoint().getId(), getVrpInstance().getId());
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class VrpDeliveryPointPK implements Serializable {
        GeographicPoint geographicPoint;
        VrpInstance vrpInstance;
    }
}
