package models;

import clustering.LatLng;
import clustering.LocationUtils;
import clustering.Point;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static clustering.LocationUtils.getQuadKey;
import static clustering.RandomLocationsGenerator.generate;
import static clustering.ZoomLevel.Z19;

/**
 * Created by evelina on 10/04/2014.
 */
@Entity
public class Location extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public Double latitude;

    @Constraints.Required
    public Double longitude;

    public Long x;

    public Long y;

    public String quadKey;

    public String name;

    /**
     * Generic query helper for entity Location with id Long
     */
    public static Finder<Long, Location> find = new Finder<Long, Location>(Long.class, Location.class);

    /**
     * Return a list of Locations filtered by name.
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
     * Return a list of Locations.
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

        Ebean.beginTransaction();
        Location.truncate();
        Ebean.endTransaction();
        Point tmp=new Point();
        for (int i = 1; i <= count; i++) {
            Location location = new Location();
            LatLng latLng = generate(sw, ne);
            location.latitude = latLng.latitude;
            location.longitude= latLng.longitude;
            LocationUtils.latLngToWorldPoint(latLng, Z19, tmp);
            location.x= Long.valueOf(tmp.x);
            location.y= Long.valueOf(tmp.y);
            location.quadKey = getQuadKey(latLng, Z19);
            location.name = UUID.randomUUID().toString();
            locations.add(location);
        }

        Ebean.save(locations);
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
