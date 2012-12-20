MultiprocessPreferences
=======================

this lib allow you to use shared preferences between a few processes on android  api &lt; 11

Before android api 11 we can't use shared preferences between different processes of the app. 
So solution for this case is - use content provider. This lib is wrapper over content provider which emulates sharedpreferences interface.  

  

**MultiprocessPreferences** - content provider which wraps sharedpreferences 
**MultiprocessSharedPreferences** - emulate shared preferences class

##Example

*Default sharedprefferences  

`
	private static final String PREF_TOKEN = "token";
	
	public static void saveToken(Context context, String token) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TOKEN, token).commit();or apply()
	}
	
	public static String getToken(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TOKEN, null);
	}

`

*MultiprocessPreferences

`
	private static final String PREF_TOKEN = "token";
	
	public static void saveToken(Context context, String token) {
		MultiprocessPreferences.getDefaultSharedPreferences(context).edit().putString(PREF_TOKEN, token).commit();//or apply()
	}
	
	public static String getToken(Context context){
		return MultiprocessPreferences.getDefaultSharedPreferences(context).getString(PREF_TOKEN, null);
	}
`