package com.gdubina.multiprocesspreferences;

import android.content.Context;

public class MultiprocessPreferenceManager {
    private static MultiprocessSharedPreferences sPreferences;

    public static MultiprocessSharedPreferences getDefaultSharedPreferences(Context context) {
        if (sPreferences == null) {
            sPreferences = new Preferences(context);
        }
        return sPreferences;
    }
}