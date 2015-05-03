package com.gdubina.multiprocesspreferences;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.Set;

public class Editor implements SharedPreferences.Editor {
    private Context context;

    public Editor(Context context){
        this.context = context;
    }

    private ContentValues values = new ContentValues();

    @Override
    public void apply(){
        context.getContentResolver().insert(
                PreferencesProvider.getContentUri(context, Types.KEY, Types.TYPE), values);
    }

    /**
     * Call content provider method immediately. apply or commit is not required for this case
     * So it's sync method.
     */
    @Override
    public SharedPreferences.Editor clear(){
        Uri uri = PreferencesProvider.getContentUri(context, Types.KEY, Types.TYPE);
        context.getContentResolver().delete(uri, null, null);
        return this;
    }

    @Override
    public boolean commit(){
        apply();
        return false;
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        values.put(key, value);
        return this;
    }

    @Override
    public Editor putFloat(String key, float value) {
        values.put(key, value);
        return this;
    }

    @Override
    public Editor putInt(String key, int value) {
        values.put(key, value);
        return this;
    }

    @Override
    public Editor putLong(String key, long value) {
        values.put(key, value);
        return this;
    }

    @Override
    public Editor putString(String key, String value) {
        values.put(key, value);
        return this;
    }

    //it will be implemented later
    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        values.putNull(key);
        return this;
    }
}
