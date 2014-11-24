
package com.scx.movemove.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * <h1>Database Handler</h1> The Database Handler class help us to deal with the database such as
 * create table, update table, get informations from tables.
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHandler.class.getName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "userInfo";

    // Table Name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_REGRE = "regre";

    // Table Columns Name
    private static final String KEY_NAME = "name";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_MALE = "male";
    private static final String KEY_DONET = "doneT";
    private static final String KEY_WALKA0 = "w_a0";
    private static final String KEY_WALKA1 = "w_a1";
    private static final String KEY_WALKA2 = "w_a2";
    private static final String KEY_WALKA3 = "w_a3";
    private static final String KEY_RUNA0 = "r_a0";
    private static final String KEY_RUNA1 = "r_a1";
    private static final String KEY_RUNA2 = "r_a2";
    private static final String KEY_RUNA3 = "r_a3";

    private static final String KEY_BD_AVE_ACC_RUN = "bd_ave_Acc_run";
    private static final String KEY_BD_MINY_ACC_RUN = "bd_minY_Acc_run";
    private static final String KEY_BD_MINY_ACC_DS = "bd_minY_Acc_ds";
    private static final String KEY_BD_AVE_XGYRO = "bd_ave_XGyro";
    private static final String KEY_BD_AVE_ACC_STABLE = "bd_ave_Acc_stable";

    private static final String CREATE_USERINFO_TABLE = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_NAME + " TEXT PRIMARY KEY," + KEY_HEIGHT
            + " INTEGER," + KEY_WEIGHT + " INTEGER," + KEY_AGE + " INTEGER,"
            + KEY_MALE + " INTEGER," + KEY_DONET + " INTEGER" + ")";

    private static final String CREATE_USERREGRE_TABLE = "CREATE TABLE "
            + TABLE_REGRE + "(" + KEY_NAME + " TEXT PRIMARY KEY," + KEY_WALKA0
            + " DOUBLE," + KEY_WALKA1 + " DOUBLE," + KEY_WALKA2 + " DOUBLE,"
            + KEY_WALKA3 + " DOUBLE," + KEY_RUNA0 + " DOUBLE," + KEY_RUNA1
            + " DOUBLE," + KEY_RUNA2 + " DOUBLE," + KEY_RUNA3 + " DOUBLE,"
            + KEY_BD_AVE_ACC_RUN + " DOUBLE," + KEY_BD_MINY_ACC_RUN
            + " DOUBLE," + KEY_BD_MINY_ACC_DS + " DOUBLE," + KEY_BD_AVE_XGYRO
            + " DOUBLE," + KEY_BD_AVE_ACC_STABLE + " DOUBLE" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERINFO_TABLE);
        db.execSQL(CREATE_USERREGRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading Database from version " + oldVersion + " to "
                + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        this.onCreate(db);
    }

    public void addUser(UserInfo user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_HEIGHT, user.getHeight());
        values.put(KEY_WEIGHT, user.getWeight());
        values.put(KEY_AGE, user.getAge());
        values.put(KEY_MALE, user.getMale());
        values.put(KEY_DONET, 0);

        db.insert(TABLE_USERS, null, values);

        values = new ContentValues();
        values.put(KEY_WALKA0, user.getW0());
        values.put(KEY_WALKA1, user.getW1());
        values.put(KEY_WALKA2, user.getW2());
        values.put(KEY_WALKA3, user.getW3());
        values.put(KEY_RUNA0, user.getR0());
        values.put(KEY_RUNA1, user.getR1());
        values.put(KEY_RUNA2, user.getR2());
        values.put(KEY_RUNA3, user.getR3());

        values.put(KEY_BD_AVE_ACC_RUN, user.getBd_ave_Acc_run());
        values.put(KEY_BD_MINY_ACC_RUN, user.getBd_minY_Acc_run());
        values.put(KEY_BD_MINY_ACC_DS, user.getBd_minY_Acc_ds());
        values.put(KEY_BD_AVE_XGYRO, user.getBd_ave_XGyro());
        values.put(KEY_BD_AVE_ACC_STABLE, user.getBd_ave_Acc_stable());

        db.insert(TABLE_REGRE, null, values);
        db.close();
    }

    public boolean hasUser(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String str = "select * from " + TABLE_USERS + " where name=?";
            Cursor cursor = db.rawQuery(str, new String[] {
                    name
            });
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return false;
    }

    public UserInfo getUser(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[] {
                KEY_NAME,
                KEY_HEIGHT, KEY_WEIGHT, KEY_AGE, KEY_MALE, KEY_DONET
        },
                KEY_NAME + "=?", new String[] {
                    name
                }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        UserInfo user = new UserInfo(cursor.getString(0),
                Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor
                        .getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor
                        .getString(5)));
        return user;
    }

    public int updateUserInfo(UserInfo user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HEIGHT, user.getHeight());
        values.put(KEY_WEIGHT, user.getWeight());
        values.put(KEY_AGE, user.getAge());
        values.put(KEY_MALE, user.getMale());
        values.put(KEY_DONET, user.getDoneT());

        values = new ContentValues();
        values.put(KEY_WALKA0, user.getW0());
        values.put(KEY_WALKA1, user.getW1());
        values.put(KEY_WALKA2, user.getW2());
        values.put(KEY_WALKA3, user.getW3());
        values.put(KEY_RUNA0, user.getR0());
        values.put(KEY_RUNA1, user.getR1());
        values.put(KEY_RUNA2, user.getR2());
        values.put(KEY_RUNA3, user.getR3());

        values.put(KEY_BD_AVE_ACC_RUN, user.getBd_ave_Acc_run());
        values.put(KEY_BD_MINY_ACC_RUN, user.getBd_minY_Acc_run());
        values.put(KEY_BD_MINY_ACC_DS, user.getBd_minY_Acc_ds());
        values.put(KEY_BD_AVE_XGYRO, user.getBd_ave_XGyro());
        values.put(KEY_BD_AVE_ACC_STABLE, user.getBd_ave_Acc_stable());

        db.update(TABLE_REGRE, values, KEY_NAME + " = ?",
                new String[] {
                    String.valueOf(user.getName())
                });
        return db.update(TABLE_USERS, values, KEY_NAME + " = ?",
                new String[] {
                    String.valueOf(user.getName())
                });
    }

    public void deleteUser(UserInfo gesture) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_NAME + " = ?",
                new String[] {
                    String.valueOf(gesture.getName())
                });
        db.close();
    }

    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

}
