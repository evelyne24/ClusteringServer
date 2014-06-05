package clustering;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Created by anatriellop on 15/05/2014.
 */
public abstract class AbstractClusteringAgorithm {

    public abstract Map<String, Cluster> getClusters(ResultSet items);
}
