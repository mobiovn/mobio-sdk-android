package com.mobio.analytics.client.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.mobio.analytics.client.MobioSDK;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.receiver.AlarmReceiver;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import java.util.ArrayList;

public class TerminateService extends Service {
    private AlarmManager alarmManager;

    public TerminateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            ArrayList<Properties> pendingJsonPush = MobioSDK.getInstance().getListFromSharePref(SharedPreferencesUtils.M_KEY_PENDING_PUSH);
            int sizeOfPendingJsonPush = pendingJsonPush.size();
            if (sizeOfPendingJsonPush > 0) {
                int countNoti = pendingJsonPush.size();
                long maxInterval = 60 * 1000L;
                long minInterval = 2 * 1000L;
                long interval = Utils.getTimeInterval(maxInterval, minInterval, countNoti);
                long now = System.currentTimeMillis();

                for (int i = 0; i < countNoti; i++) {
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    intent.setAction("ACTION_LAUNCH_NOTI");

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC,  now + interval * (i+1), alarmIntent);
                    }
                }
            }
        } catch (Exception e) {
            LogMobio.logE("TerminateService", "Exception "+e.getMessage());
        }
        stopSelf();
    }
}