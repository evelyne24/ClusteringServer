package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import play.db.ebean.Model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static clustering.LocationUtils.getQuadKey;
import static clustering.LocationsGenerator.randomizeLatLng;
import static clustering.LocationsGenerator.randomizeName;
import static clustering.ZoomLevel.Z19;

/**
 * Created by evelina on 10/04/2014.
 */
@Entity
public class Location extends Model {

    @Id
    public Long id;

    @Embedded
    public LatLng latLng;

    public String quadKey;

    public String name;

    /**
     * Generic query helper for entity Location with id Long
     */
    public static Finder<Long, Location> find = new Finder<Long, Location>(Long.class, Location.class);

    /**
     * Return a paginated get of Locations filtered by name.
     *
     * @param page     Page to display
     * @param pageSize Number of locations per listByName
     * @param sortBy   Location property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public static Page<Location> listByName(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    /**
     * Return a paginated get of Locations.
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static Page<Location> list(int page, int pageSize) {
        return find.where()
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }

    /**
     * Create locations with random coordinates within the given bounds.
     *
     * @param sw    south west bound {@link LatLng}
     * @param ne    north east bound {@link LatLng}
     * @param count how many locations to create.
     */
    public static void create(LatLng sw, LatLng ne, int count) {
        List<Location> locations = new ArrayList<Location>(count);
        Map<String, Cluster> clusters = new HashMap<String, Cluster>();

        Ebean.beginTransaction();
        Location.truncate();
        Cluster.truncate();
        Ebean.endTransaction();

        for (int i = 1; i <= count; i++) {
            Location location = new Location();
            LatLng latLng = randomizeLatLng(sw, ne);
            location.latLng = latLng;
            location.quadKey = getQuadKey(latLng, Z19);
            location.name = randomizeName();
            locations.add(location);
            Cluster.createOrUpdate(location, clusters);
        }

        Ebean.save(locations);
        Ebean.save(clusters.values());
    }


    public static void truncate() {
        String sql = "TRUNCATE TABLE location";
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
}
