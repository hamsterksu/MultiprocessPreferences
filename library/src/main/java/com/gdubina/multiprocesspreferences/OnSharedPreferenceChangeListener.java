package com.gdubina.multiprocesspreferences;

public interface OnSharedPreferenceChangeListener {
    void onMultiProcessPreferenceChange();
    void onMultiProcessPreferenceChange(String key, String type);
}
