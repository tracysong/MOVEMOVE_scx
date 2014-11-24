
package com.scx.movemove.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.scx.movemove.activities.TrackingModeActivity;
import com.scx.movemove.activities.WelcomeActivity;

/**
 * <h1>Alarm Receiver</h1> AlarmReceiver get an alarm to reset all accumulating parameter to 0 every
 * midnight
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TrackingModeActivity.sExersicedTime = 0;
        TrackingModeActivity.sCurrentWalkAmount = 0;
        TrackingModeActivity.sCurrentRunAmount = 0;
        TrackingModeActivity.sCurrentDsAmount = 0;
        TrackingModeActivity.sCurrentUsAmount = 0;
        TrackingModeActivity.sCurrentStableAmount = 0;
        TrackingModeActivity.sTotalCalorie = 0;
        TrackingModeActivity.sRemainCalorie = WelcomeActivity.DEFAULT_USER
                .getCalorie();
    }
}
