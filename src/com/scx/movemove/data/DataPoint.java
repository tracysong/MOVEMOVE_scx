
package com.scx.movemove.data;

/**
 * <h1>Data Point</h1> The Data Point class is used to represent the two variable in polynomial
 * regression function
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class DataPoint {
    private double x;
    private double y;

    public DataPoint(double x, double y) {
        this.x = x;
        this.y = y;

    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
