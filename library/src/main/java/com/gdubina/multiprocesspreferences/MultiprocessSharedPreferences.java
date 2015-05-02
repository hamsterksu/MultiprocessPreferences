package com.gdubina.multiprocesspreferences;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MultiprocessSharedPreferences {

    private Context context;
    private List<WeakReference<OnSharedPreferenceChangeListener>> listeners;

    public MultiprocessSharedPreferences(Context context){
        this.context = context;
        this.listeners = new ArrayList<WeakReference<OnSharedPreferenceChangeListener>>();
        context.getContentResolver().registerContentObserver(
                PreferencesProvider.getBaseUri(context), true, new Observer());

    }

    public Editor edit(){
        return new Editor(context);
    }

    public String getString(String key, String def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.STRING_TYPE), null, null, null, null);
        return CursorUtils.getStringValue(cursor, def);
    }

    public long getLong(String key, long def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.LONG_TYPE), null, null, null, null);
        return CursorUtils.getLongValue(cursor, def);
    }

    public float getFloat(String key, float def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.FLOAT_TYPE), null, null, null, null);
        return CursorUtils.getFloatValue(cursor, def);
    }

    public boolean getBoolean(String key, boolean def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.BOOLEAN_TYPE), null, null, null, null);
        return CursorUtils.getBooleanValue(cursor, def);
    }

    public int getInt(String key, int def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.INT_TYPE), null, null, null, null);
        return CursorUtils.getIntValue(cursor, def);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (listener != null) {
            listeners.add(new WeakReference<OnSharedPreferenceChangeListener>(listener));
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (listener != null) {
            listeners.remove(new WeakReference<OnSharedPreferenceChangeListener>(listener));
        }
    }


    private class Observer extends ContentObserver {

        public Observer() {
            super(null);
        }

        //SDK_INT < 16
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            for (WeakReference<OnSharedPreferenceChangeListener> ref : listeners) {
                OnSharedPreferenceChangeListener listener = ref.get();
                if (listener != null) {
                    listener.onMultiProcessPreferenceChange();
                }
            }
        }

        //SDK_INT >= 16
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri != null) {
                final String key = uri.getPathSegments().get(0);
                final String type = uri.getPathSegments().get(1);
                for (WeakReference<OnSharedPreferenceChangeListener> ref: listeners) {
                    OnSharedPreferenceChangeListener listener = ref.get();
                    if (listener != null) {
                        listener.onMultiProcessPreferenceChange(key, type);
                    }
                }
            }
        }
    }
}
