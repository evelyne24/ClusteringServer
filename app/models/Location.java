package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;

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
    public static Finder<Long, Location> find = new Finder<Long, Location>(Long.class, Location.class);

    /**
     * Return a page of Locations.
     *
     * @param page     Page to display
     * @param pageSize Number of locations per page
     * @param sortBy   Location property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public static Page<Location> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
            find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }
}
