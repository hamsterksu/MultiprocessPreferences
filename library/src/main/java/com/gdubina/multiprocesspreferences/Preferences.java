package com.gdubina.multiprocesspreferences;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Preferences implements MultiprocessSharedPreferences {

    private Context context;
    private List<WeakReference<OnMultiprocessPreferenceChangeListener>> listeners;

    public Preferences(Context context){
        this.context = context;
        this.listeners = new ArrayList<WeakReference<OnMultiprocessPreferenceChangeListener>>();
        context.getContentResolver().registerContentObserver(
                PreferencesProvider.getBaseUri(context), true, new Observer());

    }

    //This will be implemented later
    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit(){
        return new com.gdubina.multiprocesspreferences.Editor(context);
    }

    //This will be implemented later
    @Override
    public Map<String, ?> getAll() {
        return new HashMap<String, Object>();
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.BOOLEAN_TYPE), null, null, null, null);
        return CursorUtils.getBooleanValue(cursor, def);
    }

    @Override
    public float getFloat(String key, float def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.FLOAT_TYPE), null, null, null, null);
        return CursorUtils.getFloatValue(cursor, def);
    }

    @Override
    public int getInt(String key, int def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.INT_TYPE), null, null, null, null);
        return CursorUtils.getIntValue(cursor, def);
    }

    @Override
    public long getLong(String key, long def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.LONG_TYPE), null, null, null, null);
        return CursorUtils.getLongValue(cursor, def);
    }

    @Override
    public String getString(String key, String def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Types.STRING_TYPE), null, null, null, null);
        return CursorUtils.getStringValue(cursor, def);
    }

    //This will be implemented later
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return new HashSet<String>();
    }

    //Use registerOnMultiprocessPreferenceChangeListener(OnSharedPreferenceChangeListener listener) instead
    @Deprecated
    @Override
    public void registerOnSharedPreferenceChangeListener(android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {}

    //Use unregisterOnMultiprocessPreferenceChangeListener(OnSharedPreferenceChangeListener listener) instead
    @Deprecated
    @Override
    public void unregisterOnSharedPreferenceChangeListener(android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {}

    @Override
    public void registerOnMultiprocessPreferenceChangeListener(OnMultiprocessPreferenceChangeListener listener) {
        if (listener != null) {
            listeners.add(new WeakReference<OnMultiprocessPreferenceChangeListener>(listener));
        }
    }

    @Override
    public void unregisterOnMultiprocessPreferenceChangeListener(OnMultiprocessPreferenceChangeListener listener) {
        if (listener != null) {
            listeners.remove(new WeakReference<OnMultiprocessPreferenceChangeListener>(listener));
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
            for (WeakReference<OnMultiprocessPreferenceChangeListener> ref : listeners) {
                OnMultiprocessPreferenceChangeListener listener = ref.get();
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
                for (WeakReference<OnMultiprocessPreferenceChangeListener> ref: listeners) {
                    OnMultiprocessPreferenceChangeListener listener = ref.get();
                    if (listener != null) {
                        listener.onMultiProcessPreferenceChange(key, type);
                    }
                }
            }
        }
    }
}
