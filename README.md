ClusteringServer
================

A simple Play Framework server to demonstrate location clustering with quad keys.

### New Features
  - position the cluster circle based on the avg values of all the lat/long grouped into given cluster
  - New /location/add Rest API to support Adding random point for testing
  - Play framework 2.5.x Upgrade
  - Added more Zoom Level 
  
### Update Google API key

in app/view/map.scala.html change google API key

``` javascript
   <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDHmQxygMmLD-Yel0iHZZ1XqigLKIcGq_0">
```

### TODO:

- pre-fetch bigger bounds than the visible screen
- diff the pins/tiles to avoid clearing and redrawing them on the map (flickering)

### Screenshot
![Screenshot](ScreenShot.png)
