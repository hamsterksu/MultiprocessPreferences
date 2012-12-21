MultiprocessPreferences
=======================

this lib allow you to use shared preferences between a few processes on android  api &lt; 11

Before android api 11 we can't use shared preferences between different processes of the app. 
So solution for this case is - use content provider. This lib is wrapper over content provider which emulates sharedpreferences interface.  

  

**MultiprocessPreferences** - content provider which wraps sharedpreferences 
**MultiprocessSharedPreferences** - emulate shared preferences class

To use it you should declare provider in manifest 

	<provider 
            android:name="com.gdubina.multiprocesspreferences.MultiprocessPreferences" 
            android:authorities="@string/multiprocess_preferences_authority"
            android:exported="false"
	/>
    
and define authority in strings.xml like this

	<string name="multiprocess_preferences_authority" translatable="false">com.gdubina.multiprocesspreferences.PREFFERENCE_AUTHORITY</string>


##Example

*Default sharedprefferences*  


	public static void saveToken(Context context, String token) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TOKEN, token).commit();//or apply()
	}
	
	public static String getToken(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TOKEN, null);
	}



*MultiprocessPreferences*


	public static void saveToken(Context context, String token) {
		MultiprocessPreferences.getDefaultSharedPreferences(context).edit().putString(PREF_TOKEN, token).commit();//or apply()
	}
	
	public static String getToken(Context context){
		return MultiprocessPreferences.getDefaultSharedPreferences(context).getString(PREF_TOKEN, null);
	}
