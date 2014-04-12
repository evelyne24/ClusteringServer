package clustering;

import java.util.Random;

/**
 * Created by evelina on 12/04/2014.
 */
public class RandomLocationsGenerator {

    // UK bounds (REMEMBER: Latitude grows from left, longitude grows from bottom)
    // sw=51.508742,-3.240967
    // ne=54.316523,-0.736084

    private static final Random random = new Random();

    public static LatLng generate(LatLng sw, LatLng ne) {
        double latMin = sw.latitude;
        double latRange = ne.latitude - sw.latitude;
        double lngMin = sw.longitude;
        double lngRange = ne.longitude - sw.longitude;

        return new LatLng(latMin + random.nextDouble() * latRange, lngMin + random.nextDouble() * lngRange);
    }
}
