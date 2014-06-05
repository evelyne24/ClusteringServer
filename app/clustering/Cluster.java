package clustering;

import models.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evelina on 12/04/2014.
 */
public class Cluster  implements PointQuadTree.Item{

    public LatLng center;
    public LatLng topLeft;
    public LatLng topRight;
    public LatLng bottomLeft;
    public LatLng bottomRight;
    public Location location;
    public int count;
    public String quadKey;
    public Point mPoint;
    public List<Cluster> mChildClusters;

    private Cluster(Builder builder) {
        center = builder.center;
        topLeft = builder.topLeft;
        topRight = builder.topRight;
        bottomLeft = builder.bottomLeft;
        bottomRight = builder.bottomRight;
        location = builder.location;
        count = builder.count;
        quadKey = builder.quadKey;
        mPoint=LocationUtils.latLngToWorldPoint(center,ZoomLevel.get(19));
        mChildClusters=new ArrayList<Cluster>();
    }

    @Override
    public Point getPoint() {
        return mPoint;
    }

    public static final class Builder {
        private LatLng center;
        private LatLng topLeft;
        private LatLng topRight;
        private LatLng bottomLeft;
        private LatLng bottomRight;
        private Location location;
        private int count;
        private String quadKey;

        public Builder() {
        }

        public Builder center(LatLng center) {
            this.center = center;
            return this;
        }

        public Builder topLeft(LatLng topLeft) {
            this.topLeft = topLeft;
            return this;
        }

        public Builder topRight(LatLng topRight) {
            this.topRight = topRight;
            return this;
        }

        public Builder bottomLeft(LatLng bottomLeft) {
            this.bottomLeft = bottomLeft;
            return this;
        }

        public Builder bottomRight(LatLng bottomRight) {
            this.bottomRight = bottomRight;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder quadKey(String quadKey) {
            this.quadKey = quadKey;
            return this;
        }

        public Cluster build() {
            return new Cluster(this);
        }
    }
}
