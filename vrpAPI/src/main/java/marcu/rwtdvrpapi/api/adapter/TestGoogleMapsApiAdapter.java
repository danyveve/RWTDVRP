package marcu.rwtdvrpapi.api.adapter;

import marcu.rwtdvrpapi.api.domain.Route;
import marcu.rwtdvrpapi.api.utils.Utils;

import java.time.Instant;
import java.util.ArrayList;

public class TestGoogleMapsApiAdapter implements VrpToProviderAdapter {
    @Override
    public Route evaluate(Route route, Instant departureTime) {
        Route optimizedAndEvaluatedRoute = new Route();
        optimizedAndEvaluatedRoute.setRoute(new ArrayList<>(route.getRoute()));
        optimizedAndEvaluatedRoute.setCost((long) Utils.getRandomIntegerInRange(10,100));
        return optimizedAndEvaluatedRoute;
    }
}
