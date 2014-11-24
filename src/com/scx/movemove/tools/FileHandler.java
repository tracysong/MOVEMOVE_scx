
package com.scx.movemove.tools;

import android.content.Context;
import android.util.Log;

import com.scx.movemove.data.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <h1>File Handler</h1> The FileHandler class is used for writing to files and reading from files
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class FileHandler {
    private Context mContext;

    public FileHandler(Context context) {
        this.mContext = context;
    }

    public boolean writeToFile(String fcontent, String fname) {
        try {
            FileOutputStream fout = this.mContext.openFileOutput(fname + ".txt",
                    Context.MODE_PRIVATE);
            fout.write(fcontent.getBytes());
            fout.close();
            Log.d("Suceess", "Sucess");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String readFromFile(String fname) {
        String response = null;
        try {
            FileInputStream fin = this.mContext.openFileInput(fname + ".txt");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = fin.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byte[] bytes = bos.toByteArray();
            response = new String(bytes);
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    public void creatSpeed_Accfile(UserInfo user) {
        String stringWalk = "";
        String stringRun = "";
        switch (user.getHeightR()) {
            case 0:
                stringWalk = "1.1620585 1.5, 1.5501163 2.0, 1.661803 2.5, 1.9143987 3.0, 2.329882 3.5, 3.163864 4.0";
                stringRun = "5.137428 4.0, 5.5001206 4.5, 5.722003 5.0, 6.1791167 5.5, 6.514968 6.0";
                break;
            case 1:
                stringWalk = "1.1620585 1.5, 1.5501163 2.0, 1.661803 2.5, 1.9143987 3.0, 2.329882 3.5, 3.163864 4.0";
                stringRun = "5.137428 4.0, 5.5001206 4.5, 5.722003 5.0, 6.1791167 5.5, 6.514968 6.0";
                break;
            case 2:
                stringWalk = "1.1620585 1.5, 1.5501163 2.0, 1.661803 2.5, 1.9143987 3.0, 2.329882 3.5, 3.163864 4.0";
                stringRun = "5.137428 4.0, 5.5001206 4.5, 5.722003 5.0, 6.1791167 5.5, 6.514968 6.0";
                break;
            case 3:
                stringWalk = "1.1620585 1.5, 1.5501163 2.0, 1.661803 2.5, 1.9143987 3.0, 2.329882 3.5, 3.163864 4.0";
                stringRun = "5.137428 4.0, 5.5001206 4.5, 5.722003 5.0, 6.1791167 5.5, 6.514968 6.0";
                break;
            default:
                break;
        }
        String walkFile = user.getName() + "_walking";
        String runFile = user.getName() + "_running";
        this.writeToFile(stringWalk, walkFile);
        this.writeToFile(stringRun, runFile);
    }
}
