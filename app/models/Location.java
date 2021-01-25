package models;

import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;
import play.data.format.*;
import play.data.validation.*;

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

    public String quadKey;

    public String name;

    /**
     * Generic query helper for entity Location with id Long
     */
    public static Finder<Long, Location> find = new Finder<Long, Location>(Location.class);

    /**
     * Return a list of Locations filtered by name.
     *
     * @param page     Page to display
     * @param pageSize Number of locations per listByName
     * @param sortBy   Location property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public static PagedList<Location>  listByName(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagedList(page,pageSize);
    }

    /**
     * Return a list of Locations.
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static PagedList<Location> list(int page, int pageSize) {
        return find.where()
                .findPagedList(page,pageSize);

    }
}
