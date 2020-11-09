package marcu.rwtdvrpapi.api.adapter;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest.Waypoint;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import marcu.rwtdvrpapi.api.domain.GeographicPoint;
import marcu.rwtdvrpapi.api.domain.Route;
import marcu.rwtdvrpapi.api.utils.MyPair;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GoogleMapsApiAdapter implements VrpToProviderAdapter {

    private GeoApiContext geoApiContext;
    Map<MyPair<Set<GeographicPoint>, Instant>, Route> cache = new ConcurrentHashMap<>();

    public GoogleMapsApiAdapter(String googleMapsApiKey) {
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(googleMapsApiKey)
                .build();
    }

    @Override
    public Route evaluate(Route route, Instant departureTime) {
        Route optimizedAndEvaluatedRoute = new Route();

        MyPair<Set<GeographicPoint>, Instant> possibleCacheEntry = new MyPair<>(new HashSet<>(route.getRoute()), departureTime);
        if (cache.keySet().contains(possibleCacheEntry)) {
            //System.out.println("<<<<<<<<<<<<<<<<<< CACHE CACHE BABY >>>>>>>>>>>>>>>>>>");
            Route cachedResult = cache.get(possibleCacheEntry);
            optimizedAndEvaluatedRoute.setRoute(new ArrayList<>(cachedResult.getRoute()));
            optimizedAndEvaluatedRoute.setCost(cachedResult.getCost());
        } else {
            try {
                GeographicPoint origin = route.getRoute().get(0);
                GeographicPoint destination = route.getRoute().get(route.getRoute().size() - 1);

                List<Waypoint> waypoints = new ArrayList<>();
                route.getRoute().subList(1, route.getRoute().size() - 1).forEach(waypoint -> {
                    waypoints.add(new Waypoint(new LatLng(waypoint.getLatitude(), waypoint.getLongitude()), true));
                });

                //first get the optimized waypoints order
                int[] optimizedWaypointsOrder = optimizedWaypointsOrder(origin, destination, waypoints, departureTime);

                //rearange the waypoints based on the optimized order
                List<Waypoint> optimizedWaypoints = new ArrayList<>();
                List<GeographicPoint> optimizedRoute = new ArrayList<>();
                optimizedRoute.add(origin);
                for (int i = 0; i < optimizedWaypointsOrder.length; i++) {
                    int idx = optimizedWaypointsOrder[i] + 1; //add +1 since the first geographic point in the route is the origin
                    GeographicPoint pointAtIdx = route.getRoute().get(idx);
                    optimizedWaypoints.add(new Waypoint(new LatLng(pointAtIdx.getLatitude(), pointAtIdx.getLongitude()), false));
                    optimizedRoute.add(pointAtIdx);
                }
                optimizedRoute.add(destination);

                //request cost based on traffic for this optimized order of waypoints
                Long costBasedOnTrafficAndTime = calculateRouteCostBasedOnTraffic(origin, destination, optimizedWaypoints, departureTime);

                //set optimized route and time and traffic dependent cost
                optimizedAndEvaluatedRoute.setRoute(optimizedRoute);
                optimizedAndEvaluatedRoute.setCost(costBasedOnTrafficAndTime);


            } catch (ApiException | InterruptedException | IOException e) {
                System.out.println("Error in communicating with GMAPS API: -> GA will probably stop...");
                e.printStackTrace();
            }

            //randomized values for testing
//            optimizedAndEvaluatedRoute.setRoute(new ArrayList<>(route.getRoute()));
//            optimizedAndEvaluatedRoute.setCost((long) Utils.getRandomIntegerInRange(10,100));


            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! not cached !!!!!!!!!!!!!!!!!!!!!!!!!!!");
            cache.putIfAbsent(possibleCacheEntry, new Route(optimizedAndEvaluatedRoute));
        }

        return optimizedAndEvaluatedRoute;
    }

    private int[] optimizedWaypointsOrder(GeographicPoint origin, GeographicPoint destination, List<Waypoint> waypoints, Instant departureTime) throws InterruptedException, ApiException, IOException {
        DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                .origin(new LatLng(origin.getLatitude(), origin.getLongitude()))
                .destination(new LatLng(destination.getLatitude(), destination.getLongitude()))
                .waypoints(waypoints.toArray(new Waypoint[0]))
                .optimizeWaypoints(true)

                .mode(TravelMode.DRIVING)
                .trafficModel(TrafficModel.PESSIMISTIC)
                .departureTime(departureTime)
                .await();

        return result.routes[0].waypointOrder;
    }

    private Long calculateRouteCostBasedOnTraffic(GeographicPoint origin, GeographicPoint destination, List<Waypoint> waypoints, Instant departureTime) throws InterruptedException, ApiException, IOException {
        DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                .origin(new LatLng(origin.getLatitude(), origin.getLongitude()))
                .destination(new LatLng(destination.getLatitude(), destination.getLongitude()))
                .waypoints(waypoints.toArray(new Waypoint[0]))
                .optimizeWaypoints(true)

                .mode(TravelMode.DRIVING)
                .trafficModel(TrafficModel.PESSIMISTIC)
                .departureTime(departureTime)
                .await();

        Duration durationInTraffic = result.routes[0].legs[0].durationInTraffic;
        if (durationInTraffic != null) {
            return durationInTraffic.inSeconds;
        } else {
            return result.routes[0].legs[0].duration.inSeconds;
        }
    }
}
