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

public class MultiprocessPreferences extends ContentProvider{

	public static String PREFFERENCE_AUTHORITY;
	public static Uri BASE_URI;
	
	private static final String TYPE = "type";
	private static final String KEY = "key";

	private static final String INT_TYPE = "integer";
	private static final String LONG_TYPE = "long";
	private static final String FLOAT_TYPE = "float";
	private static final String BOOLEAN_TYPE = "boolean";
	private static final String STRING_TYPE = "string";

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
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
			editor.clear();
			editor.commit();
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
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
			for (Entry<String, Object> entry : values.valueSet()) {
				final Object value = entry.getValue();
				final String key = entry.getKey();
				if(value == null){
					editor.remove(key);
				}else if (value instanceof Long)
					editor.putLong(key, (Long) value);
				else if (value instanceof Integer)
					editor.putInt(key, (Integer) value);
				else if (value instanceof Float)
					editor.putFloat(key, (Float) value);
				else if (value instanceof Boolean)
					editor.putBoolean(key, (Boolean) value);
				else if (value instanceof String)
					editor.putString(key, (String) value);
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
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
			if (!sharedPreferences.contains(key))
				return cursor;
			MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
			Object object = null;
			if (STRING_TYPE.equals(type)) {
				object = sharedPreferences.getString(key, null);
			} else if (BOOLEAN_TYPE.equals(type)) {
				object = sharedPreferences.getBoolean(key, false) ? 1 : 0;
			} else if (INT_TYPE.equals(type)) {
				object = sharedPreferences.getInt(key, 0);
			} else if (LONG_TYPE.equals(type)) {
				object = sharedPreferences.getLong(key, 0l);
			} else if (FLOAT_TYPE.equals(type)) {
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
	
	private static final Uri getContentUri(String key, String type){
		return BASE_URI.buildUpon().appendPath(key).appendPath(type).build();
	}
	
	public static void clear(Context context){
		context.getContentResolver().delete(getContentUri(KEY, TYPE), null, null);
	} 
	
	public static String getString(Context context, String key, String def) {
		Cursor cursor = context.getContentResolver().query(getContentUri(key, STRING_TYPE), null, null, null, null);
		return getStringValue(cursor, def);
	}
	
	public static long getLong(Context context, String key, long def) {
		Cursor cursor = context.getContentResolver().query(getContentUri(key, LONG_TYPE), null, null, null, null);
		return getLongValue(cursor, def);
	}
	
	public static float getFloat(Context context, String key, float def) {
		Cursor cursor = context.getContentResolver().query(getContentUri(key, FLOAT_TYPE), null, null, null, null);
		return getFloatValue(cursor, def);
	}
	
	public static boolean getBoolean(Context context, String key, boolean def) {
		Cursor cursor = context.getContentResolver().query(getContentUri(key, BOOLEAN_TYPE), null, null, null, null);
		return getBooleanValue(cursor, def);
	}
	
	public static int getInt(Context context, String key, int def) {
		Cursor cursor = context.getContentResolver().query(getContentUri(key, INT_TYPE), null, null, null, null);
		return getIntValue(cursor, def);
	}
	
	private static String getStringValue(Cursor cursor, String def) {	
		if(cursor == null)
			return def;
		String value = def;
		if (cursor.moveToFirst()) {
			value = cursor.getString(0);
		}
		cursor.close();
		return value;		
	}
	
	private static boolean getBooleanValue(Cursor cursor, boolean def) {	
		if(cursor == null)
			return def;
		boolean value = def;
		if (cursor.moveToFirst()) {
			value = cursor.getInt(0) > 0;
		}
		cursor.close();
		return value;		
	}
	
	private static int getIntValue(Cursor cursor, int def) {
		if(cursor == null)
			return def;
		int value = def;
		if (cursor.moveToFirst()) {
			value = cursor.getInt(0);
		}
		cursor.close();
		return value;		
	}
	
	private static long getLongValue(Cursor cursor, long def) {		
		if(cursor == null)
			return def;
		long value = def;
		if (cursor.moveToFirst()) {
			value = cursor.getLong(0);
		}
		cursor.close();
		return value;		
	}
	
	private static float getFloatValue(Cursor cursor, float def) {		
		if(cursor == null)
			return def;
		float value = def;
		if (cursor.moveToFirst()) {
			value = cursor.getFloat(0);
		}
		cursor.close();
		return value;		
	}
	
	public static Editor edit(Context context){
		return new Editor(context);
	}
	
	public static MultiprocessSharedPreferences getDefaultSharedPreferences(Context context){
		return new MultiprocessSharedPreferences(context);
	}
	
	public static class Editor{
		
		Context context;
		
		private  Editor(Context context){
			this.context = context;
		}
		
		private ContentValues values = new ContentValues();

		public void apply(){
			context.getContentResolver().insert(getContentUri(KEY, TYPE), values);		
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
			MultiprocessPreferences.clear(context);
		}
	}
	
	public static class MultiprocessSharedPreferences{
		
		private Context context;
		
		private MultiprocessSharedPreferences(Context context){
			this.context = context;
		}
		
		public Editor edit(){
			return new Editor(context);
		}
		
		public String getString(String key, String def) {
			return MultiprocessPreferences.getString(context, key, def);
		}
		
		public long getLong(String key, long def) {
			return MultiprocessPreferences.getLong(context, key, def);
		}
		
		public float getFloat(String key, float def) {
			return MultiprocessPreferences.getFloat(context, key, def);
		}
		
		public boolean getBoolean(String key, boolean def) {
			return MultiprocessPreferences.getBoolean(context, key, def);
		}
		
		public int getInt(String key, int def) {
			return MultiprocessPreferences.getInt(context, key, def);
		}
	}
}
