package marcu.rwtdvrpapi.api.service;

import marcu.rwtdvrpapi.api.adapter.GoogleMapsApiAdapter;
import marcu.rwtdvrpapi.api.adapter.VrpToProviderAdapter;
import marcu.rwtdvrpapi.api.domain.GeographicPoint;
import marcu.rwtdvrpapi.api.domain.Individual;
import marcu.rwtdvrpapi.api.domain.Population;
import marcu.rwtdvrpapi.api.domain.Route;
import marcu.rwtdvrpapi.api.domain.VRPInstance;
import marcu.rwtdvrpapi.api.utils.MyPair;
import marcu.rwtdvrpapi.api.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GAHelper {
    @Value("${rwtdvrp.ga.population-size}")
    public Integer populationSize;

    @Value("${rwtdvrp.ga.nr-crossover-operators}")
    public Integer nrCrossoverOperators;

    @Value("${rwtdvrp.ga.nr-mutation-operators}")
    public Integer nrMutationOperators;

    @Value("${rwtdvrp.ga.crossover.best-pick-load}")
    public Double crossoverBestPickLoad;

    @Value("${rwtdvrp.ga.mutation.random-remove-load}")
    public Double mutationRemoveLoad;

    @Value("${rwtdvrp.ga.crossover-rate}")
    public List<Double> crossoverRates;

    @Value("${rwtdvrp.ga.mutation-rate}")
    public List<Double> mutationRates;

    @Value("${rwtdvrp.ga.preffered-departure-time-window.minutes}")
    public List<Integer> departureTimeWindows;

    public VrpToProviderAdapter vrpToProviderAdapter;

    public GAHelper(@Value("${rwtdvrp.google-maps-api-key}") String googleMapsApiKey) {
        vrpToProviderAdapter = new GoogleMapsApiAdapter(googleMapsApiKey);
    }

    public void initializePopulation(VRPInstance vrpInstance, Population population) {
        population.setDepot(vrpInstance.getDepot());
        population.setNumberOfDrivers(vrpInstance.getNumberOfDrivers());
        Instant preferredDepartureTime = vrpInstance.getPreferredDepartureTime();
        population.setPreferredDepartureTime(preferredDepartureTime);

        List<Individual> individuals = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Individual individual = new Individual();

            //set mutation and crossover parameters
            individual.setCrossoverOperator(Utils.getRandomIntegerInRange(1, nrCrossoverOperators));
            individual.setMutationOperator(Utils.getRandomIntegerInRange(1, nrMutationOperators));
            individual.setCrossoverRate(Utils.getRandomElementFromCollection(crossoverRates));
            individual.setMutationRate(Utils.getRandomElementFromCollection(mutationRates));

            //set adapted departure time
            individual.setDepartureTime(preferredDepartureTime.plus(Utils.getRandomElementFromCollection(departureTimeWindows), ChronoUnit.MINUTES));

            //setRoutes
            setRoutes(vrpInstance, population, individual);

            //add the new individual to the population
            individuals.add(individual);
        }
        population.setIndividuals(individuals);

    }

    private void setRoutes(VRPInstance vrpInstance, Population population, Individual individual) {
        //shuffle the delivery points
        ArrayList<GeographicPoint> newDeliveryPoints = new ArrayList<>(vrpInstance.getDeliveryPoints());
        Collections.shuffle(newDeliveryPoints);
        //now randomly choose breakpoints for the upcoming routes
        List<Route> routes = createRoutesFromDeliveryPoints(population.getNumberOfDrivers(), newDeliveryPoints, population.getDepot());
        individual.setRoutes(routes);
    }

    private List<Route> createRoutesFromDeliveryPoints(Integer nrOfRoutes, List<GeographicPoint> deliveryPoints, GeographicPoint depot) {
        ArrayList<Route> routes = new ArrayList<>();
        if (nrOfRoutes == 1) {
            routes.add(createNewRouteBetweenIdxs(deliveryPoints, depot, new AtomicInteger(0), deliveryPoints.size()));
        } else {
            Set<Integer> randomEndOfRouteIndexesSet = new HashSet<>();
            while (randomEndOfRouteIndexesSet.size() < nrOfRoutes - 1) {
                randomEndOfRouteIndexesSet.add(Utils.getRandomIntegerInRange(1, deliveryPoints.size() - 1));
            }
            List<Integer> randomEndOfRouteIndexesList = new ArrayList<>(randomEndOfRouteIndexesSet);
            Collections.sort(randomEndOfRouteIndexesList);

            AtomicInteger startIdx = new AtomicInteger(0);
            randomEndOfRouteIndexesList.forEach(endIdx -> {
                routes.add(createNewRouteBetweenIdxs(deliveryPoints, depot, startIdx, endIdx));
                startIdx.set(endIdx);
            });
            //add last route
            routes.add(createNewRouteBetweenIdxs(deliveryPoints, depot, startIdx, deliveryPoints.size()));

        }

        return routes;
    }

    private Route createNewRouteBetweenIdxs(List<GeographicPoint> newDeliveryPoints, GeographicPoint depot, AtomicInteger startIdx, int endIdx) {
        Route route = new Route();
        List<GeographicPoint> slice = new ArrayList<>(newDeliveryPoints.subList(startIdx.get(), endIdx));
        slice.add(0, depot);
        slice.add(depot);
        route.setRoute(slice);
        return route;
    }

    public void evaluateIndividuals(List<Individual> individuals) {

        individuals.forEach(individual -> {
            individual.getRoutes().forEach(route -> {
                Route possiblyOptimizedRoute = vrpToProviderAdapter.evaluate(route, individual.getDepartureTime());
                route.setRoute(possiblyOptimizedRoute.getRoute());
                route.setCost(possiblyOptimizedRoute.getCost());
            });
            individual.setTotalCost(individual.getRoutes().stream().map(Route::getCost).reduce(0L, Long::sum));
        });

    }

    public List<Individual> selectForRecombination(Population population) {
        //Roulette Wheel Selection
        //compute sum of all of the costs
        Long totalPopulationCost = population.getIndividuals().stream().map(Individual::getTotalCost).reduce(0L, Long::sum);
        //Compute the direct proportional probabilities for each individual
        List<MyPair<Individual, Double>> individualsWithSelectionProbabilities =
                population.getIndividuals()
                        .stream()
                        .map(individual -> new MyPair<>(individual, 1.0 - ((double) individual.getTotalCost()) / totalPopulationCost))
                        //Sort the individuals by their probabilities of being chosen
                        .sorted(Comparator.comparing(MyPair::getRight))
                        .collect(Collectors.toList());
        //Scale the probabilities to reach upper limit 1
        for (int i = 1; i < individualsWithSelectionProbabilities.size(); i++) {
            MyPair<Individual, Double> currentIndividual = individualsWithSelectionProbabilities.get(i);
            MyPair<Individual, Double> previousIndividual = individualsWithSelectionProbabilities.get(i - 1);
            currentIndividual.setRight(currentIndividual.getRight() + previousIndividual.getRight());
        }
        //generate a random number and select candidates for recombination accordingly
        List<Individual> selectedForRecombination = new ArrayList<>();
        double universalProbability = Utils.getRandomDoubleInRange(0, 1);
        individualsWithSelectionProbabilities.forEach(individualWithProbability -> {
            if (individualWithProbability.getRight() > universalProbability) {
                selectedForRecombination.add(individualWithProbability.getLeft());
            }
        });
        return selectedForRecombination;
    }

    public List<Individual> applyCrossover(List<Individual> selectedForRecombination) {
        if (selectedForRecombination.isEmpty()) return selectedForRecombination;

        //apply crossover rate
        List<Individual> selectedForRecombinationAfterCrossoverRateApplied = new ArrayList<>();
        selectedForRecombination.forEach(individual -> {
            if (individual.getCrossoverRate() > Utils.getRandomDoubleInRange(0, 1)) {
                selectedForRecombinationAfterCrossoverRateApplied.add(individual);
            }
        });

        if (selectedForRecombinationAfterCrossoverRateApplied.isEmpty())
            return selectedForRecombinationAfterCrossoverRateApplied;

        if (selectedForRecombinationAfterCrossoverRateApplied.size() == 1) {
            Individual oneSurvivor = new Individual(selectedForRecombinationAfterCrossoverRateApplied.get(0));
            oneSurvivor.setTotalCost(null);
            oneSurvivor.getRoutes().forEach(route -> route.setCost(null));
            return Collections.singletonList(oneSurvivor);
        }

        List<Individual> offsprings = new ArrayList<>();
        for (int i = 0; i < selectedForRecombinationAfterCrossoverRateApplied.size(); i++) {
            if (i + 1 == selectedForRecombinationAfterCrossoverRateApplied.size()) {
                Individual oneSurvivor = new Individual(selectedForRecombinationAfterCrossoverRateApplied.get(i));
                oneSurvivor.setTotalCost(null);
                oneSurvivor.getRoutes().forEach(route -> route.setCost(null));
                offsprings.add(oneSurvivor);
            } else {
                offsprings.addAll(createNewOffspring(
                        selectedForRecombinationAfterCrossoverRateApplied.get(i),
                        selectedForRecombinationAfterCrossoverRateApplied.get(i + 1)
                ));
            }
        }

        return offsprings;
    }

    private List<Individual> createNewOffspring(Individual parent1, Individual parent2) {
        Individual offspring1 = new Individual();
        Individual offspring2 = new Individual();
        applyCrossoverForGAOperatorsAndParameters(parent1, parent2, offspring1, offspring2);
        applyCrossoverForRoutes(parent1, parent2, offspring1, offspring2);
        offspring1.setTotalCost(null);
        offspring2.setTotalCost(null);
        offspring1.getRoutes().forEach(r -> r.setCost(null));
        offspring2.getRoutes().forEach(r -> r.setCost(null));
        return List.of(offspring1, offspring2);
    }

    private void applyCrossoverForGAOperatorsAndParameters(Individual parent1, Individual parent2, Individual offspring1, Individual offspring2) {
        offspring1.setDepartureTime(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getDepartureTime() : parent2.getDepartureTime());
        offspring2.setDepartureTime(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getDepartureTime() : parent2.getDepartureTime());

        offspring1.setCrossoverRate(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getCrossoverRate() : parent2.getCrossoverRate());
        offspring2.setCrossoverRate(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getCrossoverRate() : parent2.getCrossoverRate());
        offspring1.setMutationRate(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getMutationRate() : parent2.getMutationRate());
        offspring2.setMutationRate(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getMutationRate() : parent2.getMutationRate());

        offspring1.setCrossoverOperator(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getCrossoverOperator() : parent2.getCrossoverOperator());
        offspring2.setCrossoverOperator(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getCrossoverOperator() : parent2.getCrossoverOperator());
        offspring1.setMutationOperator(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getMutationOperator() : parent2.getMutationOperator());
        offspring2.setMutationOperator(Utils.getRandomDoubleInRange(0, 1) < 0.5 ? parent1.getMutationOperator() : parent2.getMutationOperator());
    }

    private void applyCrossoverForRoutes(Individual parent1, Individual parent2, Individual offspring1, Individual offspring2) {
        recombine(parent1, parent2, offspring1);
        recombine(parent2, parent1, offspring2);
    }

    private void recombine(Individual dominantParent, Individual recessiveParent, Individual offspring) {
        switch (dominantParent.getCrossoverOperator()) {
            case 1:
                randomCrossoverPoints(dominantParent, recessiveParent, offspring);
                break;
            case 2:
                bestPickCrossover(dominantParent, recessiveParent, offspring);
                break;
            default:
                System.out.println("Default crossover...");
                randomCrossoverPoints(dominantParent, recessiveParent, offspring);
                break;
        }
    }

    private void randomCrossoverPoints(Individual dominantParent, Individual recessiveParent, Individual offspring) {
        //prepare empty routes in offspring
        offspring.setRoutes(new ArrayList<>());
        for (int i = 0; i < dominantParent.getRoutes().size(); i++) {
            offspring.getRoutes().add(new Route());
        }

        int nrOfRemainingRoutes;
        Set<GeographicPoint> alreadyAdded = new HashSet<>();
        if (dominantParent.getRoutes().size() > 2) {
            //select 2 distinct random crossover points;
            int randomCrossoverPoint1 = Utils.getRandomIntegerInRange(1, dominantParent.getRoutes().size() - 1);
            int randomCrossoverPoint2 = Utils.getRandomIntegerInRange(1, dominantParent.getRoutes().size() - 1);
            while (randomCrossoverPoint2 == randomCrossoverPoint1) {
                randomCrossoverPoint2 = Utils.getRandomIntegerInRange(1, dominantParent.getRoutes().size() - 1);
            }
            //order the 2 crossover points
            if (randomCrossoverPoint1 > randomCrossoverPoint2) {
                int temp = randomCrossoverPoint1;
                randomCrossoverPoint1 = randomCrossoverPoint2;
                randomCrossoverPoint2 = temp;
            }
            //decide which portion is longer so we extract it from the dominant
            int sizeOfSliceInBetweenCrossoverPoints = randomCrossoverPoint2 - randomCrossoverPoint1;
            boolean longestIsInBetweenCrossoverPoints = sizeOfSliceInBetweenCrossoverPoints > dominantParent.getRoutes().size() - sizeOfSliceInBetweenCrossoverPoints;

            //add longest subsection from the dominant parent
            //and collect remaining deliveryPoints from the recessive parent
            if (longestIsInBetweenCrossoverPoints) {
                for (int i = randomCrossoverPoint1; i < randomCrossoverPoint2; i++) {
                    Route route = new Route(dominantParent.getRoutes().get(i));
                    offspring.getRoutes().set(i, route);
                    alreadyAdded.addAll(route.getRoute());
                }
            } else {
                for (int i = 0; i < randomCrossoverPoint1; i++) {
                    Route route = new Route(dominantParent.getRoutes().get(i));
                    offspring.getRoutes().set(i, route);
                    alreadyAdded.addAll(route.getRoute());
                }
                for (int i = randomCrossoverPoint2; i < dominantParent.getRoutes().size(); i++) {
                    Route route = new Route(dominantParent.getRoutes().get(i));
                    offspring.getRoutes().set(i, route);
                    alreadyAdded.addAll(route.getRoute());
                }
            }

            nrOfRemainingRoutes = longestIsInBetweenCrossoverPoints
                    ? dominantParent.getRoutes().size() - sizeOfSliceInBetweenCrossoverPoints
                    : sizeOfSliceInBetweenCrossoverPoints;

        } else if (dominantParent.getRoutes().size() == 2) { //there are exactly 2 routes in the parent
            if (dominantParent.getRoutes().get(0).getRoute().size() > dominantParent.getRoutes().get(1).getRoute().size()) {
                Route route = new Route(dominantParent.getRoutes().get(0));
                offspring.getRoutes().set(0, route);
                alreadyAdded.addAll(route.getRoute());
            } else {
                Route route = new Route(dominantParent.getRoutes().get(1));
                offspring.getRoutes().set(1, route);
                alreadyAdded.addAll(route.getRoute());
            }
            nrOfRemainingRoutes = 1;
        } else {// there is only one route
            Route route = new Route(dominantParent.getRoutes().get(0));
            offspring.getRoutes().set(0, route);
            alreadyAdded.addAll(route.getRoute());
            nrOfRemainingRoutes = 0;
        }

        if (dominantParent.getRoutes().size() != 1) {
            //randomly create remaining routes from the ones left in the recessive parent
            randomlyCreateRemainingRoutesFromRecessiveParentLeftovers(recessiveParent, offspring, alreadyAdded, nrOfRemainingRoutes);
        }

    }

    private void bestPickCrossover(Individual dominantParent, Individual recessiveParent, Individual offspring) {
        //prepare empty routes in offspring
        offspring.setRoutes(new ArrayList<>());
        for (int i = 0; i < dominantParent.getRoutes().size(); i++) {
            offspring.getRoutes().add(new Route());
        }

        //decide how many routes should be formed from dominant and recessive parent
        int nrRoutesFromDominantParent = (int) Math.ceil(dominantParent.getRoutes().size() * crossoverBestPickLoad);
        int nrOfRemainingRoutes = dominantParent.getRoutes().size() - nrRoutesFromDominantParent;
        dominantParent.setRoutes(dominantParent.getRoutes().stream().sorted(Comparator.comparing(Route::getCost).reversed()).collect(Collectors.toList()));

        //add best routes from dominant parent
        Set<GeographicPoint> alreadyAdded = new HashSet<>();
        for (int i = 0; i < nrRoutesFromDominantParent; i++) {
            Route route = new Route(dominantParent.getRoutes().get(i));
            offspring.getRoutes().set(i, route);
            alreadyAdded.addAll(route.getRoute());
        }

        //if no routes remained to be added from the recessive child, return
        if (nrOfRemainingRoutes == 0) {
            return;
        }

        //randomly create remaining routes from the ones left in the recessive parent
        randomlyCreateRemainingRoutesFromRecessiveParentLeftovers(recessiveParent, offspring, alreadyAdded, nrOfRemainingRoutes);
    }

    private void randomlyCreateRemainingRoutesFromRecessiveParentLeftovers(Individual recessiveParent, Individual offspring, Set<GeographicPoint> alreadyAdded, int nrOfRemainingRoutes) {
        GeographicPoint depot = recessiveParent.getRoutes().get(0).getRoute().get(0);
        alreadyAdded.add(depot);
        List<GeographicPoint> remainingDeliveryPoints = recessiveParent.getRoutes().stream().map(Route::getRoute).flatMap(Collection::stream).collect(Collectors.toList());
        remainingDeliveryPoints.removeAll(alreadyAdded);
        List<Route> remainingRoutes = createRoutesFromDeliveryPoints(nrOfRemainingRoutes, remainingDeliveryPoints, depot);

        AtomicInteger i = new AtomicInteger(0);
        offspring.getRoutes().forEach(route -> {
            if (route.getRoute() == null || route.getRoute().isEmpty()) {
                route.setRoute(new ArrayList<>(remainingRoutes.get(i.get()).getRoute()));
                i.incrementAndGet();
            }
        });
    }

    public void mutateOffsprings(Population population, List<Individual> offsprings) {
        if (offsprings.isEmpty()) return;

        offsprings.forEach(offspring -> {
            Double originalMutationRate = offspring.getMutationRate();
            Integer originalMutationOperator = offspring.getMutationOperator();
            if (originalMutationRate > Utils.getRandomDoubleInRange(0, 1)) {
                applyMutationForRoutes(offspring, originalMutationOperator);
            }
            if (originalMutationRate > Utils.getRandomDoubleInRange(0, 1)) {
                applyMutationForCrossoverRate(offspring);
            }
            if (originalMutationRate > Utils.getRandomDoubleInRange(0, 1)) {
                applyMutationForMutationRate(offspring);
            }
            if (originalMutationRate > Utils.getRandomDoubleInRange(0, 1)) {
                applyMutationForDepartureTimeWindow(offspring, population.getPreferredDepartureTime());
            }
            if (originalMutationRate > Utils.getRandomDoubleInRange(0, 1)) {
                applyMutationForCrossoverOperator(offspring);
            }
            if (originalMutationRate > Utils.getRandomDoubleInRange(0, 1)) {
                applyMutationForMutationOperator(offspring);
            }
        });
    }

    private void applyMutationForRoutes(Individual offspring, Integer originalMutationOperator) {
        switch (originalMutationOperator) {
            case 1:
                randomRemoveAndRandomReinsert(offspring);
                break;
            case 2:
                removeFromWorstAndInsertIntoBest(offspring);
                break;
            default:
                System.out.println("Default mutation...");
                randomRemoveAndRandomReinsert(offspring);
                break;
        }
    }

    private void randomRemoveAndRandomReinsert(Individual offspring) {
        int nrOfRemoves = (int) Math.ceil(mutationRemoveLoad * (offspring.getRoutes().stream().map(Route::getRoute).flatMap(Collection::stream).collect(Collectors.toSet()).size() - 1));

        List<GeographicPoint> removedDeliveryPoints = new ArrayList<>();
        List<Integer> emptyRoutes = new ArrayList<>();

        for (int i = 0; i < nrOfRemoves; i++) {
            //generate random route index
            int randomRouteIndex = Utils.getRandomIntegerInRange(0, offspring.getRoutes().size() - 1);

            //do not remove anything from already empty routes
            while (offspring.getRoutes().get(randomRouteIndex).getRoute().size() == 2) {
                randomRouteIndex = Utils.getRandomIntegerInRange(0, offspring.getRoutes().size() - 1);
            }

            //generate random delivery point index inside the route
            int randomDeliveryPointRemoveIndex;
            if (offspring.getRoutes().get(randomRouteIndex).getRoute().size() == 3) {
                randomDeliveryPointRemoveIndex = 1;
                emptyRoutes.add(randomRouteIndex); //keep a list of empty routes to insert into them so we do not remain with empty routes
            } else {
                randomDeliveryPointRemoveIndex = Utils.getRandomIntegerInRange(1, offspring.getRoutes().get(randomRouteIndex).getRoute().size() - 2);
            }

            //remove the delivery point from the given route and add it to the list of removed delivery points
            removedDeliveryPoints.add(offspring.getRoutes().get(randomRouteIndex).getRoute().remove(randomDeliveryPointRemoveIndex));
        }

        Collections.shuffle(removedDeliveryPoints);

        //reinsert randomly
        removedDeliveryPoints.forEach(removedDeliveryPoint -> {
            if (!emptyRoutes.isEmpty()) {
                //if there are empty routes, do not forget to insert something into them so we do not leave them empty
                Integer emptyRoutIndex = emptyRoutes.remove(0);
                offspring.getRoutes().get(emptyRoutIndex).getRoute().add(1, removedDeliveryPoint);
            } else {
                //when we finished with empty routes, randomly insert anywhere else
                int randomRouteIndex = Utils.getRandomIntegerInRange(0, offspring.getRoutes().size() - 1);
                int randomDeliveryPointInsertIndex = Utils.getRandomIntegerInRange(1, offspring.getRoutes().get(randomRouteIndex).getRoute().size() - 2);
                offspring.getRoutes().get(randomRouteIndex).getRoute().add(randomDeliveryPointInsertIndex, removedDeliveryPoint);
            }
        });
    }

    private void removeFromWorstAndInsertIntoBest(Individual offspring) {
        //first we need to evaluate to see which are the best and worst routes
        evaluateIndividuals(Collections.singletonList(offspring));

        int nrOfRemoves = (int) Math.ceil(mutationRemoveLoad * (offspring.getRoutes().stream().map(Route::getRoute).flatMap(Collection::stream).collect(Collectors.toSet()).size() - 1));

        List<GeographicPoint> removedDeliveryPoints = new ArrayList<>();
        List<Integer> emptyRoutes = new ArrayList<>();

        //sort routes by cost so we can remove from worst and insert into best;
        offspring.setRoutes(offspring.getRoutes().stream().sorted(Comparator.comparing(Route::getCost)).collect(Collectors.toList()));

        for (int i = 0; i < nrOfRemoves; i++) {
            int randomDeliveryPointRemoveIndex;
            if (offspring.getRoutes().get(i).getRoute().size() == 3) {
                randomDeliveryPointRemoveIndex = 1;
                emptyRoutes.add(i); //keep a list of empty routes so we can reinsert into them so we do not remove with empty routes
            } else {
                randomDeliveryPointRemoveIndex = Utils.getRandomIntegerInRange(1, offspring.getRoutes().get(i).getRoute().size() - 2);
            }

            //remove the delivery point from the given route and add it to the list of removed delivery points
            removedDeliveryPoints.add(offspring.getRoutes().get(i).getRoute().remove(randomDeliveryPointRemoveIndex));
        }

        Collections.shuffle(removedDeliveryPoints);

        //reinsert into best
        AtomicInteger bestRouteIndex = new AtomicInteger(offspring.getRoutes().size() - 1);
        removedDeliveryPoints.forEach(removedDeliveryPoint -> {
            if (!emptyRoutes.isEmpty()) {
                //if there are empty routes, do not forget to insert something into them so we do not leave them empty
                Integer emptyRoutIndex = emptyRoutes.remove(0);
                offspring.getRoutes().get(emptyRoutIndex).getRoute().add(1, removedDeliveryPoint);
            } else {
                //when we finished with empty routes, randomly insert anywhere else
                int randomDeliveryPointInsertIndex = Utils.getRandomIntegerInRange(1, offspring.getRoutes().get(bestRouteIndex.get()).getRoute().size() - 2);
                offspring.getRoutes().get(bestRouteIndex.get()).getRoute().add(randomDeliveryPointInsertIndex, removedDeliveryPoint);
                bestRouteIndex.decrementAndGet();
            }
        });

        //do not forget to reset evaluation so we keep integrity
        offspring.setTotalCost(null);
        offspring.getRoutes().forEach(r -> r.setCost(null));
    }


    private void applyMutationForCrossoverRate(Individual offspring) {
        offspring.setCrossoverRate(Utils.getRandomElementFromCollection(crossoverRates));
    }

    private void applyMutationForMutationRate(Individual offspring) {
        offspring.setMutationRate(Utils.getRandomElementFromCollection(mutationRates));
    }

    private void applyMutationForDepartureTimeWindow(Individual offspring, Instant preferredDepartureTime) {
        offspring.setDepartureTime(preferredDepartureTime.plus(Utils.getRandomElementFromCollection(departureTimeWindows), ChronoUnit.MINUTES));
    }

    private void applyMutationForCrossoverOperator(Individual offspring) {
        offspring.setCrossoverOperator(Utils.getRandomIntegerInRange(1, nrCrossoverOperators));
    }

    private void applyMutationForMutationOperator(Individual offspring) {
        offspring.setMutationOperator(Utils.getRandomIntegerInRange(1, nrMutationOperators));
    }

    public void selectSurvivorsForNextGen(Population population, List<Individual> offsprings) {
        List<Individual> survivors = new ArrayList<>();
        survivors.addAll(population.getIndividuals());
        survivors.addAll(offsprings);
        survivors = survivors.stream().sorted(Comparator.comparing(Individual::getTotalCost)).collect(Collectors.toList());

        population.setIndividuals(new ArrayList<>(survivors.subList(0, populationSize)));
    }
}
