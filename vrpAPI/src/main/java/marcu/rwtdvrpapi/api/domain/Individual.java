package marcu.rwtdvrpapi.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
@Builder
public class Individual {
    private List<Route> routes;
    private Long totalCost;
    private Instant departureTime;
    private Double crossoverRate;
    private Double mutationRate;
    private Integer crossoverOperator;
    private Integer mutationOperator;

    public Individual(Individual individual) {
        List<Route> copiedRoutes = new ArrayList<>();
        individual.getRoutes().forEach(route -> copiedRoutes.add(new Route(route)));
        routes = copiedRoutes;
        totalCost = individual.getTotalCost();
        departureTime = individual.getDepartureTime();
        crossoverRate = individual.getCrossoverRate();
        mutationRate = individual.getMutationRate();
        crossoverOperator = individual.getCrossoverOperator();
        mutationOperator = individual.getMutationOperator();
    }
}
