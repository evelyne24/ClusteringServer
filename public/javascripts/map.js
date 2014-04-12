var defNE = {lat:52.513422,lng:-0.200195};
var defSW = {lat:51.039235,lng:-2.164307};
var defZoom = 12;
var map = null;

function initialize() {
    var mapOptions = {
      center: new google.maps.LatLng(51.504789,-0.156555),
      zoom: defZoom
    };
    map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
    fitMapBounds(defSW, defNE, defZoom);
}

function fitMapBounds(sw, ne, zoom) {
    jsRoutes.controllers.Application.jsonList(sw, ne, zoom).ajax({
        success: function(data) {
          map.fitBounds(new google.maps.LatLngBounds(
            new google.maps.LatLng(sw.lat, sw.lng),
            new google.maps.LatLng(ne.lat, ne.lng)
          ));
          console.log(data);
        },
        error: function(error) {
           console.log("Error: " + error);
        }
    });
};
