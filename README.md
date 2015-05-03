multiprocess-preferences
=======================

This lib allow you to use shared preferences between a few processes.

   Official framework does not support use shared preferences across multiple processes. Context.MODE_MULTI_PROCESS not solve this problem. So solution for this case is - use content provider. This lib is wrapper over content provider which emulates sharedpreferences interface. Content provider communicate with shared preferences from default process.

## How to use

See sample.

## Installation
0) Add the library as module to your project.

1) Override the authority

```
    	defaultConfig {
        	applicationId "Your app id"
        	resValue("string", "multiprocess_preferences_authority","Your app id.PreferenceProvider")
    	}
```
2) Enable content provider when your need it.
```
	ComponentName provider = new ComponentName(getApplicationContext(), PreferencesProvider.class.getName());
        getApplicationContext().getPackageManager().setComponentEnabledSetting(
                provider, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
```
3) Use it as default shared preferences.
```
	MultiprocessSharedPreferences prefs = MultiprocessPreferenceManager.getDefaultSharedPreferences(this);
```
