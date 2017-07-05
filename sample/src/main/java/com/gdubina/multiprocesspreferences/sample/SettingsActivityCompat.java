package com.gdubina.multiprocesspreferences.sample;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivityCompat extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
