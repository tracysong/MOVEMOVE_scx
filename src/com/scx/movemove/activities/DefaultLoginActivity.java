
package com.scx.movemove.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.scx.movemove.data.DatabaseHandler;
import com.scx.movemove.data.R;
import com.scx.movemove.data.UserInfo;
import com.scx.movemove.tools.FileHandler;

/**
 * <h1>Default Login Activity</h1> In Default Login status, the application extract the history
 * information of the default user; Login for default user or Click button to change to other users
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class DefaultLoginActivity extends Activity {
    private DatabaseHandler mDatabaseHandler = WelcomeActivity.sDbHandler;
    private UserInfo mDefaultUser = WelcomeActivity.DEFAULT_USER;
    private TextView mDefaultName;
    private Thread mLogoTimer;
    private FileHandler mFileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.defaultlogin);
        this.mFileHandler = new FileHandler(this);
        this.mDefaultName = (TextView) this.findViewById(R.id.default_user);
        this.mDefaultName.setText("Welcome, " + this.mDefaultUser.getName());

        if (!this.mDatabaseHandler.hasUser(this.mDefaultUser.getName())) {
            this.mDatabaseHandler.addUser(this.mDefaultUser);
            this.creatDefaultfile();
        }
        this.mLogoTimer = new Thread() {
            UserInfo user = WelcomeActivity.DEFAULT_USER;

            @Override
            public void run() {
                try {
                    int logoTimer = 0;
                    while (logoTimer < 500) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }
                    Intent intent = new Intent();
                    if (this.user.getDoneT() == 0) {
                        intent.setClass(DefaultLoginActivity.this,
                                TrainingModeActivity.class);
                    } else {
                        intent.setClass(DefaultLoginActivity.this,
                                TrackingModeActivity.class);
                    }
                    DefaultLoginActivity.this.startActivity(intent);
                } catch (InterruptedException e) {
                    Log.e(DefaultLoginActivity.class.getName(), e.getMessage());
                } finally {
                    DefaultLoginActivity.this.finish();
                }
            }
        };
        this.mLogoTimer.start();

    }

    /*
     * A Default_user file created for all users
     */
    public void creatDefaultfile() {
        String stringWalk = "";
        String stringRun = "";
        stringWalk = "1.1620585 1.5, 1.5501163 2.0, 1.661803 2.5, 1.9143987 3.0, 2.329882 3.5, 3.163864 4.0";
        stringRun = "5.137428 4.0, 5.5001206 4.5, 5.722003 5.0, 6.1791167 5.5, 6.514968 6.0";
        String walkFile = this.mDefaultUser.getName() + "_walking";
        String runFile = this.mDefaultUser.getName() + "_running";
        this.mFileHandler.writeToFile(stringWalk, walkFile);
        this.mFileHandler.writeToFile(stringRun, runFile);
        this.mFileHandler.writeToFile("defaultUser", "xixi");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void LoadDefault() {

    }
}
