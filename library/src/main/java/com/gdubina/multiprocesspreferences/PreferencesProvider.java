package com.gdubina.multiprocesspreferences;

import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

public class PreferencesProvider extends ContentProvider implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String PREFFERENCE_AUTHORITY;
	private static Uri BASE_URI;

	private static final int MATCH_DATA = 0x010000;
	
	private static UriMatcher matcher;

	private SharedPreferences prefs;

	private static void init(Context context){
		
		PREFFERENCE_AUTHORITY = context.getResources().getString(R.string.multiprocess_preferences_authority);
		
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(PREFFERENCE_AUTHORITY, "*/*", MATCH_DATA);
		
		BASE_URI = Uri.parse("content://" + PREFFERENCE_AUTHORITY);
	}
	
	@Override
	public boolean onCreate() {
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
		prefs.registerOnSharedPreferenceChangeListener(this);
		if(matcher == null){
			init(getContext());
		}
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + PREFFERENCE_AUTHORITY + ".item";
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (matcher.match(uri)) {
			case MATCH_DATA:
				prefs.edit().clear().commit();
				break;
			default:
				throw new IllegalArgumentException("Unsupported uri " + uri);
		}

		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (matcher.match(uri)) {
			case MATCH_DATA:
				SharedPreferences.Editor editor = prefs.edit();
				for (Entry<String, Object> entry : values.valueSet()) {
					final Object value = entry.getValue();
					final String key = entry.getKey();
					if(value == null){
						editor.remove(key);
					}else if (value instanceof String)
						editor.putString(key, (String) value);
					else if (value instanceof Boolean)
						editor.putBoolean(key, (Boolean) value);
					else if (value instanceof Long)
						editor.putLong(key, (Long) value);
					else if (value instanceof Integer)
						editor.putInt(key, (Integer) value);
					else if (value instanceof Float)
						editor.putFloat(key, (Float) value);
					else {
						throw new IllegalArgumentException("Unsupported type " + uri);
					}
				}
				if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO){
					editor.apply();
				}else{
					editor.commit();
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported uri " + uri);
		}

		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = null;
		switch (matcher.match(uri)) {
			case MATCH_DATA:
				final String key = uri.getPathSegments().get(0);
				final String type = uri.getPathSegments().get(1);
				cursor = new MatrixCursor(new String[] { key });
				if (!prefs.contains(key))
					return cursor;
				MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
				Object object = null;
				if (Types.STRING_TYPE.equals(type)) {
					object = prefs.getString(key, null);
				} else if (Types.BOOLEAN_TYPE.equals(type)) {
					object = prefs.getBoolean(key, false) ? 1 : 0;
				} else if (Types.LONG_TYPE.equals(type)) {
					object = prefs.getLong(key, 0l);
				} else if (Types.INT_TYPE.equals(type)) {
					object = prefs.getInt(key, 0);
				} else if (Types.FLOAT_TYPE.equals(type)) {
					object = prefs.getFloat(key, 0f);
				} else {
					throw new IllegalArgumentException("Unsupported type " + uri);
				}
				rowBuilder.add(object);
				break;
			default:
				throw new IllegalArgumentException("Unsupported uri " + uri);
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		String type = null;
		for (Entry<String, ?> entry: sharedPreferences.getAll().entrySet()) {
			if(key.equals(entry.getKey())) {
				Object value = entry.getValue();
				if (value instanceof Integer) {
					type = Types.INT_TYPE;
				} else if (value instanceof Float) {
					type = Types.FLOAT_TYPE;
				} else if (value instanceof Long) {
					type = Types.LONG_TYPE;
				} else if (value instanceof String) {
					type = Types.STRING_TYPE;
				} else if (value instanceof  Boolean) {
					type = Types.BOOLEAN_TYPE;
				}
				break;
			}
		}
		if (type != null) {
			Uri uri = getContentUri(getContext(), key, type);
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}

	public static final Uri getContentUri(Context context, String key, String type){
		if(BASE_URI == null){
			init(context);
		}
		return BASE_URI.buildUpon().appendPath(key).appendPath(type).build();
	}

	public static Uri getBaseUri() {
		return BASE_URI;
	}
}
