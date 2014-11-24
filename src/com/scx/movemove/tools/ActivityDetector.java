
package com.scx.movemove.tools;

import com.scx.movemove.activities.WelcomeActivity;
import com.scx.movemove.data.SensorData;
import com.scx.movemove.data.UserInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * <h1>Activity Detector</h1> The Activity Detector is used to detection the motion of a cell phone
 * user which can differentiate activities including walking, running, climbing stairs, descending
 * stairs and not moving
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class ActivityDetector {
    private UserInfo mUser = WelcomeActivity.DEFAULT_USER;
    public Map<String, Double> mMetMap = WelcomeActivity.sMetMap;
    private String mActivityType = "Not moving";
    private double mDetectedSpeed;
    private double mRoundSpeed;
    private double mMet;
    private double mAverageAccel;
    private double mTime;
    private ArrayList<SensorData> mSensorAccel;
    private ArrayList<SensorData> mSensorGyro;

    public ActivityDetector(ArrayList<SensorData> Acc,
            ArrayList<SensorData> Gyro) {
        this.mSensorAccel = new ArrayList<SensorData>(Acc);
        this.mSensorGyro = new ArrayList<SensorData>(Gyro);
        this.mDetectedSpeed = 0.0;
        this.mRoundSpeed = 0.0;
        this.setActivityType(this.ActivityType());
        this.mTime = this.calculateTime();
        this.mMet = this.getMet();

    }

    public String ActivityType() {
        // handle Accelerometer data
        float sum_Acc = 0;
        float minY_Acc_count = 0;
        float minY_Acc_percent = 0;
        double minY_Acc = 0;
        int count_Acc = 0;

        for (int i = 0; i < this.mSensorAccel.size() - 1; i++) {
            SensorData data = this.mSensorAccel.get(i);
            double tmp = Math.pow(data.getX(), 2) + Math.pow(data.getY(), 2)
                    + Math.pow(data.getZ(), 2);
            sum_Acc += Math.pow(tmp, 0.5);
            if (data.getY() < -12) {
                minY_Acc_count += 1;
            }
            if (data.getY() < minY_Acc) {
                minY_Acc = data.getY();
            }
            count_Acc++;
        }
        this.mAverageAccel = sum_Acc / count_Acc;
        minY_Acc_percent = minY_Acc_count / count_Acc;

        // handle gyroscope data
        float sumX_Gyro = 0;
        int count_Gyro = 0;
        for (int i = 0; i < this.mSensorGyro.size() - 1; i++) {
            SensorData data = this.mSensorGyro.get(i);
            double x = data.getX();
            sumX_Gyro += x;
            count_Gyro++;
        }
        float ave_XGyro = sumX_Gyro / count_Gyro;

        // make decision

        if (this.mAverageAccel > this.mUser.getBd_ave_Acc_run()
                && minY_Acc < this.mUser.getBd_minY_Acc_run()) {

            this.setActivityType("running");

            this.mDetectedSpeed = this.mUser.getRunSpeed(this.mAverageAccel);
            this.roundSpeedrun(this.mDetectedSpeed);
        } else {

            if (minY_Acc < this.mUser.getBd_minY_Acc_ds()
                    && minY_Acc_percent > 0.0001f) {
                this.setActivityType("descending stairs");
                this.mMet = this.mMetMap.get(this.getActivityType());
            } else if (ave_XGyro < this.mUser.getBd_ave_XGyro()
                    && this.mAverageAccel > this.mUser.getBd_ave_Acc_stable()) {

                this.setActivityType("walking");
                this.mDetectedSpeed = this.mUser
                        .getWalkSpeed(this.mAverageAccel);
                this.roundSpeedwalk(this.mDetectedSpeed);
            } else if (this.mAverageAccel > this.mUser.getBd_ave_Acc_stable()) {
                this.setActivityType("climbing stairs");
                this.mMet = this.mMetMap.get(this.getActivityType());

            } else {
                this.setActivityType("Not moving");

            }

        }
        return this.getActivityType();

    }

    public double calculateTime() {

        long startA = this.mSensorAccel.get(0).getTimestamp();
        long startB = this.mSensorGyro.get(0).getTimestamp();

        long endA = this.mSensorAccel.get(this.mSensorAccel.size() - 1)
                .getTimestamp();
        long endB = this.mSensorGyro.get(this.mSensorGyro.size() - 1)
                .getTimestamp();
        double timeA = (endA - startA) / 1000000000.0d;
        double timeB = (endB - startB) / 1000000000.0d;
        this.mTime = Math.max(timeA, timeB);
        return this.mTime;
    }

    public double getMet() {
        if (this.getActivityType().equals("walking")
                || this.getActivityType().equals("running")) {
            this.mMet = this.mMetMap.get(this.getActivityType() + "_"
                    + this.mRoundSpeed);
        } else {
            this.mMet = this.mMetMap.get(this.getActivityType());
        }
        return this.mMet;
    }

    public void roundSpeedrun(double speed) {
        if (this.mDetectedSpeed < 4.25d) {
            this.mRoundSpeed = 4.0d;
        } else if (this.mDetectedSpeed > 10.75d) {
            this.mRoundSpeed = 11.0d;
        } else {
            double diff = this.mDetectedSpeed - (int) this.mDetectedSpeed;
            if (diff < 0.25d) {
                this.mRoundSpeed = ((int) this.mDetectedSpeed);
            } else if (diff < 0.75d) {
                this.mRoundSpeed = 0.5d + (int) this.mDetectedSpeed;
            } else {
                this.mRoundSpeed = 1.0d + (int) this.mDetectedSpeed;
            }
        }
    }

    public void roundSpeedwalk(double speed) {
        if (this.mDetectedSpeed < 1.75d) {
            this.mRoundSpeed = 4.0d;
        } else if (this.mDetectedSpeed > 4.75d) {
            this.mRoundSpeed = 11.0d;
        } else {
            double diff = this.mDetectedSpeed - (int) this.mDetectedSpeed;
            if (diff < 0.25d) {
                this.mRoundSpeed = ((int) this.mDetectedSpeed);
            } else if (diff < 0.75d) {
                this.mRoundSpeed = 0.5d + (int) this.mDetectedSpeed;
            } else {
                this.mRoundSpeed = 1.0d + (int) this.mDetectedSpeed;
            }
        }
    }

    public double getRoundSpeed() {
        return this.mRoundSpeed;
    }

    public String getActivityType() {
        return this.mActivityType;
    }

    public void setActivityType(String activityType) {
        this.mActivityType = activityType;
    }

}
