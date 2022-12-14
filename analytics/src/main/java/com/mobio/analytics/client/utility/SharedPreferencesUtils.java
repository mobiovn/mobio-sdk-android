package com.mobio.analytics.client.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String M_KEY_SHARED_PREFERENCES = "key_shared_preferences";
    public static final String M_KEY_FIRST_START_APP = "key_first_start_app";
    public static final String M_KEY_VERSION_CODE = "key_version_code";
    public static final String M_KEY_VERSION_NAME = "key_version_name";
    public static final String M_KEY_APP_FOREGROUND = "key_app_foreground";
    public static final String M_KEY_STATE_LOGIN = "key_state_login";
    public static final String M_KEY_USER_NAME = "key_username";
    public static final String M_KEY_PASSWORD = "key_password";
    public static final String M_KEY_API_TOKEN = "key_api_token";
    public static final String M_KEY_MERCHANT_ID = "key_merchant_id";
    public static final String M_KEY_ENVIRONMENT = "key_environment";
    public static final String M_KEY_DEVICE_TOKEN = "key_device_token";
    public static final String M_KEY_BASE_URL = "key_base_url";
    public static final String M_KEY_ENDPOINT = "key_endpoint";
    public static final String M_KEY_EVENT = "key_event";
    public static final String M_KEY_PUSH = "key_push";
    public static final String M_KEY_PENDING_PUSH = "key_pending_push";
    public static final String M_KEY_SEND_QUEUE = "key_send_queue";
    public static final String M_KEY_JOURNEY = "key_journey";
    public static final String M_KEY_D_ID = "d_id";
    public static final String M_KEY_SDK_CODE = "sdk_code";
    public static final String M_KEY_SDK_SOURCE = "sdk_source";
    public static final String M_KEY_SDK_NAME = "sdk_name";
    public static final String M_KEY_ALLOW_CALL_API = "m_allow_call_api";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(M_KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void editString(Context context, String key, String value){
        getSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static void editInt(Context context, String key, int value){
        getSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static void editBool(Context context, String key, boolean value){
        getSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String getString(Context context, String key){
        return getSharedPreferences(context).getString(key, null);
    }

    public static int getInt(Context context, String key){
        return getSharedPreferences(context).getInt(key, -1);
    }

    public static boolean getBool(Context context, String key){
        return getSharedPreferences(context).getBoolean(key, false);
    }
}
