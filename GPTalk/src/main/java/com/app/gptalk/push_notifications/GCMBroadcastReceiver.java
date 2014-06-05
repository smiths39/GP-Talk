package com.app.gptalk.push_notifications;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.app.gptalk.push_notifications.GCMIntentService;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that GcmIntentService will handle the intent
        ComponentName componentName = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
        // Start the service, keeping device awake while service is launching
        startWakefulService(context, (intent.setComponent(componentName)));
        setResultCode(Activity.RESULT_OK);
    }
}
