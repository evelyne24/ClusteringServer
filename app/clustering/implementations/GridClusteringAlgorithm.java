package clustering.implementations;

import clustering.*;
import controllers.Application;
import models.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static clustering.QuadTile.getTileFromQuadKey;

/**
 * Created by anatriellop on 15/05/2014.
 */
public class GridClusteringAlgorithm extends AbstractClusteringAgorithm {
    private static Logger log = LoggerFactory.getLogger(Application.class);
    private static Point tmpPointA=new Point();
    private static Point tmpPointB=new Point();
    @Override
    public  Map<String, Cluster> getClusters(ResultSet result) {
        long time= System.currentTimeMillis();
        try {
            Map<String, Cluster> results = new HashMap<String, Cluster>();
            while (result.next()) {

                String quadKey = result.getString("cqk");
                Cluster cluster = results.get(quadKey);
                long x=result.getLong("x");
                long y=result.getLong("y");
                if (cluster == null) {
                    QuadTile tile = getTileFromQuadKey(quadKey);
                    Location location = new Location();
                    location.name = result.getString("name");

                    LatLng locationCenter = new LatLng(result.getDouble("latitude"), result.getDouble("longitude"));
                    location.latitude = locationCenter.latitude;
                    location.longitude = locationCenter.longitude;
                    location.x=x;
                    location.y=y;
                    cluster = new Cluster.Builder()
                            .center(locationCenter)
                            .topLeft(tile.topLeft)
                            .topRight(tile.topRight)
                            .bottomLeft(tile.bottomLeft)
                            .bottomRight(tile.bottomRight)
                            .count(1)
                            .quadKey(quadKey).location(location).build();
                    results.put(quadKey, cluster);
                } else {
                    cluster.center = updateClusterCenter(cluster, x,y);
                    cluster.count = cluster.count + 1;
                }
            }
            return results;
        } catch (SQLException e) {
            return null;
        }
    }

    private static LatLng updateClusterCenter(Cluster cluster,long x, long y) {
        tmpPointA.set(cluster.location.x,cluster.location.y);
        tmpPointB.set(x,y);
        long xRes =  ((tmpPointA.x * cluster.count + tmpPointB.x) / (long) (cluster.count + 1));
        long yRes =  ((tmpPointA.y * cluster.count + tmpPointB.y) / (long) (cluster.count + 1));
        tmpPointA.set(xRes, yRes);
        return LocationUtils.worldPointToLatLng(tmpPointA, ZoomLevel.get(19));
    }
}
