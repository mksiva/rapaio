package rapaio.graphics.base;

import rapaio.core.BaseMath;

/**
 * @author tutuianu
 */
public class Range {

    private double x1 = Double.NaN;
    private double y1 = Double.NaN;
    private double x2 = Double.NaN;
    private double y2 = Double.NaN;

    public void union(Range range) {
        union(range.getX1(), range.getY1());
        union(range.getX2(), range.getY2());
    }

    public void union(double x, double y) {
        if (x == x) {
            x1 = BaseMath.min(x1 != x1 ? x : x1, x);
            x2 = BaseMath.max(x2 != x2 ? x : x2, x);
        }
        if (y == y) {
            y1 = BaseMath.min(y1 != y1 ? y : y1, y);
            y2 = BaseMath.max(y2 != y2 ? y : y2, y);
        }
    }

    public boolean contains(double x, double y) {
        return x1 <= x && x <= x2 && y1 <= y && y <= y2;
    }

    public double getWidth() {
        return x2 - x1;
    }

    public double getHeight() {
        return y2 - y1;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public int getProperDecimalsX() {
        int decimals = 0;
        double max = BaseMath.abs(x2 - x1);
        while (max <= 10.) {
            max *= 10;
            decimals++;
            if (decimals > 7) {
                return decimals;
            }
        }
        return decimals;
    }

    public int getProperDecimalsY() {
        int decimals = 0;
        double max = BaseMath.abs(y2 - y1);
        while (max <= 10.) {
            max *= 10;
            decimals++;
            if (decimals > 7) {
                return decimals;
            }
        }
        return decimals;
    }
}
