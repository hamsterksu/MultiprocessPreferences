package com.gdubina.multiprocesspreferences.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gdubina.multiprocesspreferences.MultiprocessPreferenceManager;
import com.gdubina.multiprocesspreferences.MultiprocessSharedPreferences;


//This example show how to use MultiprocessSharedPreferences
public class SampleService extends Service implements MultiprocessSharedPreferences.OnMultiprocessPreferenceChangeListener {

    public static final String LOG_TAG = SampleService.class.getName();

    private MultiprocessSharedPreferences mPrefs;

    private boolean mChecked;
    private String mString;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* Enabled by default
        ComponentName provider = new ComponentName(getApplicationContext(), PreferencesProvider.class.getName());
        getApplicationContext().getPackageManager().setComponentEnabledSetting(
                provider, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        */

        mPrefs = MultiprocessPreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnMultiprocessPreferenceChangeListener(this);
        mChecked = mPrefs.getBoolean(getString(R.string.pref_key_checkbox), false);
        Log.d(LOG_TAG, String.format("Init: checkbox value - %b", mChecked));
        mString = mPrefs.getString(getString(R.string.pref_key_list), getString(R.string.pref_default_list));
        Log.d(LOG_TAG, String.format("Init: list value - %s", mString));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrefs.unregisterOnMultiprocessPreferenceChangeListener(this);
        /*
        ComponentName provider = new ComponentName(getApplicationContext(), PreferencesProvider.class.getName());
        getApplicationContext().getPackageManager().setComponentEnabledSetting(
                provider, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
         */
    }

    //SDK_INT < 16
    @Override
    public void onMultiProcessPreferenceChange() {
        boolean checked = mPrefs.getBoolean(getString(R.string.pref_key_checkbox), false);
        if (checked != mChecked) {
            Log.d(LOG_TAG, String.format("Checkbox: %1$b => %2$b", mChecked, checked));
            mChecked = checked;
        }
        String string = mPrefs.getString(getString(R.string.pref_key_list), getString(R.string.pref_default_list));
        if (!string.equals(mString)) {
            Log.d(LOG_TAG, String.format("List: %1$s => %2$s", mString, string));
            mString = string;
        }
    }

    //SDK_INT >= 16
    @Override
    public void onMultiProcessPreferenceChange(String key, String type) {
        Log.d(LOG_TAG, String.format("Key - %1$s   Type - %2$s", key, type));
        if (key.equals(getString(R.string.pref_key_checkbox))) {
            boolean checked = mPrefs.getBoolean(key, false);
            if (checked != mChecked) {
                Log.d(LOG_TAG, String.format("Checkbox: %1$b => %2$b", mChecked, checked));
                mChecked = checked;
            }
        }
        else if (key.equals(getString(R.string.pref_key_list))) {
            String string = mPrefs.getString(key, getString(R.string.pref_default_list));
            if (!string.equals(mString)) {
                Log.d(LOG_TAG, String.format("List: %1$s => %2$s", mString, string));
                mString = string;
            }
        }
    }
}
