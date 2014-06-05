package com.app.gptalk.push_notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.app.gptalk.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {

    private NotificationManager notificationManager;

    private final static int NOTIFICATION_ID = 1;

    public GCMIntentService() {

        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);

        // The intent parameter must be the intent received in GCMBroadcastReceiver.
        String message = googleCloudMessaging.getMessageType(intent);

        // Filter messages based on message type.
        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(message)) {
                sendNotification("Request error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(message)) {
                sendNotification("Deleted request on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(message)) {
                // If regular GCM message, send notification to GP.
                sendNotification(extras.getString("Notice"));
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put message into a notification and post it.
    private void sendNotification(String message) {

        notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, IncomingBookingRequest.class);
        notificationIntent.putExtra("appointmentReference", message);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                          .setContentTitle("GP Consultation Notification")
                          .setSmallIcon(R.drawable.ic_launcher)
                          .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification_sound))
                          .setDefaults(Notification.DEFAULT_VIBRATE)
                          .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                          .setContentIntent(contentIntent)
                          .setAutoCancel(true)
                          .setContentText(message);

        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
