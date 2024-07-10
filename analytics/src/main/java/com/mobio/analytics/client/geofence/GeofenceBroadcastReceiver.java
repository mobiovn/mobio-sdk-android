package com.mobio.analytics.client.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mobio.analytics.client.utility.LogMobio;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent=GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()){
            LogMobio.logD(TAG,"onReceive: Error received geofencing event....");
            return;
        }

        List<Geofence> geofenceList= geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence:geofenceList){
            LogMobio.logD(TAG,"onReceive: "+geofence.getRequestId()+" "+geofence.toString());
        }
        //Location location=geofencingEvent.getTriggeringLocation();   used to get the location list of triggering event

        int transitionType=geofencingEvent.getGeofenceTransition();
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context,"GEOFENCE_TRANSITION_ENTER",Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context,"GEOFENCE_TRANSITION_DWELL",Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context,"GEOFENCE_TRANSITION_EXIT",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
