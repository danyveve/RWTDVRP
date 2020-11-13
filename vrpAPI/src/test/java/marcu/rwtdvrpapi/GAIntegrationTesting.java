package marcu.rwtdvrpapi;

import marcu.rwtdvrpapi.api.adapter.TestGoogleMapsApiAdapter;
import marcu.rwtdvrpapi.api.domain.GeographicPoint;
import marcu.rwtdvrpapi.api.domain.Individual;
import marcu.rwtdvrpapi.api.domain.Population;
import marcu.rwtdvrpapi.api.domain.VRPInstance;
import marcu.rwtdvrpapi.api.service.GAHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GAIntegrationTesting {
    @Autowired
    private GAHelper gaHelper;

    private VRPInstance vrpInstance;
    private GeographicPoint depot;
    private Instant preferredDeparture;

    @BeforeAll
    public void beforeAll() {
        gaHelper.vrpToProviderAdapter = new TestGoogleMapsApiAdapter();
    }

    @BeforeEach
    public void setUp() {
        depot = GeographicPoint.builder().latitude(-1D).longitude(1D).address("addressDepot").build();
        GeographicPoint dp1 = GeographicPoint.builder().latitude(-11D).longitude(11D).address("dp1").build();
        GeographicPoint dp2 = GeographicPoint.builder().latitude(-22D).longitude(22D).address("dp2").build();
        GeographicPoint dp3 = GeographicPoint.builder().latitude(-33D).longitude(33D).address("dp3").build();

        preferredDeparture = Instant.now();
        vrpInstance = VRPInstance.builder()
                .id("1")
                .depot(depot)
                .deliveryPoints(Arrays.asList(dp1, dp2, dp3))
                .numberOfDrivers(3)
                .preferredDepartureTime(preferredDeparture)
                .build();
    }

    @Test
    @Order(1)
    public void test_initialize_population() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        assertIntegrityOfPopulation(false, population);
    }

    @Test
    @Order(2)
    public void test_evaluate_population() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());
        assertIntegrityOfPopulation(true, population);
    }

    @Test
    @Order(3)
    public void test_select_for_recombination() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());
        List<Individual> selectForRecombination = gaHelper.selectForRecombination(population);
        assertIntegrityOfPopulation(true, population);
        selectForRecombination.forEach(individual -> assertIndividualIntegrity(individual, true));
    }

    @Test
    @Order(4)
    public void test_crossover() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());
        List<Individual> selectForRecombination = gaHelper.selectForRecombination(population);
        List<Individual> offsprings = gaHelper.applyCrossover(selectForRecombination);
        assertIntegrityOfPopulation(true, population);
        selectForRecombination.forEach(individual -> assertIndividualIntegrity(individual, true));
        offsprings.forEach(individual -> assertIndividualIntegrity(individual, false));
    }

    @Test
    @Order(5)
    public void test_mutation() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());
        List<Individual> selectForRecombination = gaHelper.selectForRecombination(population);
        List<Individual> offsprings = gaHelper.applyCrossover(selectForRecombination);
        gaHelper.mutateOffsprings(population, offsprings);
        assertIntegrityOfPopulation(true, population);
        selectForRecombination.forEach(individual -> assertIndividualIntegrity(individual, true));
        offsprings.forEach(individual -> assertIndividualIntegrity(individual, false));
    }

    @Test
    @Order(6)
    public void test_evaluate_offsprings() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());
        List<Individual> selectForRecombination = gaHelper.selectForRecombination(population);
        List<Individual> offsprings = gaHelper.applyCrossover(selectForRecombination);
        gaHelper.mutateOffsprings(population, offsprings);
        gaHelper.evaluateIndividuals(offsprings);
        assertIntegrityOfPopulation(true, population);
        selectForRecombination.forEach(individual -> assertIndividualIntegrity(individual, true));
        offsprings.forEach(individual -> assertIndividualIntegrity(individual, true));
    }

    @Test
    @Order(7)
    public void test_select_survivors() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());
        List<Individual> selectForRecombination = gaHelper.selectForRecombination(population);
        List<Individual> offsprings = gaHelper.applyCrossover(selectForRecombination);
        gaHelper.mutateOffsprings(population, offsprings);
        gaHelper.evaluateIndividuals(offsprings);
        gaHelper.selectSurvivorsForNextGen(population, offsprings);
        assertIntegrityOfPopulation(true, population);
        selectForRecombination.forEach(individual -> assertIndividualIntegrity(individual, true));
        offsprings.forEach(individual -> assertIndividualIntegrity(individual, true));
        assertIntegrityOfPopulation(true, population);
    }

    @Test
    @Order(8)
    public void test_population_does_not_become_worse_after_one_iteration() {
        Population population = new Population();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());

        Double initialPopulationCost = population.getIndividuals().stream().mapToDouble(Individual::getTotalCost).average().orElse(Double.NaN);

        List<Individual> selectForRecombination = gaHelper.selectForRecombination(population);
        List<Individual> offsprings = gaHelper.applyCrossover(selectForRecombination);
        gaHelper.mutateOffsprings(population, offsprings);
        gaHelper.evaluateIndividuals(offsprings);
        gaHelper.selectSurvivorsForNextGen(population, offsprings);
        assertIntegrityOfPopulation(true, population);
        selectForRecombination.forEach(individual -> assertIndividualIntegrity(individual, true));
        offsprings.forEach(individual -> assertIndividualIntegrity(individual, true));
        assertIntegrityOfPopulation(true, population);

        Double afterPopulationCost = population.getIndividuals().stream().mapToDouble(Individual::getTotalCost).average().orElse(Double.NaN);

        assertTrue(afterPopulationCost <= initialPopulationCost);
    }

    private void assertIntegrityOfPopulation(boolean isEvaluated, Population population) {
        assertEquals(depot, population.getDepot());
        assertEquals(preferredDeparture, population.getPreferredDepartureTime());
        assertEquals(3, population.getNumberOfDrivers());
        assertEquals(gaHelper.populationSize, population.getIndividuals().size());
        population.getIndividuals().forEach(individual -> {
           assertIndividualIntegrity(individual, isEvaluated);
        });
    }

    private void assertIndividualIntegrity(Individual individual, boolean isEvaluated) {
        if (isEvaluated) {
            assertNotNull(individual.getTotalCost());
        } else {
            assertNull(individual.getTotalCost());
        }
        assertTrue(gaHelper.crossoverRates.contains(individual.getCrossoverRate()));
        assertTrue(gaHelper.mutationRates.contains(individual.getMutationRate()));
        assertTrue(gaHelper.nrCrossoverOperators >= individual.getCrossoverOperator() && individual.getCrossoverOperator() >= 1);
        assertTrue(gaHelper.nrMutationOperators >= individual.getMutationOperator() && individual.getMutationOperator() >= 1);
        assertNotNull(individual.getDepartureTime());
        assertEquals(vrpInstance.getNumberOfDrivers(), individual.getRoutes().size());

        individual.getRoutes().forEach(route -> {
            if (isEvaluated) {
                assertNotNull(route.getCost());
            } else {
                assertNull(route.getCost());
            }

            route.getRoute().forEach(point -> assertTrue(vrpInstance.getDepot().equals(point) || vrpInstance.getDeliveryPoints().contains(point)));
        });
    }
}
