/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.munib.spotme.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


public final class CommonUtils {


    public static final String DEVICE_NAME = "android";
    public static final String DEVICEINFO = "deviceinfo";
    public static String USERS_CHILD = "user";
    public static String USERS_MESSAGE = "message";
    public static String TAGZ = "SpotMe";


    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            return false;
        }
    }

    public static void hideSoftKeyboard(Context activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (((AppCompatActivity) activity).getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(((AppCompatActivity) activity).getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static String getCurrentDate() {
        String date = new SimpleDateFormat("yyyyMMddHHmmssSS", Locale.getDefault()).format(new Date());
        return date;
    }

    public static String getDate() {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        return date;
    }

    public static String getTime() {
        String date = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
        return date;
    }

    public static boolean isValidLatitude(CharSequence latitude) {
        final String LATITUDE_PATTERN = "^(\\+|-)?(?:90(?:(?:\\.0{1,8})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,8})?))$";
        return Pattern.compile(LATITUDE_PATTERN).matcher(latitude).matches();
    }

    public static boolean isValidLogitude(CharSequence logitude) {
        String LONGITUDE_PATTERN = "^(\\+|-)?(?:180(?:(?:\\.0{1,8})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,8})?))$";
        return Pattern.compile(LONGITUDE_PATTERN).matcher(logitude).matches();
    }


}
