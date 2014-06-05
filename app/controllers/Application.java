package controllers;

import clustering.*;
import clustering.implementations.GridClusteringAlgorithm;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import models.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.list;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static clustering.QuadTile.getTileFromQuadKey;
import static clustering.ZoomLevel.Z14;
import static clustering.ZoomLevel.Z19;


public class Application extends Controller {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
            routes.Application.list(0, "name", "asc", "")
    );

    /**
     * Handle default path requests, redirect to computers list
     */
    public static Result index() {
        return GO_HOME;
    }

    /**
     * Display the paginated list of locations.
     *
     * @param page   Current listByName number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order  Sort order (either asc or desc)
     * @param filter Filter applied on location names
     */
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(
                list.render(
                        Location.listByName(page, 10, sortBy, order, filter),
                        sortBy, order, filter
                )
        );
    }


    public static Result createLocations(LatLng sw, LatLng ne, int count) {
        long startTime = System.currentTimeMillis();
        Location.create(sw, ne, count);
        double duration = ((double) (System.currentTimeMillis() - startTime)) / 1000;
        return ok("Generated " + count + " random locations and computed clusters in " + duration + " seconds.");
    }


    /**
     * Update db locations with random coordinates and computed quad key at the max zoom level.
     */
    public static Result update(LatLng sw, LatLng ne) {
       /* int page = 0;
        int totalSize = 0;
        Page<Location> result;
        do {
            result = Location.list(page, 1000);
            Point tmp=new Point();
            for (Location location : result.getList()) {
                LatLng latLng = RandomLocationsGenerator.generate(sw, ne);
                location.latitude = latLng.latitude;
                location.longitude = latLng.longitude;
                location.quadKey = LocationUtils.getQuadKey(latLng, Z19);
                LocationUtils.latLngToWorldPoint(latLng,Z19,tmp);
                location.x= Long.valueOf(tmp.x);
                location.y= Long.valueOf(tmp.y);
            }
            Ebean.save(result.getList());
            page++;
            totalSize += result.getList().size();
        } while (result.getList().size() > 0);*/

        createLocations(sw, ne, 10000);

        return ok("generated 100000 locations.");
    }

    /**
     * Show Google Maps centered on a default location.
     */
    public static Result map() {
        return ok(views.html.map.render());
    }


    /**
     * Return the JSON representation for all clusters found within the given bounds
     * at the specified zoom level.
     *
     * @param sw   south west bound {@link LatLng}
     * @param ne   north east bound {@link LatLng}
     * @param zoom the zoom level of the map.
     * @return
     */
    public static Result jsonList(LatLng sw, LatLng ne, int zoom) {
        try {
            if (zoom > Z14.zoom) {
                return ok(Json.toJson(getSinleLocations(sw, ne, zoom)));
            }
            Map<String, Cluster> clusters = getClustersCount(sw, ne, zoom);
            return ok(Json.toJson(clusters.values()));

        } catch (SQLException e) {
            return internalServerError(e.getMessage());
        }
    }

    private static Map<String, Cluster> getClustersCount(LatLng sw, LatLng ne, int zoom) throws SQLException {

        long time = System.currentTimeMillis();
        String sql = "SELECT SUBSTRING(quad_key,1,?) as cqk, name, latitude, longitude, x, y " +
                "                FROM location " +
                "                WHERE latitude > ? AND latitude < ?" +
                "                AND longitude >? AND longitude < ?";

        Connection conn = play.db.DB.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, zoom);
            stmt.setDouble(2, sw.latitude);
            stmt.setDouble(3, ne.latitude);
            stmt.setDouble(4, sw.longitude);
            stmt.setDouble(5, ne.longitude);

            // log.debug("Executing SQL {}", stmt.toString());
            ResultSet result = stmt.executeQuery();
            Map<String, Cluster> results = new GridClusteringAlgorithm().getClusters(result);
            return results;
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


    private static List<Cluster> getSinleLocations(LatLng sw, LatLng ne, int zoom) throws SQLException {

        String sql = "SELECT SUBSTRING(quad_key,1,?) as cqk, name, latitude, longitude" +
                "                FROM location " +
                "                WHERE latitude > ? AND latitude < ?" +
                "                AND longitude > ? AND longitude < ?";

        Connection conn = play.db.DB.getConnection();
        PreparedStatement stmt = null;
        List<Cluster> results = new ArrayList<Cluster>();
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, zoom);
            stmt.setDouble(2, sw.latitude);
            stmt.setDouble(3, ne.latitude);
            stmt.setDouble(4, sw.longitude);
            stmt.setDouble(5, ne.longitude);

            log.debug("Executing SQL {}", stmt.toString());

            ResultSet result = stmt.executeQuery();

            while (result.next()) {

                String quadKey = result.getString("cqk");
                QuadTile tile = getTileFromQuadKey(quadKey);
                log.debug("result " + tile.toString(), tile.toString());
                Location location = new Location();
                location.name = result.getString("name");
                location.latitude = result.getDouble("latitude");
                location.longitude = result.getDouble("longitude");
                Cluster.Builder cluster = new Cluster.Builder()
                        .center(new LatLng(location.latitude, location.longitude))
                        .topLeft(tile.topLeft)
                        .topRight(tile.topRight)
                        .bottomLeft(tile.bottomLeft)
                        .bottomRight(tile.bottomRight)
                        .quadKey(quadKey);


                cluster.location(location);
                cluster.quadKey(quadKey);
                cluster.count(1);
                results.add(cluster.build());
            }


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
        log.debug("returning results", results.size());
        return results;
    }
}
