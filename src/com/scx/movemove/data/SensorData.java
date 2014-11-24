
package com.scx.movemove.data;

/*
 * Accelerometer data and Gyroscope data are all 3-dimensional
 * This class is used for store the sensor data
 */
/**
 * <h1>Sensor Data</h1> The Sensor data class stores sensor information for accelerometer and
 * gyroscope which are both 3-dimensional data with a system time variable
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class SensorData {

    private long timestamp;
    double x;
    double y;
    double z;

    public SensorData(long timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "t=" + this.timestamp + ", x=" + this.x + ", y=" + this.y
                + ", z=" + this.z;
    }

}
