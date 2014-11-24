
package com.scx.movemove.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.scx.movemove.data.DatabaseHandler;
import com.scx.movemove.data.R;
import com.scx.movemove.data.UserInfo;
import com.scx.movemove.tools.FileHandler;

/**
 * <h1>Login Activity</h1> In Login activity, the application get user input, login for the user and
 * the current user is changed to DEFAULT_USER
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class LoginActivity extends Activity {

    private EditText mEdname;
    private Button mBtregister;
    private Button mBtlogin;
    private DatabaseHandler mDatabaseHandler = WelcomeActivity.sDbHandler;
    private FileHandler mFileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);
        this.mFileHandler = new FileHandler(this);
        this.mEdname = (EditText) this.findViewById(R.id.edname);
        this.mBtregister = (Button) this.findViewById(R.id.btregister);
        this.mBtlogin = (Button) this.findViewById(R.id.btlogin);
        this.mDatabaseHandler = WelcomeActivity.sDbHandler;
        this.mBtlogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = LoginActivity.this.mEdname.getText().toString();
                if (name.equals("")) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Wrong")
                            .setMessage("username could not be blank")
                            .setPositiveButton("ok", null).show();
                } else {
                    Intent intent = new Intent();

                    if (this.isUserinfo(name)) {
                        LoginActivity.this
                                .setDefaultUser(LoginActivity.this.mDatabaseHandler
                                        .getUser(name));
                        UserInfo user = WelcomeActivity.DEFAULT_USER;

                        if (user.getDoneT() == 0) {
                            intent.setClass(LoginActivity.this,
                                    TrainingModeActivity.class);
                        } else {
                            intent.setClass(LoginActivity.this,
                                    TrackingModeActivity.class);
                        }
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }
            }

            // Check if user input if correct
            public Boolean isUserinfo(String name) {
                try {

                    if (!LoginActivity.this.mDatabaseHandler.hasUser(name)) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Wrong")
                                .setMessage("User does not exist！")
                                .setPositiveButton("ok", null).show();
                        return false;
                    } else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("ok").setMessage("Login Succesfully")
                                .setPositiveButton("ok", null).show();
                        return true;
                    }

                } catch (SQLiteException e) {

                }
                return false;
            }

        });

        // go to RegisterActivity
        this.mBtregister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mDatabaseHandler.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void setDefaultUser(UserInfo user) {
        WelcomeActivity.DEFAULT_USER = user;
        LoginActivity.this.mFileHandler.writeToFile("defaultUser", "xixi");
    }

}
