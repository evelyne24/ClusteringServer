package clustering;

/**
 * Created by evelina on 12/04/2014.
 */
public class Cluster {

    public final LatLng center;
    public final int count;

    public Cluster(LatLng center, int count) {
        this.center = center;
        this.count = count;
    }
}
