package com.gdubina.multiprocesspreferences;

import android.content.SharedPreferences;

public interface MultiprocessSharedPreferences extends SharedPreferences {
    void registerOnMultiprocessPreferenceChangeListener(OnMultiprocessPreferenceChangeListener listener);
    void unregisterOnMultiprocessPreferenceChangeListener(OnMultiprocessPreferenceChangeListener listener);

    interface OnMultiprocessPreferenceChangeListener {
        void onMultiProcessPreferenceChange();
        void onMultiProcessPreferenceChange(String key, String type);
    }
}
