
package com.scx.movemove.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.scx.movemove.data.DatabaseHandler;
import com.scx.movemove.data.R;
import com.scx.movemove.data.UserInfo;
import com.scx.movemove.tools.FileHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Welcome Activity</h1> The welcome page of the application.Creat default files if they are not
 * exist
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class WelcomeActivity extends Activity {

    public static DatabaseHandler sDbHandler;
    public static Map<String, Double> sMetMap = new HashMap<String, Double>();
    public static UserInfo DEFAULT_USER = null;
    private FileHandler mFileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.welcome);
        WelcomeActivity.sDbHandler = new DatabaseHandler(this);
        this.mFileHandler = new FileHandler(this);
        this.getDefaultUser();
        /*
         * the MET lookup table
         */
        WelcomeActivity.sMetMap.put("walking_1.5", 2.0);
        WelcomeActivity.sMetMap.put("walking_2.0", 2.5);
        WelcomeActivity.sMetMap.put("walking_2.5", 3.0);
        WelcomeActivity.sMetMap.put("walking_3.0", 3.5);
        WelcomeActivity.sMetMap.put("walking_3.5", 4.3);
        WelcomeActivity.sMetMap.put("walking_4.0", 5.0);
        WelcomeActivity.sMetMap.put("walking_4.5", 7.0);
        WelcomeActivity.sMetMap.put("walking_5.0", 8.3);

        WelcomeActivity.sMetMap.put("running_4.0", 6.0);
        WelcomeActivity.sMetMap.put("running_4.5", 7.1);
        WelcomeActivity.sMetMap.put("running_5.0", 8.3);
        WelcomeActivity.sMetMap.put("running_5.5", 9.1);
        WelcomeActivity.sMetMap.put("running_6.0", 9.8);
        WelcomeActivity.sMetMap.put("running_6.5", 10.3);
        WelcomeActivity.sMetMap.put("running_7.0", 11.0);
        WelcomeActivity.sMetMap.put("running_7.5", 11.5);
        WelcomeActivity.sMetMap.put("running_8.0", 11.8);
        WelcomeActivity.sMetMap.put("running_8.5", 12.3);
        WelcomeActivity.sMetMap.put("running_9.0", 12.8);
        WelcomeActivity.sMetMap.put("running_9.5", 13.7);
        WelcomeActivity.sMetMap.put("running_10.0", 14.5);
        WelcomeActivity.sMetMap.put("running_10.5", 15.4);
        WelcomeActivity.sMetMap.put("running_11.0", 16.0);

        WelcomeActivity.sMetMap.put("descending stairs", 3.5);
        WelcomeActivity.sMetMap.put("climbing stairs", 6.0);
        WelcomeActivity.sMetMap.put("Not moving", 1.0);
        sDbHandler = new DatabaseHandler(this);

        Thread logoTimer = new Thread() {
            @Override
            public void run() {
                try {
                    int logoTimer = 0;
                    while (logoTimer < 1000) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }
                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this,
                            DefaultLoginActivity.class);
                    WelcomeActivity.this.startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    WelcomeActivity.this.finish();
                }
            }
        };
        logoTimer.start();

    }

    private void getDefaultUser() {
        if (WelcomeActivity.sDbHandler.hasUser("xixi")) {
            DEFAULT_USER = new UserInfo("xixi", 161, 55, 24, 0, 0);
            WelcomeActivity.sDbHandler.addUser(DEFAULT_USER);
            this.mFileHandler.creatSpeed_Accfile(DEFAULT_USER);
            this.mFileHandler.writeToFile("defaultUser", "xixi");
            return;

        } else {
            DEFAULT_USER = WelcomeActivity.sDbHandler.getUser(this.mFileHandler
                    .readFromFile("defaultUser"));
        }
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
}
