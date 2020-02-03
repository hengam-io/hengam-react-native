
package io.hengam;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import io.hengam.lib.notification.NotificationData;
import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.annotation.Nullable;

import io.hengam.lib.analytics.HengamAnalytics;
import io.hengam.lib.notification.NotificationButtonData;
import io.hengam.lib.notification.NotificationData;
import io.hengam.lib.Hengam;
import io.hengam.lib.notification.HengamNotification;
import io.hengam.lib.notification.HengamNotificationListener;
import io.hengam.lib.notification.UserNotification;
import io.hengam.utils.RNHengamTypes.EVENTS_TYPES;
import io.hengam.utils.RNHengamTypes.SEND_NOTIFICATION_TYPE;



import static io.hengam.utils.RNHengamUtils.getNotificationIntent;
import static io.hengam.utils.RNHengamUtils.mapToBundle;
import static io.hengam.utils.RNHengamUtils.mapToWritableMap;
import static io.hengam.utils.RNHengamUtils.notificationDataToWritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;



public class RNHengam extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;

    /**
     * Check if App is on foreground or not base on
     * {@link com.facebook.react.bridge.LifecycleEventListener}
     * LifecycleEventListener should be added to the context
     */
    private boolean isAppOnForeground = false;


    HengamNotification hengamNotification;
    HengamAnalytics hengamAnalytics;

    public RNHengam(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addLifecycleEventListener(this);

        hengamNotification = (HengamNotification) Hengam.getHengamService(Hengam.NOTIFICATION);
        hengamAnalytics = (HengamAnalytics) Hengam.getHengamService(Hengam.ANALYTICS);

        // This calls to initializeNotificationCallbacks is used when app is in foreground
        this.initializeNotificationCallbacks();
    }

    @Override
    public String getName() {
        return "RNHengam";
    }


    /**
     * This is for initializing hengam event listeners when app is in background
     * and should be called in a file that extends {@link android.app.Application}
     * in react that would be {@link "com.my.package".MainApplication#onCreate}
     * <p>
     * This func calls initializeNotificationCallbacks with a different context
     * than the real context of UI thread, the it should not be used when app in
     * foreground.
     *
     * @param context
     */
    public static void initializeEventListeners(Context context) {
        new RNHengam(new ReactApplicationContext(context)).initializeNotificationCallbacks();
    }

    private void sendCallbackEvent(String eventName, Object params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Deprecated
    @ReactMethod
    public void initialize() {
        // Does not do anything
    }

    @ReactMethod
    public void isRegistered(final Promise promise) {
        Boolean registered = Hengam.isRegistered();
        promise.resolve(registered);
    }

    @ReactMethod
    public void isInitialized(final Promise promise) {
        Boolean initialized = Hengam.isInitialized();
        promise.resolve(initialized);
    }

    @ReactMethod
    public void onRegisterationComplete(final Promise promise) {
        Hengam.setRegistrationCompleteListener(new Hengam.Callback() {
            @Override
            public void onComplete() {
                promise.resolve(true);
            }
        });
    }

    @ReactMethod
    public void onInitializationComplete(final Promise promise) {
        Hengam.setInitializationCompleteListener(new Hengam.Callback() {
            @Override
            public void onComplete() {
                promise.resolve(true);
            }
        });
    }

    @ReactMethod
    public void subscribeToTopic(final String topic, final Promise promise) {
        Hengam.subscribeToTopic(topic, new Hengam.Callback() {
            @Override
            public void onComplete() {
                promise.resolve(true);
            }
        });
    }

    @ReactMethod
    public void unsubscribeFromTopic(final String topic, final Promise promise) {
        Hengam.unsubscribeFromTopic(topic, new Hengam.Callback() {
            @Override
            public void onComplete() {
                promise.resolve(true);
            }
        });
    }

    @ReactMethod
    public void getSubscribedTopics(final Promise promise) {
        List<String> topics = Hengam.getSubscribedTopics();
        WritableArray array = new WritableNativeArray();
        for (String item :
                topics) {
            array.pushString(item);
        }
        promise.resolve(array);
    }

    @ReactMethod
    public void addTags(final ReadableMap tags, final Promise promise) {

        Hengam.addTags((Map) tags.toHashMap());
        promise.resolve(true);
    }

    @ReactMethod
    public void removeTags(final ReadableArray list, final Promise promise) {
        Hengam.removeTags((List) list.toArrayList());
        promise.resolve(true);
    }

    @ReactMethod
    public void getSubscribedTags(final Promise promise) {
        try {
            WritableMap writableMap = new WritableNativeMap();
            Map<String, String> map = Hengam.getSubscribedTags();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writableMap.putString(entry.getKey(), entry.getValue());
            }

            promise.resolve(writableMap);
        } catch (Exception e) {
            promise.reject(e);
        }
        
    }

    @Deprecated
    @ReactMethod
    public void getHengamId(final Promise promise)
    {
        String hengamId = Hengam.getHengamId();
        promise.resolve(hengamId);
    }

    @ReactMethod
    public void getGoogleAdvertisingId(final Promise promise) {
        String googleAdvertisingId = Hengam.getGoogleAdvertisingId();
                
        if (googleAdvertisingId == null) {
            promise.reject(new Error("Could not get getGoogleAdvertisingId"));
        } else {
            promise.resolve(googleAdvertisingId);    
        }
    }

    @ReactMethod
    public void getAndroidId(final Promise promise) {
        String androidId = Hengam.getAndroidId();

        if (androidId == null) {
            promise.reject(new Error("Could not get androidId"));
        } else {
            promise.resolve(androidId);
        }
    }

    @ReactMethod
    public void setCustomId(@Nullable String id, final Promise promise) {
        Hengam.setCustomId(id);
        promise.resolve(true);
    }


    @ReactMethod
    public void getCustomId(final Promise promise) {
        String customId = Hengam.getCustomId();
        
        if (customId == null) {
            promise.reject(new Error("Getting Custom Id failed"));
        } else {
            promise.resolve(customId);    
        }
    }

    @ReactMethod
    public void setUserEmail(@Nullable String email, final Promise promise) {
        Boolean result = Hengam.setUserEmail(email);
        promise.resolve(result);
    }

    @ReactMethod
    public void getUserEmail(final Promise promise) {
        String email = Hengam.getUserEmail();
        
        if (email == null) {
            promise.reject(new Error("Getting user email failed"));
        } else {
            promise.resolve(email);
        }
    }

    @ReactMethod
    public void setUserPhoneNumber(@Nullable String phoneNumber, final Promise promise) {
        Boolean result = Hengam.setUserPhoneNumber(phoneNumber);
        promise.resolve(result);
    }

    @ReactMethod
    public void getUserPhoneNumber(final Promise promise) {
        String phoneNumber = Hengam.getUserPhoneNumber();

        if (phoneNumber == null) {
            promise.reject(new Error("Getting user phone number failed"));
        } else {
            promise.resolve(phoneNumber);
        }
    }


    @ReactMethod
    public void enableNotifications(final Promise promise) {
        hengamNotification.enableNotifications();
        promise.resolve(true);
    }

    @ReactMethod
    public void disableNotifications(final Promise promise) {
        hengamNotification.disableNotifications();
        promise.resolve(true);
    }

    @ReactMethod
    public void isNotificationEnable(final Promise promise) {
        Boolean isEnabled = hengamNotification.isNotificationEnable();
        promise.resolve(isEnabled);
    }

    @ReactMethod
    public void enableCustomSound(final Promise promise) {
        hengamNotification.enableCustomSound();
        promise.resolve(true);
    }

    @ReactMethod
    public void disableCustomSound(final Promise promise) {
        hengamNotification.disableCustomSound();
        promise.resolve(true);
    }

    @ReactMethod
    public void isCustomSoundEnable(final Promise promise) {
        Boolean isEnabled = hengamNotification.isCustomSoundEnable();
        promise.resolve(isEnabled);
    }


    @ReactMethod
    public void sendNotificationToUser(String type, String id, String params, final Promise promise) {
        UserNotification userNotification;

        try {
            if (SEND_NOTIFICATION_TYPE.CUSTOM_ID.is(type)) {
                userNotification = UserNotification.withCustomId(id);
            } else if (SEND_NOTIFICATION_TYPE.ANDROID_ID.is(type)) {
                userNotification = UserNotification.withAndroidId(id);
            } else if (SEND_NOTIFICATION_TYPE.ADVERTISEMENT_ID.is(type)) {
                userNotification = UserNotification.withAdvertisementId(id);
            } else {
                promise.reject(new Exception("Send notification type is not valid"));
                return;
            }

            userNotification.setAdvancedNotification(params);
            hengamNotification.sendNotificationToUser(userNotification);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void createNotificationChannel(String channel_id, String channelName, @Nullable String description,
                                          @Nullable int importance, @Nullable boolean enableLight, @Nullable boolean enableVibration,
                                          @Nullable boolean showBadge, @Nullable int ledColor, @Nullable ReadableArray vibrationPattern, final Promise promise) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                long[] vibrationPatternArray = new long[vibrationPattern.size()];
                for (int i = 0; i < vibrationPatternArray.length; i++) {
                    vibrationPatternArray[i] = (long) vibrationPattern.getDouble(i);
                }
                hengamNotification.createNotificationChannel(channel_id, channelName, description, importance, enableLight, enableVibration, showBadge, ledColor, vibrationPatternArray);
                promise.resolve(true);
            } catch (Exception e) {
                promise.reject(e);
            }
        } else {
            promise.reject(new Exception("Notification Channel is only supported in Api 26 or higher."));
        }
    }

    @ReactMethod
    public void removeNotificationChannel(String channel_id, final Promise promise) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                hengamNotification.removeNotificationChannel(channel_id);
                promise.resolve(true);
            } catch (Exception e) {
                promise.reject(e);
            }
        } else {
            promise.reject(new Exception("Notification Channel is only supported in Api 26 or higher."));
        }
    }

    @ReactMethod
    public void sendEcommerceData(String name,Double price,final Promise promise)
    {
        hengamAnalytics.sendEcommerceData(name,price);
        promise.resolve(true);
    }

    @ReactMethod
    public void sendEvent(String name, final Promise promise)
    {
        hengamAnalytics.sendEvent(name);
        promise.resolve(true);
    }

    private void startHeadlessJsTask(Intent intent, String eventType) {
        intent.putExtra("EVENT_TYPE", eventType);

        reactContext.startService(intent);
        HeadlessJsTaskService.acquireWakeLockNow(reactContext);
    }

    private void initializeNotificationCallbacks() {
        if (hengamNotification == null) {
            hengamNotification = (HengamNotification) Hengam.getHengamService(Hengam.NOTIFICATION);
        }

        hengamNotification.setNotificationListener(new HengamNotificationListener() {
            @Override
            public void onNotification(@NonNull NotificationData notificationData) {

                if (isAppOnForeground) {
                    sendCallbackEvent(EVENTS_TYPES.RECEIVED.getBroadcast(), notificationDataToWritableMap(notificationData));

                } else {
                    Intent intent = getNotificationIntent(reactContext, notificationData);
                    startHeadlessJsTask(intent, EVENTS_TYPES.RECEIVED.getEvent());
                }
            }

            @Override
            public void onCustomContentNotification(@NonNull Map<String, Object> map) {
                if (isAppOnForeground) {
                    sendCallbackEvent(EVENTS_TYPES.CUSTOM_CONTENT_RECEIVED.getBroadcast(), mapToWritableMap(map));
                } else {
                    Intent intent = new Intent(reactContext, RNHengamNotificationService.class);
                    intent.putExtra("customContent", mapToBundle(map));
                    startHeadlessJsTask(intent, EVENTS_TYPES.CUSTOM_CONTENT_RECEIVED.getEvent());
                }
            }

            @Override
            public void onNotificationClick(@NonNull NotificationData notificationData) {
                if (isAppOnForeground) {
                    sendCallbackEvent(EVENTS_TYPES.CLICKED.getBroadcast(), notificationDataToWritableMap(notificationData));
                } else {
                    Intent intent = getNotificationIntent(reactContext, notificationData);
                    startHeadlessJsTask(intent, EVENTS_TYPES.CLICKED.getEvent());
                }
            }

            @Override
            public void onNotificationDismiss(@NonNull NotificationData notificationData) {
                if (isAppOnForeground) {
                    sendCallbackEvent(EVENTS_TYPES.DISMISSED.getBroadcast(), notificationDataToWritableMap(notificationData));
                } else {
                    Intent intent = getNotificationIntent(reactContext, notificationData);
                    startHeadlessJsTask(intent, EVENTS_TYPES.DISMISSED.getEvent());
                }
            }

            @Override
            public void onNotificationButtonClick(@NonNull NotificationButtonData notificationButtonData, @NonNull NotificationData notificationData) {
                if (isAppOnForeground) {
                    sendCallbackEvent(EVENTS_TYPES.BUTTON_CLICKED.getBroadcast(), notificationDataToWritableMap(notificationData));
                } else {
                    Intent intent = getNotificationIntent(reactContext, notificationData);

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", notificationButtonData.getId());
                    map.put("icon", notificationButtonData.getIcon());
                    map.put("text", notificationButtonData.getText());
                    Bundle bundle = mapToBundle(map);
                    intent.putExtra("notificationButtonData", bundle);

                    startHeadlessJsTask(intent, EVENTS_TYPES.BUTTON_CLICKED.getEvent());
                }
            }
        });
    }

    @Override
    public void onHostResume() {
        isAppOnForeground = true;
    }

    @Override
    public void onHostPause() {
        isAppOnForeground = false;
    }

    @Override
    public void onHostDestroy() {
        isAppOnForeground = false;
    }

}
