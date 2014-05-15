package clustering;

import models.LatLng;

import java.util.Random;
import java.util.UUID;

import static clustering.LocationUtils.*;

/**
 * Created by evelina on 12/04/2014.
 */
public class LocationsGenerator {

    private static final Random random = new Random();

    public static LatLng randomizeLatLng(LatLng sw, LatLng ne) {
        double latMin = sw.latitude;
        double lonMin = sw.longitude;

        double latRange = ne.latitude - sw.latitude;
        double lonRange = ne.longitude - sw.longitude;

        double lat = latMin + random.nextDouble() * latRange;
        double lng = lonMin + random.nextDouble() * lonRange;

        return new LatLng(clipLatitude(lat), clipLongitude(lng));
    }

    public static String randomizeName() {
        return UUID.randomUUID().toString();
    }
}
