package com.gdubina.multiprocesspreferences.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.gdubina.multiprocesspreferences.MultiprocessPreferenceManager;
import com.gdubina.multiprocesspreferences.MultiprocessSharedPreferences;

//This example show why you should use MultiprocessSharedPreferences on API 11+
public class SampleService2 extends Service implements  SharedPreferences.OnSharedPreferenceChangeListener,
        MultiprocessSharedPreferences.OnMultiprocessPreferenceChangeListener {

    public static final String LOG_TAG = SampleService2.class.getName();

    private SharedPreferences mPrefs;

    private boolean mChecked;
    private String mString;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            /* Enabled by default
            ComponentName provider = new ComponentName(getApplicationContext(), PreferencesProvider.class.getName());
            getApplicationContext().getPackageManager().setComponentEnabledSetting(
                    provider, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
             */
            mPrefs = MultiprocessPreferenceManager.getDefaultSharedPreferences(this);
            ((MultiprocessSharedPreferences)mPrefs ).registerOnMultiprocessPreferenceChangeListener(this);
        }
        else {
            mPrefs = getSharedPreferences(getApplicationContext().getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);

            //unnecessary if this service run in separate process. Context.MODE_MULTI_PROCESS not solve this problem. I do not comment next line for example.
            mPrefs.registerOnSharedPreferenceChangeListener(this);
        }
        mChecked = mPrefs.getBoolean(getString(R.string.pref_key_checkbox), false);
        Log.d(LOG_TAG, String.format("Init: checkbox value - %b", mChecked));
        mString = mPrefs.getString(getString(R.string.pref_key_list), getString(R.string.pref_default_list));
        Log.d(LOG_TAG, String.format("Init: list value - %s", mString));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ((MultiprocessSharedPreferences) mPrefs).unregisterOnMultiprocessPreferenceChangeListener(this);
            /*
            ComponentName provider = new ComponentName(getApplicationContext(), PreferencesProvider.class.getName());
            getApplicationContext().getPackageManager().setComponentEnabledSetting(
                    provider, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            */
        }
        else {
            mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    //SDK_INT < 16 (used on api < 11)
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

    //SDK_INT >= 16 (used default shared preferences with Context.MODE_MULTI_PROCESS on api 11+)
    @Override
    public void onMultiProcessPreferenceChange(String key, String type) {}

    //unnecessary if this service run in separate process. Context.MODE_MULTI_PROCESS not solve this problem. This method wil be not called.
    //Use MultiprocessSharedPreferences to be notified.
    @Override
    public void onSharedPreferenceChanged(android.content.SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_checkbox))) {
            boolean checked = sharedPreferences.getBoolean(key, false);
            if (checked != mChecked) {
                Log.d(LOG_TAG, String.format("Checkbox: %1$b => %2$b", mChecked, checked));
                mChecked = checked;
            }
        }
        else if (key.equals(getString(R.string.pref_key_list))) {
            String string = sharedPreferences.getString(key, getString(R.string.pref_default_list));
            if (!string.equals(mString)) {
                Log.d(LOG_TAG, String.format("List: %1$s => %2$s", mString, string));
                mString = string;
            }
        }
    }
}
