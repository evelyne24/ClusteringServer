package clustering;

import models.Location;

import java.util.Collections;
import java.util.Set;

public class QuadItem implements PointQuadTree.Item {

    public final Location mClusterItem;
    private final Point mPoint;
    private final LatLng mPosition;
    private Set<Location> singletonSet;

    public QuadItem(Location item) {
        mClusterItem = item;
        mPosition = new LatLng(item.latitude,item.longitude);
        mPoint = LocationUtils.latLngToWorldPoint(mPosition, ZoomLevel.get(19));
        singletonSet = Collections.singleton(mClusterItem);
    }

    public Point getPoint() {
        return mPoint;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public Set<Location> getItems() {
        return singletonSet;
    }

    public int getSize() {
        return 1;
    }
}