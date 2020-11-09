package com.marcu.vrp.backend.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vrp_instance")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class VrpInstance extends BaseIdEntity<Long> {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false, nullable = false)
    private Instant createdOn;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "depot_id", nullable = false)
    private Long depotId;

    @ManyToOne
    @JoinColumn(name = "depot_id", insertable = false, updatable = false)
    private GeographicPoint depot;

    @Column(name = "preferred_departure_time", nullable = false)
    private Instant preferredDepartureTime;

    @Column(name = "suggested_departure_time")
    private Instant suggestedDepartureTime;

    @Column(name = "total_cost")
    private Long totalCost;

    @Builder.Default
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "vrpInstance", fetch = FetchType.EAGER)
    private Set<VrpDeliveryPoint> deliveryPoints = new HashSet<>();

    @Builder.Default
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "vrpInstance", fetch = FetchType.EAGER)
    private List<Route> routes = new ArrayList<>();

}
