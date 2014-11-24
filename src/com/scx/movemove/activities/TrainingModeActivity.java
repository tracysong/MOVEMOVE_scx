
package com.scx.movemove.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scx.movemove.data.DataPoint;
import com.scx.movemove.data.DatabaseHandler;
import com.scx.movemove.data.PolynomialRegression;
import com.scx.movemove.data.R;
import com.scx.movemove.data.SensorData;
import com.scx.movemove.data.UserInfo;
import com.scx.movemove.tools.ActivityDetector;
import com.scx.movemove.tools.FileHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h1>Training Mode Activity</h1> In Training mode, we use adaptive algorithm based on user input
 * to adjust the following variables: Bounds of the decision tree Coefficients of the polynomial
 * regression functions to detect speed
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */

public class TrainingModeActivity extends Activity implements
        SensorEventListener, OnClickListener {
    private SensorManager mSensorManager;

    private Button mBtnStartTraining, mBtnEndTraining, mBtnPause;
    private TextView mXCoor;
    private TextView mYCoor;
    private TextView mZCoor;

    private Sensor mSensorGyro;
    private Sensor mSensorAccel;
    private FileHandler mFileHandler;
    private UserInfo mUser = WelcomeActivity.DEFAULT_USER;
    private String mActivityType;
    private final double ERROR = 0.05;
    private boolean mStarted = false;
    private ArrayList<SensorData> mSensorDataAccel;
    private ArrayList<SensorData> mSensorDataGyro;
    private float[] mAcceValues = new float[] {
            0, 0, 0
    };
    private double mAveAcc = 0;
    private double mMinYAcc = 0;
    private double mAveXGyro = 0;
    private double mBoundAveAccRun;
    private double mBoundMinYAccRun;
    private double mBoundMinYAccDs;
    private double mBoundAveXGyro;
    private double mBoundAveAccStable;
    private double mError;
    private double mUserInputSpeed;
    private double mDetectedSpeed;
    private double mRoundSpeed;
    private int mAccurateCount;
    private int mTypeAccurateCount;
    /*
     * 0:<165; 1:<175; 2:<185;3<195
     */
    private int mHeightRange;
    private PolynomialRegression mWalkPline;
    private PolynomialRegression mRunPline;
    private double[][] mWalkCoefDatabase = new double[4][6];
    private double[][] mRunCoefDatabase = new double[4][5];
    private float[] mGravity = new float[3];
    private DatabaseHandler mDatabaseHandler = WelcomeActivity.sDbHandler;
    private Button mBtnOtheruser;
    private int mSensorCount = 0;
    private ActivityDetector sDetect;
    private final String[] ACTIVITYS = new String[] {
            "walk", "run", "climbing stairs",
            "descending stairs", "not moving"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.training_mode);
        this.mFileHandler = new FileHandler(this);

        this.mSensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);

        this.mBtnStartTraining = (Button) this.findViewById(R.id.startTraining);
        this.mBtnEndTraining = (Button) this.findViewById(R.id.endTraining);
        this.mBtnPause = (Button) this.findViewById(R.id.Pause);

        this.mXCoor = (TextView) this.findViewById(R.id.xcoor);
        this.mYCoor = (TextView) this.findViewById(R.id.ycoor);
        this.mZCoor = (TextView) this.findViewById(R.id.zcoor);

        this.mBtnStartTraining.setOnClickListener(this);
        this.mBtnEndTraining.setOnClickListener(this);
        this.mBtnPause.setOnClickListener(this);

        this.IniButton();
        this.mBtnOtheruser = (Button) this.findViewById(R.id.btnOther);
        this.mBtnOtheruser.setOnClickListener(this);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOther:
                Intent intent = new Intent();
                intent.setClass(TrainingModeActivity.this, LoginActivity.class);
                this.finish();
                TrainingModeActivity.this.startActivity(intent);
                break;
            case R.id.startTraining:
                this.mBtnStartTraining.setEnabled(false);
                this.mBtnPause.setEnabled(true);
                this.mHeightRange = this.mUser.getHeightR();
                this.mStarted = true;
                this.mAccurateCount = 0;
                this.mTypeAccurateCount = 0;

                this.formPR(this.mHeightRange);
                for (int i = 0; i < PolynomialRegression.DEGREE; i++) {
                    this.mWalkCoefDatabase[this.mHeightRange][i] = this
                            .getRun_pline().beta(i);
                    this.mRunCoefDatabase[this.mHeightRange][i] = this
                            .getRun_pline().beta(i);
                }
                this.iniData();

                this.mSensorDataAccel = new ArrayList<SensorData>();
                this.mSensorDataGyro = new ArrayList<SensorData>();
                this.mStarted = true;
                this.mSensorAccel = this.mSensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                this.mSensorGyro = this.mSensorManager
                        .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                this.mSensorManager.registerListener(this, this.mSensorAccel,
                        SensorManager.SENSOR_DELAY_FASTEST);
                this.mSensorManager.registerListener(this, this.mSensorGyro,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;

            case R.id.endTraining:
                this.mBtnStartTraining.setEnabled(true);
                this.mBtnEndTraining.setEnabled(false);
                this.mSensorManager.unregisterListener(this);
                Intent intent1 = new Intent(this, TrackingModeActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.Pause:
                this.mSensorManager.unregisterListener(this);
                this.mBtnStartTraining.setEnabled(true);
                this.mBtnPause.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor == this.mSensorGyro) {
                if (this.mStarted) {
                    SensorData data = new SensorData(event.timestamp,
                            event.values[0], event.values[1],
                            event.values[2]);
                    this.mSensorDataGyro.add(data);
                }
            }
            if (event.sensor == this.mSensorAccel) {
                this.mAcceValues = event.values;
                if (this.mStarted) {
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
                    this.mXCoor.setText("X: " + this.mAcceValues[0]);
                    this.mYCoor.setText("Y: " + this.mAcceValues[1]);
                    this.mZCoor.setText("Z: " + this.mAcceValues[2]);
                    long timestamp = event.timestamp;
                    SensorData data = new SensorData(timestamp,
                            this.mAcceValues[0], this.mAcceValues[1],
                            this.mAcceValues[2]);
                    this.mSensorDataAccel.add(data);
                }

            }

            this.mSensorCount++;
            if (this.mSensorCount > 10000) {
                this.detectAct();
                this.mRoundSpeed = this.sDetect.getRoundSpeed();
                this.mActivityType = this.sDetect.getActivityType();
                this.interact();
            } else {

            }
        }

    }

    public void iniData1() {
        this.mSensorDataAccel = new ArrayList<SensorData>();
        this.mSensorDataGyro = new ArrayList<SensorData>();
        this.mSensorCount = 0;
    }

    public void detectAct() {
        this.sDetect = new ActivityDetector(this.mSensorDataAccel,
                this.mSensorDataGyro);
        this.iniData1();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mSensorManager.registerListener(this, this.mSensorGyro,
                SensorManager.SENSOR_DELAY_FASTEST);
        this.mSensorManager.registerListener(this, this.mSensorAccel,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*
     * Ask mUser to help with the training
     */
    public void interactSpeed() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        TrainingModeActivity.this.mAccurateCount++;
                        if (TrainingModeActivity.this.mAccurateCount > 5
                                && TrainingModeActivity.this.mTypeAccurateCount > 5) {
                            TrainingModeActivity.this.doneTraining();
                        } else {
                            TrainingModeActivity.this.contiTraining();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(TrainingModeActivity.this,
                                " Please enter you speed in the box",
                                Toast.LENGTH_LONG).show();
                        final EditText input = new EditText(
                                TrainingModeActivity.this);
                        new AlertDialog.Builder(TrainingModeActivity.this)
                                .setTitle("Please enter your speed here(mph)")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setView(input).setPositiveButton("ok", null)
                                .show();
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        TrainingModeActivity.this
                                                .dealwithInput(input);
                                        break;
                                }
                            }
                        };

                        break;
                }
            }
        };
        new AlertDialog.Builder(this)
                .setMessage(
                        " Your speed is: " + this.stringSpeed(this.mRoundSpeed)
                                + ". Is this right?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .setCancelable(true).show();

    }

    /*
     * Ask mUser to help with the training
     */
    public void interact() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        TrainingModeActivity.this.mTypeAccurateCount++;
                        if (TrainingModeActivity.this.mActivityType
                                .equals("run")
                                || TrainingModeActivity.this.mActivityType
                                        .equals("walk")) {
                            TrainingModeActivity.this.interactSpeed();
                        } else {
                            if (TrainingModeActivity.this.mAccurateCount > 5
                                    && TrainingModeActivity.this.mTypeAccurateCount > 5) {
                                TrainingModeActivity.this.doneTraining();
                            } else {
                                TrainingModeActivity.this.contiTraining();
                            }
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // ActivityAdaptor.this.enOptionButton();
                        Toast.makeText(TrainingModeActivity.this,
                                " Please choose you activity",
                                Toast.LENGTH_LONG).show();
                        final Dialog alertDialog = new AlertDialog.Builder(
                                TrainingModeActivity.this)
                                .setTitle("please choose your activity type？")
                                .setIcon(R.drawable.ic_launcher)
                                .setItems(TrainingModeActivity.this.ACTIVITYS,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                Toast.makeText(
                                                        TrainingModeActivity.this,
                                                        TrainingModeActivity.this.ACTIVITYS[which],
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                .setNegativeButton("cancel",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                switch (which) {
                                                    case 0:
                                                        TrainingModeActivity.this
                                                                .adjustbound("walk");
                                                        TrainingModeActivity.this
                                                                .contiTraining();
                                                        break;
                                                    case 1:
                                                        TrainingModeActivity.this
                                                                .adjustbound("run");
                                                        TrainingModeActivity.this
                                                                .contiTraining();
                                                        break;
                                                    case 2:
                                                        TrainingModeActivity.this
                                                                .adjustbound("climbing stairs");
                                                        TrainingModeActivity.this
                                                                .contiTraining();
                                                        break;
                                                    case 3:
                                                        TrainingModeActivity.this
                                                                .adjustbound("descending stairs");
                                                        TrainingModeActivity.this
                                                                .contiTraining();
                                                    case 4:
                                                        TrainingModeActivity.this
                                                                .adjustbound("Not moving");
                                                        TrainingModeActivity.this
                                                                .contiTraining();
                                                        break;
                                                }
                                            }
                                        }).create();
                        alertDialog.show();
                        break;
                }
            }
        };
        final Dialog builder = new AlertDialog.Builder(this)
                .setMessage(
                        TrainingModeActivity.this.mActivityType
                                + ". Is this right?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                builder.dismiss();
                t.cancel();
            }
        }, 6000);
    }

    /*
     * Deal With User Input
     */
    public void dealwithInput(EditText input) {
        TrainingModeActivity.this.mUserInputSpeed = Double.parseDouble(input
                .getText().toString());
        TrainingModeActivity.this.mError = Math
                .abs(TrainingModeActivity.this.mDetectedSpeed
                        - TrainingModeActivity.this.mUserInputSpeed)
                / TrainingModeActivity.this.mUserInputSpeed;
        Toast.makeText(TrainingModeActivity.this,
                "mError: " + TrainingModeActivity.this.mError,
                Toast.LENGTH_LONG).show();
        if (TrainingModeActivity.this.mError < TrainingModeActivity.this.ERROR) {
            this.mAccurateCount++;
            if (this.mAccurateCount > 5 && this.mTypeAccurateCount > 5) {
                this.doneTraining();
            } else {
                this.contiTraining();
            }
        } else {
            // adjust coefficients according to mUser input
            if (TrainingModeActivity.this.mActivityType == "running") {
                this.getRun_pline().AdjustCoef(
                        new DataPoint(this.mAveAcc, this.mUserInputSpeed));
                this.saveData();
                String newPoint = ", " + this.mAveAcc + " "
                        + this.mUserInputSpeed;
                String runFile = this.mUser.getName() + "_running";
                this.mFileHandler.writeToFile(newPoint, runFile);
            } else if (TrainingModeActivity.this.mActivityType == "walking") {
                this.getWalk_pline().AdjustCoef(
                        new DataPoint(this.mAveAcc, this.mUserInputSpeed));
                this.saveData();
                String newPoint = ", " + this.mAveAcc + " "
                        + this.mUserInputSpeed;
                String walkFile = this.mUser.getName() + "_walking";
                this.mFileHandler.writeToFile(newPoint, walkFile);
            }
            this.contiTraining();
        }
    }

    /*
     * Form a Polynomial regression calculator according to the input
     */
    public void formPR(int height_range) {
        int r = height_range;
        this.setWalk_pline(new PolynomialRegression(this, r,
                "walking"));
        this.setRun_pline(new PolynomialRegression(this, r,
                "running"));
    }

    /*
     * Adjust bound for more precise detection
     */
    public void adjustbound(String type) {
        if (type.equals("run")) {
            if (this.mAveAcc < this.mBoundAveAccRun) {
                this.adBound_ave_Acc_run();
            }
            if (this.mMinYAcc > this.mBoundMinYAccRun) {
                this.adBound_minY_Acc_run();
            }
        } else if (type.equals("descending stairs")) {
            if (this.mAveAcc > this.mBoundAveAccRun) {
                this.adBound_ave_Acc_run();
            }
            if (this.mMinYAcc < this.mBoundMinYAccRun) {
                this.adBound_minY_Acc_run();
            }
            if (this.mMinYAcc > this.mBoundMinYAccDs) {
                this.adBound_minY_Acc_ds();
            }
        } else if (type.equals("walk")) {
            if (this.mAveAcc > this.mBoundAveAccRun) {
                this.adBound_ave_Acc_run();
            }
            if (this.mMinYAcc < this.mBoundMinYAccRun) {
                this.adBound_minY_Acc_run();
            }
            if (this.mMinYAcc < this.mBoundMinYAccDs) {
                this.adBound_minY_Acc_ds();
            }
            if (this.mAveXGyro > this.mBoundAveXGyro) {
                this.adBound_ave_XGyro();
            }
            if (this.mAveAcc < this.mBoundAveAccStable) {
                this.adBound_ave_Acc_stable();
            }

        } else if (type.equals("climbing stairs")) {
            if (this.mActivityType.equals("run")) {
                this.adBound_ave_Acc_run();
                this.adBound_minY_Acc_run();
            } else if (this.mActivityType.equals("descending stairs")) {
                this.adBound_minY_Acc_ds();
            } else if (this.mActivityType.equals("walk")) {
                this.adBound_ave_XGyro();
            } else if (this.mActivityType.equals("Not moving")) {
                this.adBound_ave_Acc_stable();
            }
        }
        if (type.equals("Not moving")) {
            if (this.mActivityType.equals("run")) {
                this.adBound_ave_Acc_run();
                this.adBound_minY_Acc_run();
            } else if (this.mActivityType.equals("descending stairs")) {
                this.adBound_minY_Acc_ds();
            } else if (this.mActivityType.equals("walk")) {
                this.adBound_ave_XGyro();
            } else if (this.mActivityType.equals("climbing stairs")) {
                this.adBound_ave_Acc_stable();
            }
        }
        this.saveData();
    }

    /*
     * Initialize button
     */
    public void IniButton() {
        this.mBtnStartTraining.setEnabled(true);
        this.mBtnPause.setEnabled(false);
        this.mBtnEndTraining.setEnabled(true);
    }

    /*
     * Set button to continue training
     */
    public void ContinueTrainingButton() {
        TrainingModeActivity.this.mBtnStartTraining.setEnabled(false);
        TrainingModeActivity.this.mBtnPause.setEnabled(true);
        TrainingModeActivity.this.mBtnEndTraining.setEnabled(true);

    }

    public void iniData() {
        this.mBoundAveAccRun = this.mUser.getBd_ave_Acc_run();
        this.mBoundMinYAccRun = this.mUser.getBd_minY_Acc_run();
        this.mBoundMinYAccDs = this.mUser.getBd_minY_Acc_ds();
        this.mBoundAveXGyro = this.mUser.getBd_ave_XGyro();
        this.mBoundAveAccStable = this.mUser.getBd_ave_Acc_stable();
    }

    /*
     * adjust each bound separately
     */
    public void adBound_ave_Acc_run() {
        this.mBoundAveAccRun = this.mBoundAveAccRun - 2
                * (this.mBoundAveAccRun - this.mAveAcc);
    }

    public void adBound_minY_Acc_run() {
        this.mBoundMinYAccRun = this.mBoundMinYAccRun - 2
                * (this.mBoundMinYAccRun - this.mAveAcc);
    }

    public void adBound_ave_XGyro() {
        this.mBoundAveXGyro = this.mBoundAveXGyro - 2
                * (this.mBoundAveXGyro - this.mAveXGyro);
    }

    public void adBound_minY_Acc_ds() {
        this.mBoundMinYAccDs = this.mBoundMinYAccDs
                - +(this.mBoundMinYAccDs - this.mMinYAcc);
    }

    public void adBound_ave_Acc_stable() {
        this.mBoundAveAccStable = this.mBoundAveAccStable
                - +(this.mBoundAveAccStable - this.mAveAcc);
    }

    /*
     * continue Training
     */
    public void contiTraining() {
        this.ContinueTrainingButton();
        Toast.makeText(TrainingModeActivity.this,
                " Please continue your training!", Toast.LENGTH_LONG).show();
    }

    /*
     * doneTraining
     */
    public void doneTraining() {
        Toast.makeText(TrainingModeActivity.this,
                " Congratulations! You have done your training!",
                Toast.LENGTH_LONG).show();
        TrainingModeActivity.this.IniButton();
        this.mUser.setDoneT(1);
        Intent intent = new Intent();
        intent.setClass(this, TrackingModeActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    /*
     * doneTraining
     */
    public void saveData() {
        this.mUser.setWalk(this.getWalk_pline().beta(0), this.getWalk_pline().beta(1),
                this.getWalk_pline().beta(2), this.getWalk_pline().beta(3));
        this.mUser.setRun(this.getRun_pline().beta(0), this.getRun_pline().beta(1),
                this.getRun_pline().beta(2), this.getRun_pline().beta(3));
        this.mUser.setBd(this.mBoundAveAccRun, this.mBoundMinYAccRun,
                this.mBoundMinYAccDs, this.mBoundAveXGyro,
                this.mBoundAveAccStable);
        this.mBoundAveAccStable = this.mUser.getBd_ave_Acc_stable();
        this.mUser.setDoneT(this.mUser.getDoneT());
        this.mDatabaseHandler.updateUserInfo(this.mUser);
    }

    private HashMap<Double, String> speedToString = new HashMap<Double, String>();

    public String stringSpeed(double speed) {
        this.speedToString.put(1.5, "0.5-1.75mph");
        this.speedToString.put(2.0, "1.75-2.25mph");
        this.speedToString.put(2.5, "2.25-2.75mph");
        this.speedToString.put(3.0, "2.75-3.25mph");
        this.speedToString.put(3.5, "3.25-3.75mph");
        this.speedToString.put(4.0, "3.75-4.25mph");
        this.speedToString.put(4.5, "4.25-4.75mph");
        this.speedToString.put(5.0, "4.75-5.25mph");
        this.speedToString.put(5.5, "5.25-5.75mph");
        this.speedToString.put(6.0, "5.75-6.25mph");
        this.speedToString.put(6.5, "6.25-6.75mph");
        this.speedToString.put(7.0, "6.75-7.25mph");
        this.speedToString.put(7.5, "7.25-7.75mph");
        this.speedToString.put(8.0, "7.75-8.75mph");
        this.speedToString.put(8.5, "8.25-8.75mph");
        this.speedToString.put(9.0, "8.75-9.25mph");
        this.speedToString.put(9.5, "9.25-9.75mph");
        this.speedToString.put(10.0, "9.75-10.25mph");
        this.speedToString.put(10.5, "10.25-10.75mph");
        this.speedToString.put(11.0, ">10.75mph");
        return this.speedToString.get(speed);
    }

    public void roundSpeed(double speed) {
        if (this.mDetectedSpeed < 1.75d) {
            this.mRoundSpeed = 1.5d;
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

    public PolynomialRegression getWalk_pline() {
        return this.mWalkPline;
    }

    public void setWalk_pline(PolynomialRegression walk_pline) {
        this.mWalkPline = walk_pline;
    }

    public PolynomialRegression getRun_pline() {
        return this.mRunPline;
    }

    public void setRun_pline(PolynomialRegression run_pline) {
        this.mRunPline = run_pline;
    }
}
