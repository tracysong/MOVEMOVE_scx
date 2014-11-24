
package com.scx.movemove.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.scx.movemove.data.DatabaseHandler;
import com.scx.movemove.data.R;
import com.scx.movemove.data.UserInfo;
import com.scx.movemove.tools.FileHandler;

/**
 * <h1>Register Activity</h1> Register Activity id used for registration of the new user of the
 * application
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class RegisterActivity extends Activity {

    private EditText mUserHeight;
    private EditText mUserWeight;
    private EditText mUserAge;
    private EditText mEdname;
    private Button mBtregister;
    private RadioGroup mGroup;
    private UserInfo mUser;
    private String mName;
    private int mHeight;
    private int mWeight;
    private int mAge;
    private int mMale;
    private DatabaseHandler mDatabaseHandler = WelcomeActivity.sDbHandler;
    private FileHandler mFileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.register);
        this.mFileHandler = new FileHandler(this);
        this.mEdname = (EditText) this.findViewById(R.id.edname1);
        this.mUserHeight = (EditText) this.findViewById(R.id.edHeight);
        this.mUserWeight = (EditText) this.findViewById(R.id.edWeight);
        this.mUserAge = (EditText) this.findViewById(R.id.edAge);
        this.mBtregister = (Button) this.findViewById(R.id.btregister1);
        this.mGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
        this.mDatabaseHandler = WelcomeActivity.sDbHandler;
        this.mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) RegisterActivity.this
                        .findViewById(radioButtonId);
                String gender = rb.getText().toString();
                if (gender.equals("Female")) {
                    RegisterActivity.this.mMale = 0;
                } else {
                    RegisterActivity.this.mMale = 1;
                }
            }
        });
        this.mBtregister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.mName = RegisterActivity.this.mEdname
                        .getText().toString();
                RegisterActivity.this.mHeight = Integer
                        .parseInt(RegisterActivity.this.mUserHeight.getText()
                                .toString());
                RegisterActivity.this.mWeight = Integer
                        .parseInt(RegisterActivity.this.mUserWeight.getText()
                                .toString());
                RegisterActivity.this.mAge = Integer
                        .parseInt(RegisterActivity.this.mUserAge.getText()
                                .toString());

                if (RegisterActivity.this.addUser(RegisterActivity.this.mName,
                        RegisterActivity.this.mHeight,
                        RegisterActivity.this.mWeight,
                        RegisterActivity.this.mAge,
                        RegisterActivity.this.mMale, 0)) {
                    DialogInterface.OnClickListener ss = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // go to login page
                            Intent in = new Intent();
                            in.setClass(RegisterActivity.this,
                                    DefaultLoginActivity.class);
                            RegisterActivity.this.startActivity(in);
                            RegisterActivity.this.onDestroy();
                        }
                    };
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("success")
                            .setMessage("you are registered")
                            .setPositiveButton("ok", ss).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
     * add new user to the database
     */
    public boolean addUser(String name, int height, int weight, int age,
            int male, int doneT) {
        this.mUser = new UserInfo(name, height, weight, age, male, 0);
        try {
            this.mDatabaseHandler.addUser(this.mUser);
            Toast.makeText(RegisterActivity.this, "DavaSaved",
                    Toast.LENGTH_LONG).show();
            this.mFileHandler.creatSpeed_Accfile(this.mUser);
            this.setDefaultUser(this.mUser);
            return true;
        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, "mUser already existed",
                    Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void setDefaultUser(UserInfo user) {
        WelcomeActivity.DEFAULT_USER = user;
        this.mFileHandler.writeToFile("defaultUser", "xixi");
    }
}
