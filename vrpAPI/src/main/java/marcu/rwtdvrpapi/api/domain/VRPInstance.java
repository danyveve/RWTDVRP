package marcu.rwtdvrpapi.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
@Builder
public class VRPInstance {
    private String id;
    private GeographicPoint depot;
    private List<GeographicPoint> deliveryPoints;
    private Integer numberOfDrivers;
    private Instant preferredDepartureTime;
}
