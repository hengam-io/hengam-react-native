package io.hengam.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.WritableMap;

import java.util.Map;

import io.hengam.lib.notification.NotificationData;

public class RNHengamUtils {

    public static Intent getNotificationIntent(Context context, NotificationData notificationData) {
        return new RNHengamIntent().getNotificationIntent(context, notificationData);
    }

    public static Bundle mapToBundle(Map<String, Object> map) {
        return new RNHengamIntent().mapToBundle(map);
    }

    public static WritableMap notificationDataToWritableMap(NotificationData notificationData) {
        return new RNHengamWritable().notificationDataToWritableMap(notificationData);
    }

    public static WritableMap mapToWritableMap(Map<String, Object> map) {
        return new RNHengamWritable().mapToWritableMap(map);
    }
}
