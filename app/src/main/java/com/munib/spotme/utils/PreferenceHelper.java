package com.munib.spotme.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.munib.spotme.R;

import java.lang.reflect.Type;
import java.util.List;


/**
 * Created by user on 26/4/18.
 */

public class PreferenceHelper {
    private Context context;

    private final String PREFS_INTRO = "isIntro";
    private final String PREFS_LOGIN = "isLogin";
    private final String PREFS_FBLOGIN = "isFBLogin";
    private final String PREFS_ISMAINUSER = "isMainUser";
    private SharedPreferences prefs = null;
    private SharedPreferences.Editor loginPrefsEditor;
    private final String TOKEN = "token";
    private final String ISVERIFIED = "isVerified";
    private final String IsAllowMIA = "isAllowMIA";
    private final String USERID = "userId";
    private final String MAINUSERID = "mainUserId";
    private String MY_PREFS_NAME = "nextGenPreference";
    private String PREFS_REGISTRATION_DATA = "patientList";
    private String LATITUDE = "latitude";
    private String LONGITUDE = "longitude";
    private String HospitalID="hospitalId";


    public String getLatitude() {
        return prefs.getString(LATITUDE, "");
    }

    public void setLatitude(String latitude) {
        prefs.edit().putString(LATITUDE, latitude).commit();
    }

    public String getLongtiude() {
        return prefs.getString(LONGITUDE, "");
    }

    public void setLongtiude(String longtiude) {
        prefs.edit().putString(LONGITUDE, longtiude).commit();
    }

    public String getFCM_TOKEN() {
        return prefs.getString(FCM_TOKEN, "");
    }

    private String FCM_TOKEN = "fcmtoken";

    public PreferenceHelper(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

    }


    public String getHostpitalId() {
        return prefs.getString(HospitalID, "");
    }

    public void setHostpitalId(String longtiude) {
        prefs.edit().putString(HospitalID, longtiude).commit();
    }



    public void setToken(String refreshedToken) {
        prefs.edit().putString(TOKEN, refreshedToken).commit();
    }

    public void setCallCount(Integer name) {
        prefs.edit().putInt("call_count", name).apply();
    }

    public Integer getCallCount() {
        return prefs.getInt("call_count", 0);
    }

    public void setFCMToken(String refreshedToken) {
        prefs.edit().putString(FCM_TOKEN, refreshedToken).commit();
    }

    public String getToken() {
        return prefs.getString(TOKEN, null);
    }

    public void setId(int refreshedToken) {
        prefs.edit().putInt(USERID, refreshedToken).apply();
    }

    public int getMainId() {
        return prefs.getInt(MAINUSERID, -1);
    }

    public void setMainId(int refreshedToken) {
        prefs.edit().putInt(MAINUSERID, refreshedToken).apply();
    }

    public int getId() {
        return prefs.getInt(USERID, -1);
    }

    public void setProfileBase64(String mobileNumber) {
        prefs.edit().putString("profile_base", mobileNumber).apply();
    }

    public String getProfileBase64() {
        return prefs.getString("profile_base", "");
    }

    public void setFacilityID(String mobileNumber) {
        prefs.edit().putString("facility_id", mobileNumber).apply();
    }

    public String getFacilityID() {
        return prefs.getString("facility_id", "");
    }

    public void setDoctorLicense(String mobileNumber) {
        prefs.edit().putString("doctor_lisence", mobileNumber).apply();
    }

    public String getDoctorLicense() {
        return prefs.getString("doctor_lisence", "");
    }


    public void clearPreference() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
