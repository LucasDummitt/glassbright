// Copyright 2014 Lucas Dummitt https://plus.google.com/+LucasDummitt

package com.yggsoft.glassbright;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

@SuppressLint("Wakelock")
public class GlassBrightReceiver extends BroadcastReceiver {
	String RECEIVER_TAG = "GBReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	Log.d(RECEIVER_TAG, "Entering onReceive");
    	if(intent.getAction() != null)
    	{
    		String strIntentReceived = intent.getAction();
    		if(strIntentReceived.equals(Intent.ACTION_SCREEN_ON))
    		{
    	    	Log.d(RECEIVER_TAG, "ACTION_SCREEN_ON intent received");

    			GlassBrightDbAdapter mSettings = new GlassBrightDbAdapter(context);
    			mSettings.open();
    			String strValue;
    			strValue = mSettings.getSetting(GlassBrightTools.SETTING_GLASSBRIGHT_ENABLED);
    			if(strValue.equals(GlassBrightTools.SETTING_ENABLED_VALUE))
    			{
    		    	Log.d(RECEIVER_TAG, "ACTION_SCREEN_ON received and GLASSBRIGHT_ENABLED is true");
    		    	
        			// Get amount of time to stay bright
    				strValue = mSettings.getSetting(GlassBrightTools.SETTING_TIMER);
        			int intValue = Integer.parseInt(strValue);

        			// Double check that power settings haven't been changed by the system.  If it has, set it back to the enabled settings.
    		    	if(GlassBrightTools.IsPowerReset(context, intValue))
    		    	{
    		    		GlassBrightTools.EnableGlassBright(context);
    		    	}

    		    	String autoSetting = mSettings.getSetting(GlassBrightTools.SETTING_GLASSBRIGHT_AUTO);
    		    	if(autoSetting.equals(GlassBrightTools.SETTING_ENABLED_VALUE))
    		    	{
    		    		// We won't do anything, because the user wants Glass to handle brightness
    		    		PowerManager pm;
    		    		PowerManager.WakeLock wl;
    		    		pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    		    		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "GlassBright WL D");
    		    		if(intValue != -1)
    		    		{
    		    			wl.acquire(intValue * 1000);
    		    			Log.d(RECEIVER_TAG, "WakeLock acquired.  Timeout: " + (intValue * 1000) + " milliseconds.");
    		    		}
    		    		else
    		    		{
    		    			// If it  never goes off, we'll use a default 10 seconds for the wakelock
    		    			wl.acquire();
    		    			Log.d(RECEIVER_TAG, "WakeLock acquired.  This lock won't time out!");
    		    		}    		    	}
    		    	else
    		    	{
    		    		
    		    		// Wakelock will keep head motions from shutting off (so rough head motions will not wake up and put it back to sleep)
    		    		PowerManager pm;
    		    		PowerManager.WakeLock wl;
    		    		pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    		    		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "GlassBright WL B");
    		    		if(intValue != -1)
    		    		{
    		    			wl.acquire(intValue * 1000);
    		    			Log.d(RECEIVER_TAG, "WakeLock acquired.  Timeout: " + (intValue * 1000) + " milliseconds.");
    		    		}
    		    		else
    		    		{
    		    			// If it  never goes off, we'll use a default 10 seconds for the wakelock
    		    			wl.acquire();
    		    			Log.d(RECEIVER_TAG, "WakeLock acquired.  This lock won't time out!");
    		    		}
    		    	}
    			}
    			else
    			{
    		    	Log.d(RECEIVER_TAG, "ACTION_SCREEN_ON received but GLASSBRIGHT_ENABLED is false");    				
    			}
    			mSettings.close();
    		}
    		else
    		{
    			Log.d(RECEIVER_TAG, "Some other unexpected action: " + strIntentReceived);
    		}
    	}
    }
}
