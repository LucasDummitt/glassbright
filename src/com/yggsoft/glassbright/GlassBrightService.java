// Copyright 2014 Lucas Dummitt https://plus.google.com/+LucasDummitt 

package com.yggsoft.glassbright;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

public class GlassBrightService extends Service {
	private static final String SERVICE_TAG = "GBService";
    private static final String LIVE_CARD_TAG = "glassbright";

    private static int BROADCAST_REGISTERED = 0;
    private static BroadcastReceiver receiver;
    private TimelineManager mTimelineManager;
    private LiveCard mLiveCard;
    private RemoteViews liveCardView;

    @Override
    public void onCreate() {
        super.onCreate();
        GlassBrightTools.CreateGlassBrightSettings(getApplicationContext());
        mTimelineManager = TimelineManager.from(this);
    }

    public void UpdateTimeoutText(String strText)
    {
    	if(strText.equals("-1"))
    	{
            liveCardView.setTextViewText(R.id.timeoutText, "Screen Timeout: never");
    	}
    	else
    	{
            liveCardView.setTextViewText(R.id.timeoutText, "Screen Timeout: " + strText);    		
    	}
        mLiveCard.setViews(liveCardView);        		    	
    }
    
    public void UpdateAutoText(String strText)
    {
    	if(strText.equals(GlassBrightTools.SETTING_DISABLED_VALUE))
    	{
            liveCardView.setTextViewText(R.id.autoText, "Brightness: Full");
    	}
    	else
    	{
            liveCardView.setTextViewText(R.id.autoText, "Brightness: Auto");    		
    	}
        mLiveCard.setViews(liveCardView);        		    	
    }    
    
    public class LocalBinder extends Binder {
        GlassBrightService getService() {
            return GlassBrightService.this;
        }
    }    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(SERVICE_TAG, "Entering onStartCommand");
    	Context currentContext = this.getApplicationContext();

    	if(BROADCAST_REGISTERED == 0)
    	{
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.SCREEN_ON");

            receiver = new GlassBrightReceiver();
            registerReceiver(receiver, filter);  
            BROADCAST_REGISTERED = 1;
    	}
    	    	
        if (mLiveCard == null) {
        	Log.d(SERVICE_TAG, "Card didn't exist, enabling GlassBright and making one.");
        	
        	// Turn on the GlassBright setting
        	GlassBrightTools.EnableGlassBright(currentContext);
        	
            mLiveCard = mTimelineManager.createLiveCard(LIVE_CARD_TAG);
            Intent menuIntent = new Intent(this, GlassBrightMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            liveCardView = new RemoteViews(this.getPackageName(), R.layout.glassbright_live_card);
            if(GlassBrightTools.GetAuto(currentContext).equals(GlassBrightTools.SETTING_DISABLED_VALUE))
            {
            	liveCardView.setTextViewText(R.id.autoText, "Brightness: Full");
            }
            else
            {
            	liveCardView.setTextViewText(R.id.autoText, "Brightness: Auto");            	
            }
            liveCardView.setTextViewText(R.id.timeoutText, "Screen Timeout: " + GlassBrightTools.GetTimeout(currentContext));
            mLiveCard.setViews(liveCardView);
            
            mLiveCard.publish(LiveCard.PublishMode.REVEAL);
        }
        else
        {
        	// Already exists, so just perform a toggle
        	if(GlassBrightTools.ToggleGlassBright(currentContext))
        	{
                liveCardView.setTextViewText(R.id.enabledText, "GlassBright On");
                mLiveCard.setViews(liveCardView);
        	}
        	else
        	{
                liveCardView.setTextViewText(R.id.enabledText, "GlassBright Off");
                mLiveCard.setViews(liveCardView);        		
        	}
        }
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
    	if(BROADCAST_REGISTERED == 1)
    	{
    		BROADCAST_REGISTERED = 0;
    		unregisterReceiver(receiver);
    	}
        if (mLiveCard != null && mLiveCard.isPublished()) {
        	GlassBrightTools.DisableGlassBright(getApplicationContext());
            mLiveCard.unpublish();
            mLiveCard = null;
        }    	
        super.onDestroy();
    }
}