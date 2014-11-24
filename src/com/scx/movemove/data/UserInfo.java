
package com.scx.movemove.data;

/**
 * <h1>User Information</h1> The UserInfo class stores personal information for a user including
 * their name, age, weight, height, personal detection parameter and personal daily fitness target
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class UserInfo {
    private String mName;
    private int mHeight;
    private int mWeight;
    private int mAge;
    private int mMale;
    private int mDoneTraining;
    private int mHeightRange;
    private double[] mBounds;
    private double[] mRegreCoef;
    int calorie_daily;

    public UserInfo(String name, int height, int weight, int age, int male,
            int doneT) {
        this.mName = name;
        this.mHeight = height;
        this.mWeight = weight;
        this.mAge = age;
        this.mMale = male;
        this.mDoneTraining = doneT;
        this.mBounds = new double[5];
        this.mRegreCoef = new double[8];
        this.setHeightR();
        this.setDefaultBD();
        this.setDefaultCoef();
        this.setDef_DailyCalorie();
    }

    public String getName() {
        return this.mName;

    }

    public void setName(String name) {
        this.mName = name;

    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;

    }

    public int getWeight() {
        return this.mWeight;
    }

    public void setWeight(int height) {
        this.mHeight = height;

    }

    public void setAge(int age) {
        this.mAge = age;
    }

    public int getAge() {
        return this.mAge;
    }

    public int getMale() {
        return this.mMale;
    }

    public void setMale(int male) {
        this.mMale = male;
    }

    public int getDoneT() {
        return this.mDoneTraining;
    }

    public void setDoneT(int doneT) {
        this.mMale = doneT;
    }

    public void setHeightR() {
        if (this.getHeight() < 165) {
            this.mHeightRange = 0;
        } else if (this.getHeight() < 175) {
            this.mHeightRange = 1;
        } else if (this.getHeight() < 185) {
            this.mHeightRange = 2;
        } else {
            this.mHeightRange = 3;
        }
    }

    public int getHeightR() {
        return this.mHeightRange;
    }

    public void setDefaultBD() {
        switch (this.mHeightRange) {
            case 0:
                this.mBounds[0] = 4;
                this.mBounds[1] = -10;
                this.mBounds[2] = -13;
                this.mBounds[3] = 0.1;
                this.mBounds[4] = 0.1;
            case 1:
                this.mBounds[0] = 4;
                this.mBounds[1] = -10;
                this.mBounds[2] = -13;
                this.mBounds[3] = 0.1;
                this.mBounds[4] = 0.1;
            case 2:
                this.mBounds[0] = 4;
                this.mBounds[1] = -10;
                this.mBounds[2] = -13;
                this.mBounds[3] = 0.1;
                this.mBounds[4] = 0.1;
            case 3:
                this.mBounds[0] = 4;
                this.mBounds[1] = -10;
                this.mBounds[2] = -13;
                this.mBounds[3] = 0.1;
                this.mBounds[4] = 0.1;

        }
    }

    public double getBd_ave_Acc_run() {
        return this.mBounds[0];
    }

    public void setBd_ave_Acc_run(double data) {
        this.mBounds[0] = data;
    }

    public double getBd_minY_Acc_run() {
        return this.mBounds[1];
    }

    public void setBd_minY_Acc_run(double data) {
        this.mBounds[1] = data;
    }

    public double getBd_minY_Acc_ds() {
        return this.mBounds[2];
    }

    public void setBd_minY_Acc_ds(double data) {
        this.mBounds[2] = data;
    }

    public double getBd_ave_XGyro() {
        return this.mBounds[3];
    }

    public void setBd_ave_XGyro(double data) {
        this.mBounds[3] = data;
    }

    public double getBd_ave_Acc_stable() {
        return this.mBounds[4];
    }

    public void setBd_ave_Acc_stable(double data) {
        this.mBounds[4] = data;
    }

    public void setDefaultCoef() {
        switch (this.mHeightRange) {
            case 0:
                this.mRegreCoef[0] = 1;
                this.mRegreCoef[1] = 2;
                this.mRegreCoef[2] = 2;
                this.mRegreCoef[3] = 3;
                this.mRegreCoef[4] = 2;
                this.mRegreCoef[5] = 1;
                this.mRegreCoef[6] = 2;
                this.mRegreCoef[7] = 2;

            case 1:
                this.mRegreCoef[0] = 1;
                this.mRegreCoef[1] = 2;
                this.mRegreCoef[2] = 2;
                this.mRegreCoef[3] = 3;
                this.mRegreCoef[4] = 2;
                this.mRegreCoef[5] = 1;
                this.mRegreCoef[6] = 2;
                this.mRegreCoef[7] = 2;
            case 2:
                this.mRegreCoef[0] = 1;
                this.mRegreCoef[1] = 2;
                this.mRegreCoef[2] = 2;
                this.mRegreCoef[3] = 3;
                this.mRegreCoef[4] = 2;
                this.mRegreCoef[5] = 1;
                this.mRegreCoef[6] = 2;
                this.mRegreCoef[7] = 2;
            case 3:
                this.mRegreCoef[0] = 1;
                this.mRegreCoef[1] = 2;
                this.mRegreCoef[2] = 2;
                this.mRegreCoef[3] = 3;
                this.mRegreCoef[4] = 2;
                this.mRegreCoef[5] = 1;
                this.mRegreCoef[6] = 2;
                this.mRegreCoef[7] = 2;
        }

    }

    public double getW0() {
        return this.mRegreCoef[0];
    }

    public double getW1() {
        return this.mRegreCoef[1];
    }

    public double getW2() {
        return this.mRegreCoef[2];
    }

    public double getW3() {
        return this.mRegreCoef[3];
    }

    public double getR0() {
        return this.mRegreCoef[4];
    }

    public double getR1() {
        return this.mRegreCoef[5];
    }

    public double getR2() {
        return this.mRegreCoef[6];
    }

    public double getR3() {
        return this.mRegreCoef[7];
    }

    public void setWalk(double a0, double a1, double a2, double a3) {
        this.mRegreCoef[0] = a0;
        this.mRegreCoef[1] = a1;
        this.mRegreCoef[2] = a2;
        this.mRegreCoef[3] = a3;
    }

    public void setRun(double a0, double a1, double a2, double a3) {
        this.mRegreCoef[4] = a0;
        this.mRegreCoef[5] = a1;
        this.mRegreCoef[6] = a2;
        this.mRegreCoef[7] = a3;
    }

    public void setBd(double a0, double a1, double a2, double a3, double a4) {
        this.mBounds[0] = a0;
        this.mBounds[1] = a1;
        this.mBounds[2] = a2;
        this.mBounds[3] = a3;
        this.mBounds[4] = a4;
    }

    public double getWalkSpeed(double acc) {
        double y = 0.0;
        for (int j = 3; j >= 0; j--) {
            y = this.mRegreCoef[j] + (acc * y);
        }
        return y;
    }

    public double getRunSpeed(double acc) {
        double y = 0.0;
        for (int j = 3; j >= 0; j--) {
            y = this.mRegreCoef[j + 4] + (acc * y);
        }
        return y;
    }

    /*
     * Use Mifflin St Jeor Equation to calculate the default daily calorie target(kal/day)
     */
    void setDef_DailyCalorie() {
        int s = -161;
        if (this.getMale() == 1) {
            s = 5;
        }
        this.calorie_daily = (int) ((10 * this.mWeight + 6.25 * this.mHeight
                + 5 * this.mAge + s) * 1000);

    }

    void setCalorieD(int c) {

        this.calorie_daily = c;

    }

    public int getCalorie() {
        return this.calorie_daily;
    }
}
