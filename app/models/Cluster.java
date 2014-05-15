package models;

import clustering.QuadTile;
import clustering.ZoomLevel;
import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static clustering.LocationUtils.computeWeightedCenter;
import static clustering.QuadTile.getTileFromQuadKey;
import static clustering.ZoomLevel.MAX_CLUSTER_ZOOM;
import static clustering.ZoomLevel.MIN_CLUSTER_ZOOM;

/**
 * Created by evelina on 12/04/2014.
 */
@Entity
public class Cluster extends Model {

    @Id
    public String quadKey;

    public Integer zoom;

    @Column(name = "size")
    public Integer count;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "center_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "center_lon"))
    })
    public LatLng center;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "tl_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "tl_lon"))
    })
    public LatLng topLeft;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "tr_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "tr_lon"))
    })
    public LatLng topRight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "bl_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "bl_lon"))
    })
    public LatLng bottomLeft;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "br_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "br_lon"))
    })
    public LatLng bottomRight;

    public Location location;

    public Cluster() {
    }

    private Cluster(Builder builder) {
        quadKey = builder.quadKey;
        count = builder.count;
        zoom = builder.zoom;
        center = builder.center;
        topLeft = builder.topLeft;
        topRight = builder.topRight;
        bottomLeft = builder.bottomLeft;
        bottomRight = builder.bottomRight;
        location = builder.location;
    }

    @Override
    public String toString() {
        return "{" +
                "quadKey='" + quadKey + '\'' +
                ", zoom=" + zoom +
                ", count=" + count +
                ", center=" + center +
                '}';
    }

    public static final class Builder {
        private String quadKey;
        private Integer zoom;
        private Integer count;
        private LatLng center;
        private LatLng topLeft;
        private LatLng topRight;
        private LatLng bottomLeft;
        private LatLng bottomRight;
        private Location location;

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

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder quadKey(String quadKey) {
            this.quadKey = quadKey;
            return this;
        }

        public Builder zoom(int zoom) {
            this.zoom = zoom;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Cluster build() {
            return new Cluster(this);
        }
    }

    /**
     * Generic query helper for entity Cluster.
     */
    public static Finder<String, Cluster> find = new Finder<String, Cluster>(String.class, Cluster.class);

    /**
     * Get all Clusters within bounds for a given zoom level.
     *
     * @param sw
     * @param ne
     * @param zoom
     * @return
     */
    public static List<Cluster> get(LatLng sw, LatLng ne, int zoom) {
        return find.where()
                .gt("center_lat", sw.latitude)
                .lt("center_lat", ne.latitude)
                .gt("center_lon", sw.longitude)
                .lt("center_lon", ne.longitude)
                .eq("zoom", zoom)
                .findList();
    }

    /**
     * For the given {@link Location}, create or update its {@link Cluster} for all the "cluster-able" zoom levels
     * defined in {@link ZoomLevel}.
     *
     * @param location
     */
    public static void createOrUpdate(Location location, Map<String, Cluster> clusters) {
        for (int zoom = MIN_CLUSTER_ZOOM; zoom <= MAX_CLUSTER_ZOOM; zoom++) {
            String quadKeyAtZoom = location.quadKey.substring(0, zoom);
            Cluster cluster = clusters.get(quadKeyAtZoom);

            if (cluster == null) {
                QuadTile tile = getTileFromQuadKey(quadKeyAtZoom);
                cluster = new Cluster.Builder()
                        .quadKey(quadKeyAtZoom)
                        .count(1)
                        .zoom(zoom)
                        .center(location.latLng)
                        .topLeft(tile.topLeft)
                        .topRight(tile.topRight)
                        .bottomLeft(tile.bottomLeft)
                        .bottomRight(tile.bottomRight)
                        .build();
                clusters.put(quadKeyAtZoom, cluster);
            } else {
                cluster.center = computeWeightedCenter(cluster, location.latLng);
                cluster.count++;
            }
        }
    }

    public static void truncate() {
        String sql = "TRUNCATE TABLE cluster";
        Connection conn = play.db.DB.getConnection();
        CallableStatement stmt = null;
        try {
            stmt = conn.prepareCall(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * For the given {@link Location}, create or update its {@link Cluster} for all the "cluster-able" zoom levels
     * defined in {@link ZoomLevel}.
     *
     * @param location
     */
    public static void createOrUpdate(Location location) {
        for (int zoom = MIN_CLUSTER_ZOOM; zoom <= MAX_CLUSTER_ZOOM; zoom++) {
            String quadKeyAtZoom = location.quadKey.substring(0, zoom);
            Cluster cluster = find.where().eq("quad_key", quadKeyAtZoom).findUnique();

            if (cluster == null) {
                QuadTile tile = getTileFromQuadKey(quadKeyAtZoom);
                cluster = new Cluster.Builder()
                        .quadKey(quadKeyAtZoom)
                        .count(1)
                        .zoom(zoom)
                        .center(location.latLng)
                        .topLeft(tile.topLeft)
                        .topRight(tile.topRight)
                        .bottomLeft(tile.bottomLeft)
                        .bottomRight(tile.bottomRight)
                        .build();
                Ebean.save(cluster);
            } else {
                cluster.center = computeWeightedCenter(cluster, location.latLng);
                cluster.count++;
                Ebean.update(cluster);
            }
        }
    }
}
