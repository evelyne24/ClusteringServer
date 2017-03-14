package clustering;

import static java.lang.Math.*;

/**
 * A collection of utility methods.
 */
public class LocationUtils {

    public static final int TILE_SIZE = 256;

    public static final double MIN_LATITUDE = -85.05112877;
    public static final double MAX_LATITUDE = 85.05112877;
    public static final double MIN_LONGITUDE = -179.999;
    public static final double MAX_LONGITUDE = 179.999;

    /**
     * Make sure a value stays within a minimum and maximum values.
     *
     * @param n
     * @param min
     * @param max
     * @return
     */
    public static double clip(double n, double min, double max) {
        return min(max(n, min), max);
    }

    /**
     * Same as {@link LocationUtils#clip(double, double, double)} for integers.
     *
     * @param n
     * @param min
     * @param max
     * @return
     */
    public static int clip(int n, int min, int max) {
        return min(max(n, min), max);
    }

    /**
     * Make sure latitude stays within correct bounds.
     *
     * @param latitude
     * @return
     */
    public static double clipLatitude(double latitude) {
        return clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
    }

    /**
     * Make sure longitude stays within correct bounds.
     *
     * @param longitude
     * @return
     */
    public static double clipLongitude(double longitude) {
        return clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    /**
     * Converts a point from geographical coordinates (latitude, longitude)
     * to absolute world pixel (not screen pixel) for a zoom level.
     *
     * @param latLng
     * @return
     */
    public static Point latLngToWorldPoint(LatLng latLng, ZoomLevel zoom) {
        final double latitude = clipLatitude(latLng.latitude);
        final double longitude = clipLongitude(latLng.longitude);
        final double sinLatitude = sin(latitude * (PI / 180.0));

        final double x = (longitude + 180.0) / 360.0;
        final double y = 0.5 - log((1.0 + sinLatitude) / (1.0 - sinLatitude)) / (4.0 * PI);

        final double pixelX = clip(x * zoom.mapSize + 0.5, 0, zoom.mapSize - 1);
        final double pixelY = clip(y * zoom.mapSize + 0.5, 0, zoom.mapSize - 1);

        return new Point((int) pixelX, (int) pixelY);
    }

    /**
     * Converts a world point back into geographical coordinates for a zoom level.
     *
     * @param point
     * @return
     */
    public static LatLng worldPointToLatLng(Point point, ZoomLevel zoom) {
        final long mapSize = zoom.mapSize;
        final double x = (clip(point.x, 0, mapSize - 1.0) / mapSize) - 0.5;
        final double y = 0.5 - ((double) clip(point.y, 0, mapSize - 1) / mapSize);
        double latitude = 90 - 360.0 * atan(exp(-y * 2 * PI)) / PI;
        double longitude = 360.0 * x;
        return new LatLng(latitude, longitude);
    }

    /**
     * Computes the X, Y coordinates of the tile containing this point.
     *
     * @param point
     * @return
     */
    public static Point worldPointToTileXY(Point point) {
        final int tileX = (int) Math.floor((double) point.x / TILE_SIZE);
        final int tileY = (int) Math.floor((double) point.y / TILE_SIZE);
        return new Point(tileX, tileY);
    }

    public static float distanceBetween(double startLatitude, double startLongitude,
                                        double endLatitude, double endLongitude) {
        float[] results = new float[1];
        distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    /**
     * Computes the QuadKey string for a tile at a certain level.
     *
     * @return
     */
    public static String getQuadKey(int x, int y, int zoom) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = zoom; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((x & mask) != 0) {
                digit++;
            }
            if ((y & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    public static String getQuadKey(LatLng latLng, ZoomLevel zoom) {
        Point tile = worldPointToTileXY(latLngToWorldPoint(latLng, zoom));
        return getQuadKey(tile.x, tile.y, zoom.zoom);
    }

    public static void distanceBetween(double startLatitude, double startLongitude,
                                       double endLatitude, double endLongitude, float[] results) {
        if (results == null || results.length < 1) {
            throw new IllegalArgumentException("results is null or has length < 1");
        }
        computeDistanceAndBearing(startLatitude, startLongitude,
                endLatitude, endLongitude, results);
    }

    private static void computeDistanceAndBearing(double lat1, double lon1,
                                                  double lat2, double lon2, float[] results) {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        int MAXITERS = 20;
        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lon1 *= Math.PI / 180.0;
        lon2 *= Math.PI / 180.0;

        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

        double L = lon2 - lon1;
        double A = 0.0;
        double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
        double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma = 0.0;
        double deltaSigma = 0.0;
        double cosSqAlpha = 0.0;
        double cos2SM = 0.0;
        double cosSigma = 0.0;
        double sinSigma = 0.0;
        double cosLambda = 0.0;
        double sinLambda = 0.0;

        double lambda = L; // initial guess
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2; // (14)
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
            sigma = Math.atan2(sinSigma, cosSigma); // (16)
            double sinAlpha = (sinSigma == 0) ? 0.0 :
                    cosU1cosU2 * sinLambda / sinSigma; // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 :
                    cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
            A = 1 + (uSquared / 16384.0) * // (3)
                    (4096.0 + uSquared *
                            (-768 + uSquared * (320.0 - 175.0 * uSquared)));
            double B = (uSquared / 1024.0) * // (4)
                    (256.0 + uSquared *
                            (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
            double C = (f / 16.0) *
                    cosSqAlpha *
                    (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * // (6)
                    (cos2SM + (B / 4.0) *
                            (cosSigma * (-1.0 + 2.0 * cos2SMSq) -
                                    (B / 6.0) * cos2SM *
                                            (-3.0 + 4.0 * sinSigma * sinSigma) *
                                            (-3.0 + 4.0 * cos2SMSq)));

            lambda = L +
                    (1.0 - C) * f * sinAlpha *
                            (sigma + C * sinSigma *
                                    (cos2SM + C * cosSigma *
                                            (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)

            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }

        float distance = (float) (b * A * (sigma - deltaSigma));
        results[0] = distance;
        if (results.length > 1) {
            float initialBearing = (float) Math.atan2(cosU2 * sinLambda,
                    cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
            initialBearing *= 180.0 / Math.PI;
            results[1] = initialBearing;
            if (results.length > 2) {
                float finalBearing = (float) Math.atan2(cosU1 * sinLambda,
                        -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
                finalBearing *= 180.0 / Math.PI;
                results[2] = finalBearing;
            }
        }
    }
}
