package marcu.rwtdvrpapi.api.service;

import marcu.rwtdvrpapi.api.domain.Individual;
import marcu.rwtdvrpapi.api.domain.Population;
import marcu.rwtdvrpapi.api.domain.Solution;
import marcu.rwtdvrpapi.api.domain.VRPInstance;
import marcu.rwtdvrpapi.api.utils.MyPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class RWTDVRPServiceImpl implements RWTDVRPService {
    @Value("${rwtdvrp.api.nr-workers}")
    private Integer nrWorkers;

    @Value("${rwtdvro.ga.not-changed-generations-limit}")
    private Integer notChangedGenerationsLimit;

    private ThreadPoolExecutor threadPoolExecutor;
    private Map<String, MyPair<Population, Future<?>>> inProgress = new ConcurrentHashMap<>();
    private Map<String, MyPair<Population, Future<?>>> finished = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private GAHelper gaHelper;

    public RWTDVRPServiceImpl(@Value("${rwtdvrp.api.nr-workers}") Integer nrWorkers,
                              final GAHelper gaHelper) {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrWorkers);
        this.gaHelper = gaHelper;
    }

    @Override
    public String start(VRPInstance vrpInstance) throws Exception {
        //Generate a unique id if not existing
        String uniqueId = vrpInstance.getId();
        if (uniqueId == null || uniqueId.isEmpty()) {
            uniqueId = UUID.randomUUID().toString();
        }
        while (inProgress.keySet().contains(uniqueId)) {
            uniqueId = UUID.randomUUID().toString();
        }
        vrpInstance.setId(uniqueId);


        if (threadPoolExecutor.getActiveCount() >= nrWorkers) {
            throw new Exception("The server is busy now solving other VRP instances. Please try again later!");
        }
        Future<?> submit = threadPoolExecutor.submit(() -> {
            try {
                solveVrp(vrpInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //mark an in progress solver for the VRP with this id
        inProgress.put(uniqueId, new MyPair<>(new Population(), submit));

        return uniqueId;
    }

    @Override
    public Solution stop(String id) throws Exception {
        //get the population
        MyPair<Population, Future<?>> toBeStopped = inProgress.remove(id);
        if (toBeStopped == null) {
            toBeStopped = finished.remove(id);

            if (toBeStopped == null) {
                throw new Exception(String.format("There was no running solver for a VRP instance with the following id: {%s}.", id));
            }
        }

        Population population = toBeStopped.getLeft();

        //stop the thread
        toBeStopped.getRight().cancel(true);

        //select the best individual
        Individual bestIndividual = population.getIndividuals().stream().sorted(Comparator.comparing(Individual::getTotalCost)).collect(Collectors.toList()).get(0);
        Solution solution = new Solution();
        solution.setRoutes(bestIndividual.getRoutes());
        solution.setDepartureTime(bestIndividual.getDepartureTime());
        solution.setTotalCost(bestIndividual.getTotalCost());
        return solution;
    }

    private void solveVrp(VRPInstance vrpInstance) {
        Instant startTime = Instant.now();

        int i = 0;
        String uniqueId = vrpInstance.getId();
        Population population = inProgress.get(uniqueId).getLeft();
        gaHelper.initializePopulation(vrpInstance, population);
        gaHelper.evaluateIndividuals(population.getIndividuals());

        double actualPopulationMeanCostValue = population.getIndividuals().stream().mapToDouble(Individual::getTotalCost).average().orElse(Double.NaN);
        double previousPopulationMeanCostValue;
        int notChangedCount = 0;

        while (inProgress.keySet().contains(uniqueId) && notChangedCount < notChangedGenerationsLimit) {
            previousPopulationMeanCostValue = actualPopulationMeanCostValue;
            System.out.println("VRP instnace " + uniqueId +", gen. " + i + " = " + actualPopulationMeanCostValue);

            List<Individual> selectedForRecombination = gaHelper.selectForRecombination(population);
            List<Individual> offsprings = gaHelper.applyCrossover(selectedForRecombination);
            gaHelper.mutateOffsprings(population, offsprings);
            gaHelper.evaluateIndividuals(offsprings);
            gaHelper.selectSurvivorsForNextGen(population, offsprings);
            actualPopulationMeanCostValue = population.getIndividuals().stream().mapToDouble(Individual::getTotalCost).average().orElse(Double.NaN);
            if (actualPopulationMeanCostValue < previousPopulationMeanCostValue) {
                notChangedCount = 0;
            } else {
                notChangedCount++;
            }
            i++;
        }

        //stopped so add to finished with atomic operation
        synchronized (lock) {
            if (inProgress.containsKey(uniqueId)) {
                finished.putIfAbsent(uniqueId, inProgress.remove(uniqueId));
            }
        }
        Instant endTime = Instant.now();
        Duration timePassed = Duration.between(startTime, endTime);
        String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
        System.out.println("Result obtained in " + timeUnit);
    }
}
