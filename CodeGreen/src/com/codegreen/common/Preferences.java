/**
 * =================================================================================================================
 * File Name            : Preferences.java
 * Author               : vishal.r @ Endeavour Software Technologies pvt. ltd., Bangalore.
 * Version              : 0.1
 * Date                 : 29-Mar-2011
 * Copyright            : Digi-Data Corporation.
 * Description          : 
 * To Do List           : TODO
 * History              :
 * =====================================================================================================================
 *  Sr. No.     Date            Name                            Reviewed By                                     Description
 * =====================================================================================================================
 *              29-Mar-2011     vishal.r                     vishal.r                                             Initial Version. 
 * =====================================================================================================================
 */
package com.codegreen.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author ravindra.kamble
 *
 */
public class Preferences {
	// Preferences keys

	public static final boolean DEFAULT_BOOLEAN = false;
	public static final String  DEFAULT_STRING	= null;
	public static final float 	DEFAULT_FLOAT 	= 0.0f;
	public static final long 	DEFAULT_LONG	= 0l;
	public static final int 	DEFAULT_INT 	= 0;

	private static Preferences instance;

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferencesEditor;

	/**
	 * Private constructor for singleton pattern.
	 * @param context A Context object to allow access to the default SharedPreferences object.
	 */
	private Preferences(Context context){
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

		sharedPreferencesEditor = sharedPreferences.edit();

	}

	public void clearAllPrefs(){
		sharedPreferencesEditor.clear();
		sharedPreferencesEditor.commit();
	}

	/**
	 * Access method for the Preferences singleton.  
	 * @param context A Context object to allow access to the default SharedPreferences object.
	 * @return The Preferences object for this app.
	 */
	public static synchronized Preferences getInstance(Context context){
		if (instance == null){
			instance = new Preferences(context);
		}

		return instance;
	}


	public boolean getBoolean(String key){
		boolean returnVal = DEFAULT_BOOLEAN;

		if (sharedPreferences != null){
			returnVal = sharedPreferences.getBoolean(key, DEFAULT_BOOLEAN);
		}

		return returnVal;
	}


	public float getFloat(String key){
		float returnVal = DEFAULT_FLOAT;

		if (sharedPreferences != null){
			returnVal = sharedPreferences.getFloat(key, DEFAULT_FLOAT);
		}

		return returnVal;
	}


	public int getInt(String key){
		int returnVal = DEFAULT_INT;

		if (sharedPreferences != null){
			returnVal = sharedPreferences.getInt(key, DEFAULT_INT);
		}

		return returnVal;
	}


	public long getLong(String key){
		long returnVal = DEFAULT_LONG;

		if (sharedPreferences != null){
			returnVal = sharedPreferences.getLong(key, DEFAULT_LONG);
		}

		return returnVal;
	}


	public String getString(String key){
		String returnVal = DEFAULT_STRING;

		if (sharedPreferences != null){
			returnVal = sharedPreferences.getString(key, DEFAULT_STRING);
		}

		return returnVal;
	}


	// Setters.
	public void addOrUpdateBoolean(String key, boolean value){
		synchronized (this) {
			if (sharedPreferencesEditor != null){
				sharedPreferencesEditor.putBoolean(key, value);

				sharedPreferencesEditor.commit();
			}
		}
	}


	public void addOrUpdateFloat(String key, float value){
		synchronized (this) {
			if (sharedPreferencesEditor != null){
				sharedPreferencesEditor.putFloat(key, value);

				sharedPreferencesEditor.commit();
			}
		}
	}


	public void addOrUpdateInt(String key, int value){
		synchronized (this) {
			if (sharedPreferencesEditor != null){
				sharedPreferencesEditor.putInt(key, value);

				sharedPreferencesEditor.commit();
			}
		}
	}


	public void addOrUpdateLong(String key, long value){
		synchronized (this) {
			if (sharedPreferencesEditor != null){
				sharedPreferencesEditor.putLong(key, value);

				sharedPreferencesEditor.commit();
			}
		}
	}


	public void addOrUpdateString(String key, String value){
		synchronized (this) {
			if (sharedPreferencesEditor != null){
				sharedPreferencesEditor.putString(key, value);

				sharedPreferencesEditor.commit();
			}
		}
	}


	public void addOrUpdateStringsBatch(String[] keys, String[] values){
		synchronized (this) {
			if ((keys != null) && (values != null) && (keys.length == values.length)){
				if (sharedPreferencesEditor != null){
					for (int i = keys.length; --i >= 0; ){
						sharedPreferencesEditor.putString(keys[i], values[i]);
					}

					sharedPreferencesEditor.commit();
				}
			}
		}
	}


	public void delete(String key){
		synchronized (this) {
			if (sharedPreferencesEditor != null){
				sharedPreferencesEditor.remove(key);

				sharedPreferencesEditor.commit();
			}
		}
	}
}
