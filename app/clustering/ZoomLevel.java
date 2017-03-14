package clustering;


import static clustering.LocationUtils.*;

/**
 * The pre-computed tile size per zoom level.
 */
public enum ZoomLevel {

    Z0(0, 256),
    Z1(1, 512),
    Z2(2, 1024),
    Z3(3, 2048),
    Z4(4, 4096),
    Z5(5, 8192),
    Z6(6, 16384),
    Z7(7, 32768),
    Z8(8, 65536),
    Z9(9, 131072),
    Z10(10, 262144),
    Z11(11, 524288),
    Z12(12, 1048576),
    Z13(13, 2097152),
    Z14(14, 4194304),
    Z15(15, 8388608),
    Z16(16, 16777216),
    Z17(17, 33554432),
    Z18(18, 67108864),
    Z19(19, 134217728),
    Z20(20, 268435456),
    Z21(21, 536870912),
    Z22(22, 1073741824),
    Z23(23, 2147483648l);

    public static final int MIN_ZOOM = 0;
    public static final int MAX_ZOOM = 19;

    public static ZoomLevel get(int zoom) {
        return ZoomLevel.values()[clip(zoom, MIN_ZOOM, MAX_ZOOM)];
    }

    public final int zoom;
    public final long mapSize;
    public final Point maxTiles;

    ZoomLevel(int zoom, long mapSize) {
        this.zoom = zoom;
        this.mapSize = mapSize;
        maxTiles = latLngToWorldPoint(new LatLng(MAX_LATITUDE, MAX_LONGITUDE), this);
    }


}

