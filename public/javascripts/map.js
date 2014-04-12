var defNE = {lat:52.513422,lng:-0.200195};
var defSW = {lat:51.039235,lng:-2.164307};
var map = null;

function initialize() {
    var mapOptions = {
      center: new google.maps.LatLng(51.504789,-0.156555),
      zoom: 8
    };
    map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
    fitMapBounds(defSW, defNE);
}

function parseCluster(key, value) {
    // Create a LatLng point
    var latLng = new google.maps.LatLng(
      value.center.latitude,
      value.center.longitude
    );

    return {center: latLng, count: value.count, quadKey: key}
}

function addMarker(cluster) {
    // Create a Marker
    var marker = new google.maps.Marker({
      position: cluster.center,
      map: map,
      animation: google.maps.Animation.DROP
    });

    // Create the tooltip and its text
    var infoWindow = new google.maps.InfoWindow();
    var html = "Cluster " + cluster.quadKey + " (" + cluster.count + ")";

    // Add a listener for the marker
    google.maps.event.addListener(marker, 'click', function() {
         infoWindow.setContent(html);
         infoWindow.open(map, marker);
    });

    // Add the marker to the map
    marker.setMap(map);
}

function getClusters(sw, ne, zoom) {
    jsRoutes.controllers.Application.jsonList(sw, ne, zoom).ajax({
        success: function(data) {
            $.each(data, function (key, value) {
                addMarker(parseCluster(key, value));
            });
        },

        error: function(error) {
           console.log("Error: " + error);
        }
    });
}

function fitMapBounds(sw, ne) {
    google.maps.event.addListener(map, 'zoom_changed', function() {
      getClusters(sw, ne, map.getZoom() + 2); // get the clusters for a bigger zoom level
    });

    map.fitBounds(new google.maps.LatLngBounds(
      new google.maps.LatLng(sw.lat, sw.lng),
      new google.maps.LatLng(ne.lat, ne.lng)
    ));
}
