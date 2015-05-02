package com.gdubina.multiprocesspreferences.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.gdubina.multiprocesspreferences.MultiprocessPreferenceManager;
import com.gdubina.multiprocesspreferences.MultiprocessSharedPreferences;
import com.gdubina.multiprocesspreferences.OnSharedPreferenceChangeListener;


public class SampleService extends Service implements OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = SampleService.class.getName();

    private boolean mChecked;
    private String mString;

    private MultiprocessSharedPreferences mPrefs;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Create");
        mPrefs = MultiprocessPreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mChecked = mPrefs.getBoolean(getString(R.string.pref_key_checkbox), false);
        Log.d(LOG_TAG, String.format("Init: checkbox value - %b", mChecked));
        mString = mPrefs.getString(getString(R.string.pref_key_list), getString(R.string.pref_default_list));
        Log.d(LOG_TAG, String.format("Init: list value - %s", mString));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Destroy");
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    //SDK_INT < 16
    @Override
    public void onMultiProcessPreferenceChange() {
        Log.d(LOG_TAG, "SDK VERSION < 16");
    }

    //SDK_INT >= 16
    @Override
    public void onMultiProcessPreferenceChange(String key, String type) {
        Log.d(LOG_TAG, String.format("Key - %1$s   Type - %2$s", key, type));
        if (key.equals(getString(R.string.pref_key_checkbox))) {
            boolean value = mPrefs.getBoolean(key, false);
            if (value != mChecked) {
                Toast.makeText(this, String.format("%1$b => %2$b", mChecked, value), Toast.LENGTH_SHORT).show();
                mChecked = value;
            }
        }
        else if (key.equals(getString(R.string.pref_key_list))) {
            String value = mPrefs.getString(getString(R.string.pref_key_list), getString(R.string.pref_default_list));
            if (!value.equals(mString)) {
                Toast.makeText(this, String.format("%1$s => %2$s", mString, value), Toast.LENGTH_SHORT).show();
                mString = value;
            }
        }
    }
}
