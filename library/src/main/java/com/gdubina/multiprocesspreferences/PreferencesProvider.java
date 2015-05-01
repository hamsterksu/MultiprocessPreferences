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

public class PreferencesProvider extends ContentProvider {

	public static String PREFFERENCE_AUTHORITY;
	public static Uri BASE_URI;

	private static final int MATCH_DATA = 0x010000;
	
	private static UriMatcher matcher;
	
	private static void init(Context context){
		
		PREFFERENCE_AUTHORITY = context.getString(R.string.multiprocess_preferences_authority);
		
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(PREFFERENCE_AUTHORITY, "*/*", MATCH_DATA);
		
		BASE_URI = Uri.parse("content://" + PREFFERENCE_AUTHORITY);
	}
	
	@Override
	public boolean onCreate() {
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
				PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext())
					.edit().clear().commit();
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
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
						getContext().getApplicationContext()).edit();
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
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
						getContext().getApplicationContext());
				if (!sharedPreferences.contains(key))
					return cursor;
				MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
				Object object = null;
				if (Constants.STRING_TYPE.equals(type)) {
					object = sharedPreferences.getString(key, null);
				} else if (Constants.BOOLEAN_TYPE.equals(type)) {
					object = sharedPreferences.getBoolean(key, false) ? 1 : 0;
				} else if (Constants.LONG_TYPE.equals(type)) {
					object = sharedPreferences.getLong(key, 0l);
				} else if (Constants.INT_TYPE.equals(type)) {
					object = sharedPreferences.getInt(key, 0);
				} else if (Constants.FLOAT_TYPE.equals(type)) {
					object = sharedPreferences.getFloat(key, 0f);
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

	public static final Uri getContentUri(Context context, String key, String type){
		if(BASE_URI == null){
			init(context);
		}
		return BASE_URI.buildUpon().appendPath(key).appendPath(type).build();
	}
}
