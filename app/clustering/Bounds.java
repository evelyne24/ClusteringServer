package clustering;

/**
 * Created by anatriellop on 15/05/2014.
 */
public class Bounds {

    public final double midX;
    public final double midY;
    public final double left;
    public final double right;
    public final double top;
    public final double bottom;

    public Bounds(double left, double right, double top, double bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        midX = (left +right) / 2;
        midY = (top + bottom) / 2;
    }


    public boolean contains(double x, double y) {
        return left <= x && x <= right && top <= y && y <= bottom;
    }

    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    public boolean intersects(double left, double right, double top, double bottom) {
        return left < this.right && this.left < right && top < this.bottom && this.top < bottom;
    }

    public boolean intersects(Bounds bounds) {
        return intersects(bounds.left, bounds.right, bounds.top, bounds.bottom);
    }

    public boolean contains(Bounds bounds) {
        return bounds.left >= left && bounds.right <= right && bounds.top >= top && bounds.bottom <= bottom;
    }
}
