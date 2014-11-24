
package com.scx.movemove.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scx.movemove.data.R;
import com.scx.movemove.data.SensorData;
import com.scx.movemove.data.UserInfo;
import com.scx.movemove.tools.ActivityDetector;
import com.scx.movemove.tools.AlarmReceiver;
import com.scx.movemove.view.MyProgress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * <h1>Tracking Mode Activity</h1> in the Tracking Mode Activity, user activities are tracked, the
 * amount of calorie burned is calculated and advice are given here
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class TrackingModeActivity extends Activity implements OnClickListener,
        SensorEventListener {
    private SensorManager mSensorManager;
    private Button mBtnResume, mBtnPause, mBtnWalk, mBtnRun, mBtnCS, mBtnDS,
            mBtnBike, mBtnOtheruser;
    public static double sCurrentWalkAmount;
    public static double sCurrentRunAmount;
    public static double sCurrentDsAmount;
    public static double sCurrentUsAmount;
    public static double sCurrentStableAmount;
    public static double sTotalCalorie;
    public static double sRemainCalorie;
    public static double sExersicedTime;

    private ActivityDetector mDetector;
    private SimpleDateFormat mSimpleDataformate = null;
    private Sensor mSensorGyro;
    private Sensor mSensorAcce;
    private ArrayList<SensorData> mSensorDataAccel;
    private ArrayList<SensorData> mSensorDataGyro;
    private float mGravity[] = new float[3];
    private float mAcceValues[] = new float[] {
            0, 0, 0
    };
    private int mSensorCount;
    private double mWalkTime;
    private double mRunTime;
    private double mCsTime;
    private double mDsTime;
    private double mBikeTime;
    private TextView mActivity;
    private TextView mCalorie;
    private UserInfo mUser = WelcomeActivity.DEFAULT_USER;
    private MyProgress mPbHor;
    private Thread mLogoTimer;

    private Handler mWatchdogHandler;
    private Runnable mWatchdog;
    private static final long WATCHDOG_PERIOD = 300000; // 5 minutes

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.tracking_mode);
        this.mCalorie = (TextView) this.findViewById(R.id.Calorie);
        this.mActivity = (TextView) this.findViewById(R.id.activity);
        this.mPbHor = (MyProgress) this.findViewById(R.id.pbHor);
        this.mBtnResume = (Button) this.findViewById(R.id.Resume);
        this.mBtnPause = (Button) this.findViewById(R.id.Pause);
        this.mBtnWalk = (Button) this.findViewById(R.id.btnWalk1);
        this.mBtnRun = (Button) this.findViewById(R.id.btnRun1);
        this.mBtnCS = (Button) this.findViewById(R.id.btnClimbingStairs1);
        this.mBtnDS = (Button) this.findViewById(R.id.btnDescendingStairs1);
        this.mBtnBike = (Button) this.findViewById(R.id.btnBike);
        this.mBtnWalk.setOnClickListener(this);
        this.mBtnRun.setOnClickListener(this);
        this.mBtnCS.setOnClickListener(this);
        this.mBtnDS.setOnClickListener(this);
        this.mBtnBike.setOnClickListener(this);
        this.mBtnResume.setOnClickListener(this);
        this.mBtnPause.setOnClickListener(this);
        this.mBtnOtheruser = (Button) this.findViewById(R.id.btnOther1);
        this.mBtnOtheruser.setOnClickListener(this);
        TrackingModeActivity.sTotalCalorie = 0;
        TrackingModeActivity.sRemainCalorie = this.mUser.getCalorie();
        TrackingModeActivity.sCurrentWalkAmount = 0;
        TrackingModeActivity.sCurrentRunAmount = 0;
        TrackingModeActivity.sCurrentDsAmount = 0;
        TrackingModeActivity.sCurrentUsAmount = 0;
        this.mSensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);

        this.mSensorAcce = this.mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mSensorGyro = this.mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.mSensorManager.registerListener(this, this.mSensorGyro,
                SensorManager.SENSOR_DELAY_FASTEST);
        this.mSensorManager.registerListener(this, this.mSensorAcce,
                SensorManager.SENSOR_DELAY_FASTEST);
        this.iniData();
        this.IniButton();
        this.mLogoTimer = new Thread() {
            @Override
            public void run() {
                try {
                    int logoTimer = 0;
                    while (logoTimer < 20000) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }
                    TrackingModeActivity.sTotalCalorie = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };
        this.mLogoTimer.start();
        this.mPbHor.setProgress(5);

        // Set the alarm to start at approximately 0:00.
        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Intent resetDataIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, resetDataIntent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                alarmIntent);

        // start a watch dog thread to backup the data periodically
        this.mWatchdogHandler = new Handler();
        this.mWatchdog = new Runnable() {
            @Override
            public void run() {
                TrackingModeActivity.this.backupData();
                TrackingModeActivity.this.mWatchdogHandler.postDelayed(
                        TrackingModeActivity.this.mWatchdog, WATCHDOG_PERIOD);
            }
        };

        this.mWatchdogHandler.postDelayed(this.mWatchdog, WATCHDOG_PERIOD);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOther1:
                Intent intent = new Intent();
                intent.setClass(TrackingModeActivity.this, LoginActivity.class);
                this.finish();
                TrackingModeActivity.this.startActivity(intent);

                break;
            case R.id.Resume:

                this.changeButton();

                this.mSensorManager.registerListener(this, this.mSensorGyro,
                        SensorManager.SENSOR_DELAY_FASTEST);
                this.mSensorManager.registerListener(this, this.mSensorAcce,
                        SensorManager.SENSOR_DELAY_FASTEST);

                break;

            case R.id.Pause:

                this.mBtnResume.setEnabled(true);
                this.mBtnPause.setEnabled(false);
                this.mSensorManager.unregisterListener(this);

                break;

            case R.id.btnWalk1:
                Toast.makeText(
                        TrackingModeActivity.this,
                        "You need: " + this.mWalkTime
                                + " hours of hiking today", Toast.LENGTH_LONG)
                        .show();
                break;
            case R.id.btnRun1:
                Toast.makeText(
                        TrackingModeActivity.this,
                        "You need: " + this.mRunTime
                                + " hours of jogging today", Toast.LENGTH_LONG)
                        .show();
                break;
            case R.id.btnClimbingStairs1:
                Toast.makeText(
                        TrackingModeActivity.this,
                        "You need: " + this.mCsTime
                                + " hours of climbing stairs today",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btnDescendingStairs1:
                Toast.makeText(
                        TrackingModeActivity.this,
                        "You need: " + this.mDsTime
                                + " hours of descending stairs today",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btnBike:
                Toast.makeText(
                        TrackingModeActivity.this,
                        "You need: " + this.mBikeTime
                                + " hours of biking today", Toast.LENGTH_LONG)
                        .show();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        // stop watch dog
        this.mWatchdogHandler.removeCallbacks(this.mWatchdog);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == this.mSensorGyro) {
            long timestamp = event.timestamp;
            SensorData data = new SensorData(timestamp, event.values[0],
                    event.values[1], event.values[2]);
            this.mSensorDataGyro.add(data);
        }
        if (event.sensor == this.mSensorAcce) {
            this.mAcceValues = event.values;
            float alpha = (float) 0.8;
            this.mGravity[0] = alpha * this.mGravity[0] + (1 - alpha)
                    * event.values[0];
            this.mGravity[1] = alpha * this.mGravity[1] + (1 - alpha)
                    * event.values[1];
            this.mGravity[2] = alpha * this.mGravity[2] + (1 - alpha)
                    * event.values[2];
            this.mAcceValues[0] = event.values[0] - this.mGravity[0];
            this.mAcceValues[1] = event.values[1] - this.mGravity[1];
            this.mAcceValues[2] = event.values[2] - this.mGravity[2];
            long timestamp = event.timestamp;
            SensorData data = new SensorData(timestamp, this.mAcceValues[0],
                    this.mAcceValues[1], this.mAcceValues[2]);
            this.mSensorDataAccel.add(data);

        }
        this.mSensorCount++;
        if (this.mSensorCount > 2000) {
            this.detectAct();
        }

    }

    public void calculate() {
        double timeInterval = TrackingModeActivity.sExersicedTime;
        double timeInterval_inhour = timeInterval / 60 / 60;
        if (this.mDetector.getActivityType().equals("walking")) {
            TrackingModeActivity.sCurrentWalkAmount += timeInterval;
        } else if (this.mDetector.getActivityType().equals("running")) {
            TrackingModeActivity.sCurrentRunAmount += timeInterval;
        } else if (this.mDetector.getActivityType().equals("descending stairs")) {
            TrackingModeActivity.sCurrentDsAmount += timeInterval;
        } else if (this.mDetector.getActivityType().equals("climbing stairs")) {
            TrackingModeActivity.sCurrentUsAmount += timeInterval;
        } else {
            TrackingModeActivity.sCurrentStableAmount += timeInterval;
        }
        double h = this.getTime() / 100;
        double stable_calorie = 0;
        if (h > 8) {
            stable_calorie = (h - 8) / 14 * 1400;
        }
        TrackingModeActivity.sTotalCalorie += timeInterval_inhour
                * this.mUser.getWeight() * this.mDetector.getMet();
        TrackingModeActivity.sRemainCalorie = this.mUser.getCalorie()
                - TrackingModeActivity.sTotalCalorie - stable_calorie;
        double tmp = this.mUser.getCalorie()
                - TrackingModeActivity.sTotalCalorie - 1400;
        this.mWalkTime = tmp / this.mUser.getWeight() / 5;
        this.mBikeTime = tmp / 8 / this.mUser.getWeight();
        this.mRunTime = tmp / 6 / this.mUser.getWeight();
        this.mCsTime = tmp / 4 / this.mUser.getWeight();
        this.mDsTime = tmp / 2 / this.mUser.getWeight();

    }

    /*
     * Initialize button
     */
    public void IniButton() {
        TrackingModeActivity.this.mBtnResume.setEnabled(false);
        TrackingModeActivity.this.mBtnPause.setEnabled(true);
        TrackingModeActivity.this.mBtnWalk.setEnabled(true);
        TrackingModeActivity.this.mBtnRun.setEnabled(true);
        TrackingModeActivity.this.mBtnCS.setEnabled(true);
        TrackingModeActivity.this.mBtnDS.setEnabled(true);
        TrackingModeActivity.this.mBtnBike.setEnabled(true);
    }

    /*
     * CHANGE button
     */
    public void changeButton() {
        TrackingModeActivity.this.mBtnResume.setEnabled(false);
        TrackingModeActivity.this.mBtnPause.setEnabled(true);
        TrackingModeActivity.this.mBtnWalk.setEnabled(true);
        TrackingModeActivity.this.mBtnRun.setEnabled(true);
        TrackingModeActivity.this.mBtnCS.setEnabled(true);
        TrackingModeActivity.this.mBtnDS.setEnabled(true);
        TrackingModeActivity.this.mBtnBike.setEnabled(true);
    }

    public void updateView() {
        this.mCalorie.setText("mCalorie burned: "
                + (int) Math.round(TrackingModeActivity.sTotalCalorie)
                + "KCAL");
        double show = (this.mUser.getCalorie() - TrackingModeActivity.sRemainCalorie)
                / this.mUser.getCalorie() * 100;
        this.mPbHor.setProgress((int) show);
        this.mActivity.setText("mActivity: " + this.mDetector.getActivityType());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void iniData() {
        this.mSensorDataAccel = new ArrayList<SensorData>();
        this.mSensorDataGyro = new ArrayList<SensorData>();
        this.mSensorCount = 0;
    }

    public void detectAct() {
        this.mDetector = new ActivityDetector(this.mSensorDataAccel,
                this.mSensorDataGyro);
        TrackingModeActivity.sExersicedTime = this.mDetector.calculateTime();
        this.calculate();
        this.updateView();

        this.iniData();
    }

    @SuppressLint("SimpleDateFormat")
    public double getTime() {
        this.mSimpleDataformate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String time = this.mSimpleDataformate.format(new Date());
        time = time.substring(8, 12);
        double s = Double.parseDouble(time);
        return s;
    }

    public void saveData() {

    }

    private void backupData() {
        SharedPreferences sharedPref = this.getSharedPreferences(
                this.mUser.getName(), MODE_PRIVATE);
        Editor editor = sharedPref.edit();
        String dataStr = "" + System.currentTimeMillis() + ":"
                + TrackingModeActivity.sExersicedTime + ","
                + TrackingModeActivity.sCurrentWalkAmount + ","
                + TrackingModeActivity.sCurrentRunAmount + ","
                + TrackingModeActivity.sCurrentDsAmount + ","
                + TrackingModeActivity.sCurrentUsAmount + ","
                + TrackingModeActivity.sCurrentStableAmount + ","
                + TrackingModeActivity.sTotalCalorie + ","
                + TrackingModeActivity.sRemainCalorie + ","
                + WelcomeActivity.DEFAULT_USER.getCalorie();
        editor.putString("userData", dataStr);
        editor.commit();
    }

    /*
     * private void retrieveData() { SharedPreferences sharedPref = this.getSharedPreferences(
     * this.user.getName(), MODE_PRIVATE); String dataStr = sharedPref.getString("userData", ""); if
     * (dataStr != null && !"".equals(dataStr)) { // TODO: check if one day passed String[] data =
     * dataStr.split(":")[1].split(","); TrackingModeActivity.sExersiced_time =
     * Double.parseDouble(data[0]); TrackingModeActivity.sCurrentWalk_amount = Double
     * .parseDouble(data[1]); // TODO: complete the rest } }
     */
}
