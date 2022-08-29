package com.mobio.sample;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobio.analytics.client.MobioSDK;

public class AppPushFirebaseService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        MobioSDK.getInstance().setDeviceToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        MobioSDK.getInstance().handlePushMessage(remoteMessage);

        //App handle push here
    }
}
