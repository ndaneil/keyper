package hu.awesometeam.keyper;


import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Objects;

public class NotificationReader extends NotificationListenerService {
    private static final boolean ignoreMedia = true;
    public NotificationReader() {
        super();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if(Objects.equals(sbn.getNotification().category, "msg")) {
            Bundle extras = sbn.getNotification().extras;
            String s = extras.get(Notification.EXTRA_TEXT) == null ? null : extras.get(Notification.EXTRA_TEXT).toString();
            Log.d("NotificationReceiver", "Received: " + sbn.getNotification().category + " " + s);

        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){

    }
}