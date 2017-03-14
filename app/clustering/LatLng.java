package clustering;

import play.libs.F;
import play.mvc.QueryStringBindable;

import java.util.Map;
import java.util.Optional;

/**
 * Created by evelina on 11/04/2014.
 */
public class LatLng implements QueryStringBindable<LatLng> {

    public double latitude;

    public double longitude;

    public LatLng() {
    }

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Optional<LatLng> bind(String key, Map<String, String[]> data) {
        if (data.containsKey(key)) {
            try {
                String[] values = data.get(key)[0].split(",");
                if (values.length == 2) {
                    return Optional.of(new LatLng(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                }
            } catch (NumberFormatException e) {
            }
        }
        return Optional.empty();
    }

    @Override
    public String unbind(String key) {
        return key + "=" + latitude + "," + longitude;
    }

    @Override
    public String javascriptUnbind() {
        return "function(k, v) {\n" +
                "    return encodeURIComponent(k) + '=' + v.lat + ',' + v.lng; \n" +
                "}";
    }

    @Override
    public String toString() {
        return "LatLng{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
