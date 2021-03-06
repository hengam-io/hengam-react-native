import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const { RNHengam } = NativeModules;


 const hengamEventEmitter = new NativeEventEmitter();

 const EVENTS_TYPES = ["received", "clicked", "dismissed", "button_clicked", "custom_content_received"]

 // key = events that user can attach handlers on them
 // value = broadcast events that are emitted from the native
 // and are corrospond to the ones in (co.ronash.hengam.utils)
 const _hengamEvents = new Map([
     [EVENTS_TYPES[0], "Hengam-NotificationReceived"],
     [EVENTS_TYPES[1], "Hengam-Clicked"],
     [EVENTS_TYPES[2], "Hengam-Dismissed"],
     [EVENTS_TYPES[3], "Hengam-ButtonClicked"],
     [EVENTS_TYPES[4], "Hengam-CustomContentReceived"],
 ]);

 // store all broadcastListeners (actually their returned subscriptions) and their handlers in this object
 const _broadcastListeners = {};

 const _cachedNotification = new Map();
 const _userEventHandlers = new Map();

 function _attachEventBroadcasts(event, nativeBroadcastEvent) {
     return hengamEventEmitter.addListener(nativeBroadcastEvent, (notification) => {
         let userEventHandler = _userEventHandlers.get(event);

         // Check if user already set a handler
         // for this event type then call it
         // if not cache notification for later
         if (userEventHandler) {
             userEventHandler(notification);
         } else {
             _cachedNotification.set(event, notification);
         }
     });
 }

 // Start point for attaching nativeBrodcast events
 if (RNHengam !== null) {
     _hengamEvents.forEach(function(nativeBroadcastEvent, event) {
         _broadcastListeners[event] = _attachEventBroadcasts(event, nativeBroadcastEvent);
     });
 }


 class Hengam {

      /**
      * Available events type to add listener on them
      */
     static EVENTS = {
         RECEIVED: EVENTS_TYPES[0],
         CLICKED: EVENTS_TYPES[1],
         DISMISSED: EVENTS_TYPES[2],
         BUTTON_CLICKED: EVENTS_TYPES[3],
         CUSTOM_CONTENT_RECEIVED: EVENTS_TYPES[4],
     }

     static ANDROID_ID_TYPES = {
         CUSTOM_ID: 'CUSTOM_ID',
         ANDROID_ID: 'ANDROID_ID',
         ADVERTISEMENT_ID: 'ADVERTISEMENT_ID'
     }

     static addEventListener(eventType, eventHandler) {
         if (!eventHandler) return;

         // save user eventHandler
         _userEventHandlers.set(eventType, eventHandler);

         // If already we have a cached notification for this eventType
         // call userEventHandler with this cached notification
         const cachedNotification = _cachedNotification.get(eventType);
         if (cachedNotification) {
             eventHandler(cachedNotification);
             _cachedNotification.delete(eventType);
         }
     }

     static removeEventListener(eventType) {
         _userEventHandlers.delete(eventType);
     }

     static clearListeners() {
         _hengamEvents.forEach((_value, key) => {
             hengamEventEmitter.removeAllListeners(_broadcastListeners[key]);
             _broadcastListeners.delete(key);
         });
     }

     static start(appId) {
         if (!isInitilized()) return;
         if (Platform.OS === 'android') return;

         RNHengam.start(appId)
     }

     /**
      * Check if Hengam is initialized or not
      *
      * it will return promise of type boolean
      *
      * @return {Promise<boolean>} Promise - if no parameter passed
      */
     static isInitialized() {
         if (Platform.OS === 'ios') return;
         return RNHengam.isInitialized();
     }

     /**
      * Check if Hengam is registered or not
      *
      * it will return promise of type boolean
      *
      * @return {Promise<boolean>} Promise - if no parameter passed
      */
     static isRegistered() {
         if (Platform.OS === 'ios') return;
         return RNHengam.isRegistered();
     }
     /**
      * it will called when push registertion is completed
      */
     static onHengamRegisterationComplete() {
         if (Platform.OS === 'ios') return;
         return RNHengam.onRegisterationComplete();
     }
      /**
      * it will called when push initialization is completed
      */
     static onHengamInitializationComplete() {
         if (Platform.OS === 'ios') return;
         return RNHengam.onInitializationComplete();
     }

     /**
      * get user's hengam_id
      *
      * it will return a promise.
      *
      * @return {Promise<string>} Promise - if no callback passed
      */
     static getHengamId() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getHengamId();
     }

     /**
      * get advertisingId
      * it will return a promise
      */
     static getGoogleAdvertisingId() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getGoogleAdvertisingId();
     }
     /**
      * get androidId
      * it will return a promise
      */
     static getAndroidId() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getAndroidId();
     }
     /**
      * set custom id
      * @param {string} id
      * @returns promise
      */
     static setCustomId(id) {
         if (Platform.OS === 'ios') return;
         return RNHengam.setCustomId(id);
     }
     /**
      * get custom id
      */
     static getCustomId() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getCustomId();
     }

     /**
      * set user email
      * @param {String} email
      */
     static setUserEmail(email) {
         if (Platform.OS === 'ios') return;
         return RNHengam.setUserEmail(email);
     }
     /**
      * get user email
      */
     static getUserEmail() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getUserEmail();
     }
     /**
      * set user phone number
      * @param {String} phone
      */
     static setUserPhoneNumber(phone) {
         if (Platform.OS === 'ios') return;
         return RNHengam.setUserPhoneNumber(phone);
     }

     /**
      * get user phone number
      */
     static getUserPhoneNumber() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getUserPhoneNumber();
     }

     /**
      * Subscribe a topic
      *
      * @param {string} topicName
      * @return void
      */
     static subscribeToTopic(topicName) {
         if (Platform.OS === 'ios') {
             return RNHengam.subscribe(topicName);
         } else {
             return RNHengam.subscribeToTopic(topicName);
         }
     }

     /**
      * Unsubscribe from a topic
      *
      * @param {string} topicName
      * @return void
      */
     static unsubscribeFromTopic(topicName) {
         if (Platform.OS === 'ios') {
             RNHengam.unsubscribe(topic)
         } else {
             return RNHengam.unsubscribeFromTopic(topicName);
         }
     }

     /**
      * get subscribed topics
      */
     static getSubscribedTopics() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getSubscribedTopics();
     }

     /**
      *
      * @param {object} tags - Object of key: string, value: string
      */
     static addTags(tags) {
         if (Platform.OS === 'ios') return;
         return RNHengam.addTags(tags);
     }

     /**
      *
      * @param {list} list - a list of strings
      */
     static removeTags(list) {
         if (Platform.OS === 'ios') return;
         return RNHengam.removeTags(list);
     }


     static getSubscribedTags() {
         if (Platform.OS === 'ios') return;
         return RNHengam.getSubscribedTags();
     }

     /**
      * Disable notification
      *
      */
     static disableNotifications() {
         if (Platform.OS === 'ios') return;
         return RNHengam.disableNotifications();
     }

     /**
      * Enable notification
      *
      */
     static enableNotifications() {
         if (Platform.OS === 'ios') return;
         return RNHengam.enableNotifications();
     }
     /**
      * Check weather notification is disabled or not
      */
     static isNotificationEnable() {
         if (Platform.OS === 'ios') return;
         return RNHengam.isNotificationEnable();
     }

     /**
      * enable custom sound
      */
     static enableCustomSound() {
         if (Platform.OS === 'ios') return;
         return RNHengam.enableCustomSound();
     }

     /**
      * disble custom sound
      */
     static disableCustomSound() {
         if (Platform.OS === 'ios') return;
         return RNHengam.disableCustomSound();
     }

     /**
      * Check weather custom sound is disbled or not
      */
     static isCustomSoundEnable() {
         if (Platform.OS === 'ios') return;
         return RNHengam.isCustomSoundEnable();
     }

     /**
      * Send notification to another device
      */
     static sendNotificationToUser({type, userId, ...otherParams}) {
         if (Platform.OS === 'ios') return;
         if (!type || !userId) {
             return Promise.reject("Must specify `type` & `userId`");
         }
         if (!Hengam.ANDROID_ID_TYPES[type]) {
             return Promise.reject("Provide valid type from `Hengam.ANDROID_ID_TYPES`");
         }

         return RNHengam.sendNotificationToUser(type, userId, JSON.stringify(otherParams));
     }

     /**
      * Create a notification channel (only Android 8.0+)
      *
      * @param {string} channelId
      * @param {string} channelName
      * @param {string} description
      * @param {number<int>} importance
      * @param {boolean} enableLight
      * @param {boolean} enableVibration
      * @param {bollean} showBadge
      * @param {number<int>} ledColor
      * @param {array} vibrationPattern
      * @return void
      */
     static createNotificationChannel(...params) {
         if (Platform.OS === 'ios') return;
         return RNHengam.createNotificationChannel(...params);
     }

     /**
      * Remove notification channel with channelId
      *
      * @param {string} channelId
      */
     static removeNotificationChannel(channelId) {
         if (Platform.OS === 'ios') return;
         return RNHengam.removeNotificationChannel(channelId);
     }

     static sendEcommerceData(name,price) {
         if (Platform.OS === 'ios') return;
         return RNHengam.sendEcommerceData(name,price);
     }

     static sendEvent(name) {
         if (Platform.OS === 'ios') return;
         return RNHengam.sendEvent(name);
     }

 }

 export default Hengam;
