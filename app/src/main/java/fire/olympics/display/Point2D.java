package fire.olympics.display;
public class Point2D {
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // See here for why this is so complicated:
    // https://www.sitepoint.com/implement-javas-equals-method-correctly/
    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        Point2D other = (Point2D) o;
        return x == other.x && y == other.y;
    }

    public Point2D clone() {
        return new Point2D(x, y);
    }
}
