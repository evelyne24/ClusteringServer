package clustering;

import play.libs.F;
import play.mvc.QueryStringBindable;

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
    public F.Option<LatLng> bind(String key, java.util.Map<String, String[]> data) {
        if (data.containsKey(key)) {
            try {
                String[] values = data.get(key)[0].split(",");
                if (values.length == 2) {
                    return F.Some(new LatLng(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
                }
            } catch (NumberFormatException e) {
            }
        }
        return F.None();
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
