package com.mobio.analytics.client.utility;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.mobio.analytics.BuildConfig;
import com.mobio.analytics.client.model.digienty.Event;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.ViewDimension;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {
    /**
     * Returns the referrer who started the Activity.
     */
    public static Uri getReferrer(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return activity.getReferrer();
        }
        return getReferrerCompatible(activity);
    }

    private static ViewDimension getScreenDimension(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dpHeight = displayMetrics.heightPixels;
        int dpWidth = displayMetrics.widthPixels;
        return new ViewDimension(dpWidth, dpHeight);
    }

    public static int getHeightOfScreen(Context context) {
        return getScreenDimension(context).height;
    }

    public static int getWidthOfScreen(Context context) {
        return getScreenDimension(context).width;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    return false;
                }
            }
            return true;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    public static String getTypeOfData(Properties valueMap) {
        if (valueMap.containsKey("identity")) {
            return "identity";
        }

        if (valueMap.containsKey("track")) {
            return "track";
        }

        if (valueMap.containsKey("notification")) {
            return "notification";
        }

        return null;
    }

    public static boolean isActivityDead(Activity activity) {
        if (activity == null) {
            return true;
        }
        boolean isActivityDead = activity.isFinishing();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isActivityDead = isActivityDead || activity.isDestroyed();
        }
        return isActivityDead;
    }

    public static int getHeightOfStatusBar(Activity activity) {
        int height;
        Resources myResources = activity.getResources();
        int idStatusBarHeight = myResources.getIdentifier( "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = activity.getResources().getDimensionPixelSize(idStatusBarHeight);
        } else {
            height = 0;
        }
        return height;
    }

    public static int getHeightOfNavigationBar(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean hasNavBar(WindowManager windowManager)
    {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static boolean isNavAtBottom(Activity activity){
        return (activity.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
                || (activity.getResources().getConfiguration().smallestScreenWidthDp >= 600);
    }

    public static boolean compareTwoJson(String first, String second) {
        try {
            JSONObject jsonObject = new JSONObject(first);
            JSONObject jsonObject1 = new JSONObject(second);
            Iterator<String> s = jsonObject.keys();
            while (s.hasNext()) {
                String str = s.next();
                if (!jsonObject.get(str).equals(jsonObject1.get(str))) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the referrer on devices running SDK versions lower than 22.
     */
    private static Uri getReferrerCompatible(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Intent intent = activity.getIntent();
            Uri referrerUri = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
            if (referrerUri != null) {
                return referrerUri;
            }
            // Intent.EXTRA_REFERRER_NAME
            String referrer = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
            if (referrer != null) {
                // Try parsing the referrer URL; if it's invalid, return null
                try {
                    return Uri.parse(referrer);
                } catch (android.net.ParseException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static long getTimeInterval(long max, long min, int size) {
        long diff = max - min;
        return diff / size + min;
    }

    public static String getIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        String deviceId;
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public static Class<?> getClassFromName(String name) {
        Class<?> act = null;
        try {
            act = Class.forName(name);
        } catch (ClassNotFoundException e) {
            LogMobio.logE("Utils", "ClassNotFoundException "+e);
        }
        return act;
    }

    public static void listAllActivities(Context context) {
        PackageManager pManager = context.getPackageManager();
        String packageName = context.getApplicationContext().getPackageName();

        try {
            ActivityInfo[] list = pManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;
            for (ActivityInfo activityInfo : list) {
                //TODO
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogMobio.logE("Utils","NameNotFoundException "+e);
        }
    }

    public static String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    public static String getTimeUTC() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return sdf.format(new Date());
    }

    public static String getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
            }
        } catch (IOException e) {
            LogMobio.logE("Utils","IOException "+e);
        }
        return address;
    }

    public static ArrayList<Event> createListEvent(ArrayList<Event.Dynamic> dynamicEvents) {
        ArrayList<Event> listEvent = new ArrayList<>();
        Event event = new Event().putSource("digienty")
                .putType("dynamic")
                .putActionTime((long) dynamicEvents.get(0).getEventData().get("action_time"))
                .putDynamic(dynamicEvents);
        listEvent.add(event);
        return listEvent;
    }

    public static ArrayList<Event.Dynamic> createDynamicListEvent(String eventKey, Properties eventData) {
        ArrayList<Event.Dynamic> dynamicListEvent = new ArrayList<>();
        Event.Dynamic dynamicEvent = new Event.Dynamic().putEventKey(eventKey).putEventData(eventData);
        dynamicListEvent.add(dynamicEvent);
        return dynamicListEvent;
    }

    public static Map<String, String> getHeader(Context context) {
        String token = SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_API_TOKEN);
        String merchantID = SharedPreferencesUtils.getString(context, SharedPreferencesUtils.M_KEY_MERCHANT_ID);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        header.put("X-Merchant-Id", merchantID);
        header.put("User-Agent", "analytics-android " + BuildConfig.VERSION_NAME);
        return header;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean isBluetoothEnable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        } else {
            // Bluetooth is not enable :)
            return mBluetoothAdapter.isEnabled();
        }
    }

    public static String getLocale(Context context) {
        return Locale.getDefault().getLanguage();
    }

    public static boolean hasWritePermissions(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean hasPhonePermissions(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
    }
}
