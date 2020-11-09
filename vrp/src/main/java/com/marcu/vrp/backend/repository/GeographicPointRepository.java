package com.marcu.vrp.backend.repository;

import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.model.GeographicPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface GeographicPointRepository extends JpaRepository<GeographicPoint, Long>, JpaSpecificationExecutor<GeographicPoint> {
    Optional<GeographicPoint> findByAddressAndLatitudeAndLongitude(String address, Double latitude, Double longitude);
}
