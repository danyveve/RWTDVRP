package marcu.rwtdvrpapi.api.adapter;

import marcu.rwtdvrpapi.api.domain.Route;

import java.time.Instant;

public interface VrpToProviderAdapter{
    Route evaluate(Route route, Instant departureTime);
}
