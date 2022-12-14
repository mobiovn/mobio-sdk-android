package com.mobio.analytics.client.model.factory;

import android.content.Context;
import android.os.Build;

import com.mobio.analytics.BuildConfig;
import com.mobio.analytics.client.model.digienty.App;
import com.mobio.analytics.client.model.digienty.DataIdentity;
import com.mobio.analytics.client.model.digienty.DataNotification;
import com.mobio.analytics.client.model.digienty.DataTrack;
import com.mobio.analytics.client.model.digienty.Device;
import com.mobio.analytics.client.model.digienty.Event;
import com.mobio.analytics.client.model.digienty.Identity;
import com.mobio.analytics.client.model.digienty.IdentityDetail;
import com.mobio.analytics.client.model.digienty.Journey;
import com.mobio.analytics.client.model.digienty.Network;
import com.mobio.analytics.client.model.digienty.Notification;
import com.mobio.analytics.client.model.digienty.Os;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.digienty.Screen;
import com.mobio.analytics.client.model.digienty.Sdk;
import com.mobio.analytics.client.model.digienty.Track;
import com.mobio.analytics.client.utility.NetworkUtil;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import java.util.ArrayList;
import java.util.List;

public class ModelFactory {

    private static Device getDevice(Context context){
        return new Device().putChannel("app")
                .putType("mobile")
                .putDId(SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_D_ID))
                .putTId(Utils.getDeviceId(context))
                .putUId("");
    }

    private static IdentityDetail getIdentityDetail(Context context){
        return new IdentityDetail().putChannel("app")
                .putType("mobile")
                .putDId(SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_D_ID))
                .putTId(Utils.getDeviceId(context))
                .putUId("")
                .putOs(getOs())
                .putNetwork(getNetwork(context))
                .putScreen(getScreen(context))
                .putLocale(Utils.getLocale(context))
                .putTimezone(Utils.getTimeZone())
                .putApp(getApp());
    }

    private static Sdk getSdk(Context context){
        return new Sdk().putCode(SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_SDK_CODE))
                .putSource(SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_SDK_SOURCE))
                .putName("SDK_ANDROID")
                .putBuild(BuildConfig.VERSION_CODE+"")
                .putVersion(BuildConfig.VERSION_NAME);
    }

    public static DataTrack getDataTrack(Context context){
        return new DataTrack().putTrack(new Track().putSdk(getSdk(context)).putDevice(getDevice(context)));
    }

    public static DataIdentity getDataIdentity(Context context){
        return new DataIdentity().putIdentity(getIdentity(context));
    }

    public static DataNotification getDataNotification(Context context){
        return new DataNotification().putNotification(new Notification().putSdk(getSdk(context))
                .putDetail(new Notification.Detail().putToken(SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_DEVICE_TOKEN))
                                                    .putPermission(Utils.areNotificationsEnabled(context) ? Notification.KEY_GRANTED : Notification.KEY_DENIED))
                .putActionTime(System.currentTimeMillis())
                .putDevice(getIdentityDetail(context)));
    }

    private static Screen getScreen(Context context){
        return new Screen().putHeight((int) Utils.dpFromPx(context, Utils.getHeightOfScreen(context)))
                .putWidth((int) Utils.dpFromPx(context, Utils.getWidthOfScreen(context)));
    }

    private static Os getOs(){
        return new Os().putName("Android").putVersion(Build.VERSION.RELEASE);
    }

    private static Network getNetwork(Context context){
        Network network = new Network();
        if(NetworkUtil.getConnectivityStatus(context) == NetworkUtil.NETWORK_STATUS_MOBILE){
            network.putCellular(true);
            network.putWifi(false);
        }
        else if(NetworkUtil.getConnectivityStatus(context) == NetworkUtil.NETWORK_STATUS_WIFI){
            network.putWifi(true);
            network.putCellular(false);
        }
        network.putBluetooth(Utils.isBluetoothEnable()).putAddress(Utils.getIpAddress(context));
        return network;
    }

    private static App getApp() {
        return new App().putManufacturer(Build.MANUFACTURER)
                .putModel(Build.MODEL)
                .putName(Build.DEVICE)
                .putType("Android");
    }

    private static Identity getIdentity(Context context) {
        return new Identity().putSdk(getSdk(context))
                .putIdentityDetail(getIdentityDetail(context))
                .putActionTime(System.currentTimeMillis());
    }

    public static List<Event> createBaseListForPopup(Push push, String object, String type, long actionTime) {
        Event.Base base = new Event.Base()
                .putObject(object)
                .putValue(new Properties().putValue("journey", getJourney(push)));

        ArrayList<Event> events = new ArrayList<>();
        Event event = new Event().putBase(base)
                .putSource("popup_builder")
                .putType(type)
                .putActionTime(actionTime);
        events.add(event);
        return events;
    }

    public static List<Event> createBaseList(Event.Base base, String type, long actionTime, String source) {
        ArrayList<Event> events = new ArrayList<>();
        Event event = new Event().putBase(base)
                .putSource(source)
                .putType(type)
                .putActionTime(actionTime);
        events.add(event);
        return events;
    }

    public static Event.Base createBase(String object, Properties value){
        return new Event.Base()
                .putObject(object)
                .putValue(value);
    }

    public static Journey getJourney(Push push){
        Push.Data data = push.getData();

        if(data == null) return null;

        return new Journey()
                .putId(data.getJourneyId())
                .putNodeId(data.getNodeId())
                .putInstanceId(data.getInstanceId())
                .putMasterCampaignId(data.getMasterCampaignId())
                .putMerchantId(data.getMerchantId())
                .putPopupId(data.getPopupId())
                .putSendId(data.getSendId())
                .putCode(data.getCode())
                .putInteractiveTime(System.currentTimeMillis())
                .putProfileId(data.getProfileId());
    }
}
