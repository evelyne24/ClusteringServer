package controllers;

import models.Cluster;
import models.LatLng;
import models.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.list;

import static play.libs.Json.toJson;


public class Application extends Controller {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(routes.Application.getLocations(0, "name", "asc", ""));

    /**
     * Handle default path requests, redirect to computers page
     */
    public static Result index() {
        return GO_HOME;
    }

    /**
     * Show Google Maps centered on a default location.
     */
    public static Result map() {
        return ok(views.html.map.render());
    }

    public static Result getLocations(int page, String sortBy, String order, String filter) {
        return ok(list.render(Location.listByName(page, 10, sortBy, order, filter), sortBy, order, filter));
    }

    public static Result createLocations(LatLng sw, LatLng ne, int count) {
        long startTime = System.currentTimeMillis();
        Location.create(sw, ne, count);
        double duration = ((double) (System.currentTimeMillis() - startTime)) / 1000;
        return ok("Generated " + count + " random locations and computed clusters in " + duration + " seconds.");
    }

    public static Result getClusters(LatLng sw, LatLng ne, int zoom) {
        log.info("Getting clusters in bounds {} - {} at zoom {}", sw, ne, zoom);
        return ok(toJson(Cluster.get(sw, ne, zoom)));
    }


}
