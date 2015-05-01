package com.gdubina.multiprocesspreferences;

import android.content.Context;
import android.database.Cursor;

class MultiprocessSharedPreferences {
    private Context context;

    public MultiprocessSharedPreferences(Context context){
        this.context = context;
    }

    public Editor edit(){
        return new Editor(context);
    }

    public String getString(String key, String def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Constants.STRING_TYPE), null, null, null, null);
        return CursorUtils.getStringValue(cursor, def);
    }

    public long getLong(String key, long def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Constants.LONG_TYPE), null, null, null, null);
        return CursorUtils.getLongValue(cursor, def);
    }

    public float getFloat(String key, float def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Constants.FLOAT_TYPE), null, null, null, null);
        return CursorUtils.getFloatValue(cursor, def);
    }

    public boolean getBoolean(String key, boolean def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Constants.BOOLEAN_TYPE), null, null, null, null);
        return CursorUtils.getBooleanValue(cursor, def);
    }

    public int getInt(String key, int def) {
        Cursor cursor = context.getContentResolver().query(
                PreferencesProvider.getContentUri(context, key, Constants.INT_TYPE), null, null, null, null);
        return CursorUtils.getIntValue(cursor, def);
    }
}
