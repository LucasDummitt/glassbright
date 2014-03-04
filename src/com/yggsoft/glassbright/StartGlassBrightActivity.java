// Copyright 2014 Lucas Dummitt https://plus.google.com/+LucasDummitt

package com.yggsoft.glassbright;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartGlassBrightActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, GlassBrightService.class));
        finish();
    }
}