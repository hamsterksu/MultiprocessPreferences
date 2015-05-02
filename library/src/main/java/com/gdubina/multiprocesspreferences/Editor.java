package com.gdubina.multiprocesspreferences;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class Editor {
    Context context;

    public Editor(Context context){
        this.context = context;
    }

    private ContentValues values = new ContentValues();

    public void apply(){
        context.getContentResolver().insert(
                PreferencesProvider.getContentUri(context, Types.KEY, Types.TYPE), values);
    }

    public void commit(){
        apply();
    }

    public Editor putString(String key, String value) {
        values.put(key, value);
        return this;
    }

    public Editor putLong(String key, long value) {
        values.put(key, value);
        return this;
    }

    public Editor putBoolean(String key, boolean value) {
        values.put(key, value);
        return this;
    }

    public Editor putInt(String key, int value) {
        values.put(key, value);
        return this;
    }

    public Editor putFloat(String key, float value) {
        values.put(key, value);
        return this;
    }

    public void remove(String key) {
        values.putNull(key);
    }

    /**
     * Call content provider method immediately. apply or commit is not required for this case
     * So it's sync method.
     */
    public void clear(){
        Uri uri = PreferencesProvider.getContentUri(context, Types.KEY, Types.TYPE);
        context.getContentResolver().delete(uri, null, null);
    }
}
