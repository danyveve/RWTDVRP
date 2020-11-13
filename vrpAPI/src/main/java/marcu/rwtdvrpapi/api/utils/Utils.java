package marcu.rwtdvrpapi.api.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static int getRandomIntegerInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double getRandomDoubleInRange(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static <T> T getRandomElementFromCollection(Collection<T> collection) {
        if (collection == null || collection.size() == 0) {
            return null;
        }
        int randomIndex = getRandomIntegerInRange(0, collection.size() - 1);
        return new ArrayList<>(collection).get(randomIndex);
    }
}
