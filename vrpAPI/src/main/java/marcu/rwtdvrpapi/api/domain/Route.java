package marcu.rwtdvrpapi.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
@Builder
public class Route {
    private List<GeographicPoint> route;
    private Long cost;

    public Route(Route toBeCopied) {
        route = new ArrayList<>(toBeCopied.getRoute());
        cost = toBeCopied.getCost();
    }
}
