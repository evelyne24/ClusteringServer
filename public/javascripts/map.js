
var minZoom = 3;
var maxZoom = 19;
var defZoom = 12;
var defCenter = new google.maps.LatLng(51.504789,  -0.156555);

var map = null;
var markers = [];
var clusters = [];
var maxCluster;

function initialize() {
    var mapOptions = {
      center: defCenter,
      zoom: defZoom,
      minZoom: minZoom,
      maxZoom: maxZoom
    };
    map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);

    google.maps.event.addListener(map, 'idle', function() {
        getClusters(map.getBounds(), map.getZoom() + 1);
    });
}

function parseCluster(key, value) {
    var latLng = new google.maps.LatLng(
      value.center.latitude,
      value.center.longitude
    );
    return {center: latLng, count: value.count, quadKey: key}
}

function addMarker(cluster) {
    var marker = createMarker(cluster);
    markers.push(marker);

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

 // Create a cluster or a single Marker
function createMarker(cluster) {
    return cluster.count > 1 ?
           getClusterMarker(cluster) :
           getSingleMarker(cluster);
}

function getSingleMarker(cluster) {
    return new google.maps.Marker({
        position: cluster.center,
        map: map,
        animation: google.maps.Animation.DROP
    });
}

function getClusterMarker(cluster) {
    return new MarkerWithLabel({
      position: cluster.center,
      map: map,
      icon: { path: google.maps.SymbolPath.CIRCLE,
        fillOpacity: 0.5,
        fillColor: 'ff0000',
        strokeOpacity: 0,
        scale: 5 + 30 * (cluster.count / maxCluster)
      },
      labelContent: cluster.count,
      labelAnchor: new google.maps.Point(50, 10),
      labelClass: "cluster"
    });
}

// Deletes all markers on the map
function clearMap() {
  for(i in markers) {
    markers[i].setMap(null);
  }
  markers = [];
  clusters = [];
}

function getClusters(bounds, zoom) {
    //console.log("Get clusters for bounds: " + bounds + ", zoom: " + zoom);

    var sw = {lat: bounds.getSouthWest().lat(), lng: bounds.getSouthWest().lng()};
    var ne = {lat: bounds.getNorthEast().lat(), lng: bounds.getNorthEast().lng()};

    clearMap();

    jsRoutes.controllers.Application.jsonList(sw, ne, zoom).ajax({
        success: function(data) {

            maxCluster = 0;

            $.each(data, function (key, value) {
                var cluster = parseCluster(key, value);;
                if(cluster.count > maxCluster) {
                    maxCluster = cluster.count;
                }
                clusters.push(cluster);
            });

            for(i in clusters) {
                addMarker(clusters[i]);
            }
        },

        error: function(error) {
           console.log("Error: " + error);
        }
    });
}