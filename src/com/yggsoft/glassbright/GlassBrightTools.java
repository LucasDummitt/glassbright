// Copyright 2014 Lucas Dummitt https://plus.google.com/+LucasDummitt

package com.yggsoft.glassbright;

import android.content.Context;
import android.util.Log;

public class GlassBrightTools {
    public static final String APP_NAME = "GlassBright";
    public static final String CURRENT_VERSION = "1.0.0.0";

	public static final String SETTING_GLASSBRIGHT_ENABLED = "GlassBrightEnabled";
	public static final String SETTING_TIMER = "GlassBrightTimer";
    public static final String SETTING_ENABLED_VALUE = "1";
    public static final String SETTING_DISABLED_VALUE = "0";
    public static final String SETTING_TIMER_DEFAULT = "10";

	private static final String TOOLS_TAG = "GBTools";

    // If the app settings don't exist, create them set to the defaults
    public static void CreateGlassBrightSettings(Context currentContext)
    {
    	GlassBrightDbAdapter mSettings = new GlassBrightDbAdapter(currentContext);
		mSettings.open();
		String strValue = mSettings.getSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED);
		if(strValue == null)
		{
			mSettings.createSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED, GlassBrightTools.SETTING_DISABLED_VALUE);
		}
		strValue = mSettings.getSetting(GlassBrightTools.SETTING_TIMER);
		if(strValue == null)
		{
			// If it doesn't exist, create it and reload.
			mSettings.createSetting(GlassBrightTools.SETTING_TIMER, GlassBrightTools.SETTING_TIMER_DEFAULT);
		}		
		mSettings.close();
    }

    // Forces all of the system and app settings to what is required to enable GlassBright, regardless of whether it is enabled or not.
    public static void EnableGlassBright(Context currentContext)
    {
    	Log.d(TOOLS_TAG, "Entering EnableGlassBright");
    	GlassBrightDbAdapter mSettings = new GlassBrightDbAdapter(currentContext);
		mSettings.open();
		String strValue = mSettings.getSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED);
		mSettings.updateSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED, GlassBrightTools.SETTING_ENABLED_VALUE);
		android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);			
		strValue = mSettings.getSetting(GlassBrightTools.SETTING_TIMER);
		int intValue = Integer.parseInt(strValue);
		if(intValue != -1)
		{
			android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, intValue * 1000);
		}
		else
		{
			android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, intValue);				
		}
        
        
        mSettings.close();
    	Log.d(TOOLS_TAG, "Exiting EnableGlassBright");
    }    
    
    // Forces all of the system and app settings to what is required to disable GlassBright, regardless of whether it is disabled or not.    
    public static void DisableGlassBright(Context currentContext)
    {
    	Log.d(TOOLS_TAG, "Entering DisableGlassBright");
    	
    	GlassBrightDbAdapter mSettings = new GlassBrightDbAdapter(currentContext);
		mSettings.open();
		mSettings.updateSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED, GlassBrightTools.SETTING_DISABLED_VALUE);
		android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		mSettings.close();
    	Log.d(TOOLS_TAG, "Exiting DisableGlassBright");
    }

    // Enables GlassBright if it is disabled, or disables it if it is enabled.
    public static boolean ToggleGlassBright(Context currentContext)
    {
    	Log.d(TOOLS_TAG, "Entering ToggleGlassBright");
    	GlassBrightDbAdapter mSettings = new GlassBrightDbAdapter(currentContext);
		mSettings.open();
		String strValue = mSettings.getSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED);
		boolean modeSet = false;
		boolean brightnessSet = false;
		boolean enabledBright = false;

		if(strValue.equals(GlassBrightTools.SETTING_ENABLED_VALUE))
		{
	    	Log.d(TOOLS_TAG, "Setting existed and was enabled.  Disabling...");
			mSettings.updateSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED, GlassBrightTools.SETTING_DISABLED_VALUE);
			modeSet = android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		}
		else if(strValue.equals(GlassBrightTools.SETTING_DISABLED_VALUE))
		{
	    	Log.d(TOOLS_TAG, "Setting existed and was disabled.  Enabling...");
			mSettings.updateSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED, GlassBrightTools.SETTING_ENABLED_VALUE);			
			modeSet = android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            brightnessSet = android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);			
			strValue = mSettings.getSetting(GlassBrightTools.SETTING_TIMER);
			int intValue = Integer.parseInt(strValue);
			if(intValue != -1)
			{
				android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, intValue * 1000);
			}
			else
			{
				android.provider.Settings.System.putInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, intValue);				
			}
            enabledBright = true;
		}
		else
		{
			Log.d(TOOLS_TAG, "Unexpected value encountered: " + strValue);
		}
		Log.d(TOOLS_TAG, "Mode set: " + modeSet);
		Log.d(TOOLS_TAG, "Brightness set: " + brightnessSet);
		mSettings.close();
    	Log.d(TOOLS_TAG, "Exiting ToggleGlassBright");
    	return enabledBright;
    }
    
    // Sets the timeout to a new value
    public static String SetGlassBrightTimeout(Context currentContext)
    {
    	GlassBrightDbAdapter mSettings = new GlassBrightDbAdapter(currentContext);
		mSettings.open();
		String strValue = mSettings.getSetting(GlassBrightTools.SETTING_TIMER);
		int intValue = Integer.parseInt(strValue);
		int newValue;
		switch(intValue)
		{
		    case 5:
		    	newValue = 10;
			    break;
		    case 10:
		    	newValue = 15;
		    	break;
		    case 15:
		    	newValue = 30;
		    	break;
		    case 30:
		    	newValue = -1;
		    	break;
		    case -1:
		    	newValue = 5;
		        break;
		    default:
		    	newValue = 10;
		    	break;		
		}
		mSettings.updateSetting(GlassBrightTools.SETTING_TIMER, Integer.toString(newValue));
		mSettings.close();
		Log.d(TOOLS_TAG, "Set the new timeout to: " + newValue);
		return Integer.toString(newValue);
    }    
	
    // Under certain conditions, Glass will change the power settings.  This returns true if this has happened
    public static boolean IsPowerReset(Context currentContext, int settingTimeout)
    {
    	Log.d(TOOLS_TAG, "Starting IsPowerReset");
		int currentMode = android.provider.Settings.System.getInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, -2);
        // int currentBrightness = android.provider.Settings.System.getInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, -2);
        int currentTimeout = android.provider.Settings.System.getInt(currentContext.getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -2);

        if(currentMode == -2 || currentTimeout == -2)
        {
        	Log.w(TOOLS_TAG, "Unable to get power settings!  This should never happen.");
        }
        // If brightness is set to automatic of the timeout doesn't equal the timeout set, return true
        else if(currentMode == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC || (settingTimeout*1000) != currentTimeout)
        {
        	Log.d(TOOLS_TAG, "IsPowerReset is true");
        	return true;
        }
    	Log.d(TOOLS_TAG, "IsPowerReset is false (default case)");
        return false;
    }
}
