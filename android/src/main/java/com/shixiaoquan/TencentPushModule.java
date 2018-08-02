package com.shixiaoquan;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.encrypt.Rijndael;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by rain on 2018/3/26.
 */

public class TencentPushModule extends ReactContextBaseJavaModule {

    public static final String MODULE_NAME = "TencentPushModule";
    private static String TAG = "TencentPushModule";

    private Context reactContext;
    private int badge = 0;

    private Context mContext;
    private static String mEvent;
    private static Intent mCachedBundle;
    private static ReactApplicationContext mRAC;

    private static HashMap<Integer, Callback> sCacheMap = new HashMap<Integer, Callback>();


    TencentPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        registerReceivers();
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public boolean canOverrideExistingModule() {
        return true;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        mCachedBundle = null;
        if (null != sCacheMap) {
            sCacheMap.clear();
        }
    }

    @ReactMethod
    public void notifyJSDidLoad(Callback callback) {
        // send cached event
        if (getReactApplicationContext().hasActiveCatalystInstance()) {
            mRAC = getReactApplicationContext();
            LocalNotificationCache.getInstance().setStopCache(true);
            sendEvent();
            callback.invoke(0);
        }
    }

    /*********************************************************************************
     * XGPushManager功能类
     * 方法默认为public static类型
     *********************************************************************************/

    /**
     * 启动并注册（无注册回调）
     *
     */
    @ReactMethod
    public void registerPush() {
        XGPushManager.registerPush(this.reactContext);
    }

    /**
     * 启动并注册（有注册回调）
     *
     * @param cb
     */
    @ReactMethod
    public void registerPush(final Callback cb) {
        XGPushManager.registerPush(this.reactContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object date, int flag) {
                cb.invoke(0, date);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 启动并注册APP，同时绑定账号,
     * 推荐有帐号体系的APP使用
     * （3.2.2不包括3.2.2之前的版本使用，有注册回调）
     *
     * @param account
     * @param cb
     */
    @ReactMethod
    public void registerPush(String account, final Callback cb) {
        XGPushManager.registerPush(this.reactContext, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object date, int flag) {
                cb.invoke(0, date);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 启动并注册APP，同时绑定账号,
     * 推荐有帐号体系的APP使用
     * （3.2.2不包括3.2.2之前的版本使用，有注册回调）
     * 仅供带登陆态的业务使用
     *
     * @param account
     * @param cb
     */
    @ReactMethod
    public void registerPush(String account, String ticket, int ticketType, String qua, final Callback cb) {
        XGPushManager.registerPush(this.reactContext, account, ticket, ticketType, qua, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object date, int flag) {
                cb.invoke(0, date);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 启动并注册APP，同时绑定账号,
     * 推荐有帐号体系的APP使用
     * （3.2.2以及3.2.2之后的版本使用，
     * 此接口会覆盖设备之前绑定过的账号，仅当前注册的账号生效）,
     * 无注册回调
     *
     * @param account
     */
    @ReactMethod
    public void bindAccount(String account) {
        XGPushManager.bindAccount(this.reactContext, account);
    }

    /**
     * 启动并注册APP，同时绑定账号,
     * 推荐有帐号体系的APP使用
     * （3.2.2以及3.2.2之后的版本使用，
     * 此接口会覆盖设备之前绑定过的账号，仅当前注册的账号生效）,
     * 有注册回调
     *
     * @param account
     * @param cb
     */
    @ReactMethod
    public void bindAccount(String account, final Callback cb) {
        final WritableMap map = Arguments.createMap();
        XGPushManager.bindAccount(this.reactContext, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object date, int flag) {
                map.putInt("code", 0);
                map.putString("res", date.toString());
                cb.invoke(map);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 启动并注册APP，同时绑定账号,
     * 推荐有帐号体系的APP使用
     * （3.2.2以及3.2.2之后的版本使用，
     * 此接口保留之前的账号，只做增加操作，
     * 一个token下最多只能有3个账号超过限制会自动顶掉之前绑定的账号，无注册回调）
     *
     * @param account
     */
    @ReactMethod
    public void appendAccount(String account) {
        XGPushManager.appendAccount(this.reactContext, account);
    }


    /**
     * 启动并注册APP，同时绑定账号,
     * 推荐有帐号体系的APP使用
     * （3.2.2以及3.2.2之后的版本使用，
     * 此接口保留之前的账号，只做增加操作，
     * 一个token下最多只能有3个账号超过限制会自动顶掉之前绑定的账号，有注册回调）
     *
     * @param account
     * @param cb
     */
    @ReactMethod
    public void appendAccount(String account, final Callback cb) {
        XGPushManager.appendAccount(this.reactContext, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object date, int flag) {
                cb.invoke(0, date);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 解绑指定账号（3.2.2以及3.2.2之后的版本使用，有注册回调）
     *
     * @param account
     */
    @ReactMethod
    public void delAccount(String account) {
        XGPushManager.delAccount(this.reactContext, account);
    }

    /**
     * 解绑指定账号（3.2.2以及3.2.2之后的版本使用，有注册回调）
     *
     * @param account
     * @param cb
     */
    @ReactMethod
    public void delAccount(String account, final Callback cb) {
        XGPushManager.delAccount(this.reactContext, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object date, int flag) {
                cb.invoke(0, date);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 反注册，建议在不需要接收推送的时候调用
     *
     * @param cb
     */
    @ReactMethod
    public void unregisterPush(final Callback cb) {
        XGPushManager.unregisterPush(this.reactContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                WritableMap map = Arguments.createMap();
                map.putString("data", (String) data);
                map.putInt("flag", flag);
                cb.invoke(0, map);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                cb.invoke(String.valueOf(errCode), msg);
            }
        });
    }

    /**
     * 设置标签
     *
     * @param tagName
     */
    @ReactMethod
    public void setTag(String tagName) {
        XGPushManager.setTag(this.reactContext, tagName);
    }

    /**
     * 删除标签
     *
     * @param tagName
     */
    @ReactMethod
    public void deleteTag(String tagName) {
        XGPushManager.deleteTag(this.reactContext, tagName);
    }

    /**
     * 本地通知
     *
     * @param title
     * @param content
     */
    @ReactMethod
    public void addLocalNotification(String title, String content) {
        XGLocalMessage message = new XGLocalMessage();
        message.setTitle(title);
        message.setContent(content);
        Logger.i(TAG, title);
        Logger.i(TAG, content);
        XGPushManager.addLocalNotification(this.reactContext, message);
    }

    /**
     * 检测通知栏是否关闭
     *
     * @param cb
     */
    @ReactMethod
    public void isNotificationOpened(Callback cb) {
        cb.invoke(0, XGPushManager.isNotificationOpened(this.reactContext));
    }

    /**
     * 清除通知栏所有通知
     */
    @ReactMethod
    public void cancelAllNotifaction() {
        XGPushManager.cancelAllNotifaction(this.reactContext);
    }

    /**
     * 清除单个通知
     *
     * @param noticeId
     */
    @ReactMethod
    public void cancelNotifaction(int noticeId) {
        XGPushManager.cancelNotifaction(this.reactContext, noticeId);
    }

    /**
     * 清除本地通知
     */
    @ReactMethod
    public void clearLocalNotifications() {
        XGPushManager.clearLocalNotifications(this.reactContext);
    }

    /*****************************************************************
     *                         XGPushConfig配置类
     * 方法默认为public static类型,
     * 对于本类提供的set和enable方法，要在XGPushManager接口前调用才能及时生效
     *****************************************************************/

    /**
     * 初始化
     *
     * @param accessId
     * @param accessKey
     */
    @ReactMethod
    public void init(int accessId, String accessKey) {
        XGPushConfig.setAccessId(this.reactContext, accessId);
        XGPushConfig.setAccessKey(this.reactContext, accessKey);
    }

    /**
     * 是否开启debug模式，即输出logcat日志重要：为保证数据的安全性，发布前必须设置为false）
     */
    @ReactMethod
    public void enableDebug(boolean isDebug) {
        XGPushConfig.enableDebug(this.reactContext, isDebug);
    }

    /**
     * 开启logcat输出，方便debug，发布时请关闭
     */
    @ReactMethod
    public void isEnableDebug(Callback cb) {
        cb.invoke(0, XGPushConfig.isEnableDebug(this.reactContext));
    }

    /**
     * 配置accessId
     *
     * @param accessId
     */
    @ReactMethod
    public void setAccessId(String accessId) {
        try {
            XGPushConfig.setAccessId(this.reactContext, Long.parseLong(accessId));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 获取accessId
     *
     * @return
     */
    @ReactMethod
    public long getAccessId() {
        return XGPushConfig.getAccessId(this.reactContext);
    }

    /**
     * 配置accessKey
     *
     * @param accessKey
     */
    @ReactMethod
    public void setAccessKey(String accessKey) {
        XGPushConfig.setAccessKey(this.reactContext, accessKey);
    }

    /**
     * 获取accessKey
     *
     */
    @ReactMethod
    public void getAccessKey() {
        XGPushConfig.getAccessKey(this.reactContext);
    }

    /**
     * 获取设备的token，只有注册成功才能获取到正常的结果
     *
     * @param cb
     */
    @ReactMethod
    public void getToken(Callback cb) {
        cb.invoke(XGPushConfig.getToken(this.reactContext));
    }

    /**
     * 设置上报通知栏是否关闭 默认打开
     *
     * @param debugMode
     */
    @ReactMethod
    public void setReportNotificationStatusEnable(boolean debugMode) {
        XGPushConfig.setReportNotificationStatusEnable(this.reactContext, debugMode);
    }

    /**
     * 设置上报APP 列表，用于智能推送 默认打开
     *
     * @param debugMode
     */
    @ReactMethod
    public void setReportApplistEnable(boolean debugMode) {
        XGPushConfig.setReportApplistEnable(this.reactContext, debugMode);
    }

    /**
     * 设置支持第三方厂商推送
     * 需要在 registerPush 之前调用
     *
     * @param isEnable
     */
    @ReactMethod
    public void enableOtherPush(boolean isEnable) {
        XGPushConfig.enableOtherPush(this.reactContext, isEnable);
    }

    /**
     * 设置小米推送APPID
     *
     * @param appid
     */
    @ReactMethod
    public void setMiPushAppId(String  appid) {
        XGPushConfig.setMiPushAppId(this.reactContext, appid);
    }

    /**
     * 设置小米推送APPKEY
     *
     * @param appkey
     */
    @ReactMethod
    public void setMiPushAppKey(String  appkey) {
        XGPushConfig.setMiPushAppKey(this.reactContext, appkey);
    }

    /**
     * 初始化小米
     * @param appId
     * @param appKey
     */
    @ReactMethod
    public void initXiaomi(String appId, String appKey) {
        XGPushConfig.setMiPushAppId(this.reactContext, appId);
        XGPushConfig.setMiPushAppKey(this.reactContext, appKey);
    }

    /**
     * 设置魅族推送APPID
     *
     * @param appid
     */
    @ReactMethod
    public void setMzPushAppId(String  appid) {
        XGPushConfig.setMiPushAppId(this.reactContext, appid);
    }

    /**
     * 设置魅族推送APPKEY
     *
     * @param appkey
     */
    @ReactMethod
    public void setMzPushAppKey(String  appkey) {
        XGPushConfig.setMiPushAppKey(this.reactContext, appkey);
    }

    /**
     * 初始化魅族
     *
     * @param appId
     * @param appKey
     */
    @ReactMethod
    public void initMeizu(String appId, String appKey) {
        //设置魅族APPID和APPKEY
        XGPushConfig.setMzPushAppId(this.reactContext, appId);
        XGPushConfig.setMzPushAppKey(this.reactContext, appKey);
    }

    /**
     * 华为手机的写日志定位问题
     *
     * @param isDebug
     */
    @ReactMethod
    public void setHuaweiDebug(boolean isDebug) {
        XGPushConfig.setHuaweiDebug(isDebug);
    }

    @ReactMethod
    public void getInitialNotification(Callback cb) {
        WritableMap params = Arguments.createMap();
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Intent intent = activity.getIntent();
            try {
                if (intent != null && intent.hasExtra("protect")) {
                    String title = Rijndael.decrypt(intent.getStringExtra("title"));
                    String content = Rijndael.decrypt(intent.getStringExtra("content"));
                    String customContent = Rijndael.decrypt(intent.getStringExtra("custom_content"));
                    params.putString("title", title);
                    params.putString("content", content);
                    params.putString("custom_content", customContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cb.invoke(0, params);
    }

    /**
     * 获取应用图标的 Badge 数字
     *
     * @param callback
     */
    @ReactMethod
    public void getApplicationIconBadgeNumber(Callback callback) {
        callback.invoke(this.badge);
    }

    /**
     * 设置应用图标的 Badge 数字
     *
     * @param number
     */
    @ReactMethod
    public void setApplicationIconBadgeNumber(int number) {
        this.badge = number;
        ShortcutBadger.applyCount(this.reactContext, number);
    }

    private void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_ON_REGISTERED);
        intentFilter.addAction(Constants.ACTION_ON_TEXT_MESSAGE);
        intentFilter.addAction(Constants.ACTION_ON_NOTIFICATION_CLICKED);
        intentFilter.addAction(Constants.ACTION_ON_NOTIFICATION_SHOWED);
        reactContext.registerReceiver(new XGPushReceiver(), intentFilter);
    }

    private static void sendEvent() {
        if (mRAC != null) {
            if (mEvent != null) {
                Logger.i(TAG, "Sending event : " + mEvent);
                WritableMap map = Arguments.createMap();
                switch (mEvent) {
                    case Constants.EVENT_MESSAGE_RECEIVED:
                        String title = mCachedBundle.getStringExtra("title");
                        String content = mCachedBundle.getStringExtra("content");
                        String customContent = mCachedBundle.getStringExtra("custom_content");
                        map.putString("title", title);
                        map.putString("content", content);
                        map.putString("custom_content", customContent);
                        mRAC.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(mEvent, map);
                        break;
                    case Constants.EVENT_REGISTERED_ID:
                        String token = mCachedBundle.getStringExtra("token");
                        mRAC.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(mEvent, token);
                        break;
                    case Constants.EVENT_REMOTE_NOTIFICATION_RECEIVED:
                    case Constants.EVENT_OPEN_NOTIFICATION:
                        map = Arguments.createMap();
                        map.putString("title", mCachedBundle.getStringExtra("title"));
                        map.putString("content", mCachedBundle.getStringExtra("content"));
                        map.putString("custom_content", mCachedBundle.getStringExtra("custom_content"));
                        mRAC.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(mEvent, map);
                        LocalNotificationCache.getInstance().checkNotificationIdAndRemove(mCachedBundle.getLongExtra("msgId", -123456));
                        break;
                }
                mEvent = null;
                mCachedBundle = null;
            }
            if (!LocalNotificationCache.getInstance().isCacheEmpty()) {
                WritableMap mapLocal = Arguments.createMap();
                Intent localIntent = (Intent) LocalNotificationCache.getInstance().popNotification();
                Logger.i(TAG, "Sending event : " + Constants.EVENT_OPEN_NOTIFICATION + "  " + localIntent.toString());
                mapLocal = Arguments.createMap();
                mapLocal.putString("title", localIntent.getStringExtra("title"));
                mapLocal.putString("content", localIntent.getStringExtra("content"));
                mapLocal.putString("custom_content", localIntent.getStringExtra("custom_content"));
                mRAC.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(Constants.EVENT_OPEN_NOTIFICATION, mapLocal);
            }
        }
    }

    public static class XGPushReceiver extends BroadcastReceiver {

        public XGPushReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                WritableMap params = Arguments.createMap();
                mCachedBundle = intent;
                Logger.d(TAG, intent.toString());
                switch (intent.getAction()) {
                    case Constants.ACTION_ON_REGISTERED:
                        mEvent = Constants.EVENT_REGISTERED_ID;
                        intent.getBundleExtra("notification");
                        String token = intent.getStringExtra("token");
                        Logger.i(TAG, "注册token: " + token);
                        params.putString("deviceToken", token);
                        sendEvent();
                        break;
                    case Constants.ACTION_ON_TEXT_MESSAGE:
                        mEvent = Constants.EVENT_MESSAGE_RECEIVED;
                        String title = intent.getStringExtra("title");
                        String content = intent.getStringExtra("content");
                        String customContent = intent.getStringExtra("custom_content");

                        Logger.i(TAG, "收到自定义消息: title:" + title + " content:" + content + " custom_content:" + customContent);

                        params.putString("title", title);
                        params.putString("content", content);
                        params.putString("custom_content", customContent);
                        sendEvent();
                        break;
                    case Constants.ACTION_ON_NOTIFICATION_SHOWED:
                        mEvent = Constants.EVENT_REMOTE_NOTIFICATION_RECEIVED;
                        Logger.i(TAG, "收到推送下来的通知: title:" + intent.getStringExtra("title") + " content" + intent.getStringExtra("content"));
                        Logger.i(TAG, "收到推送下来的通知: custom_content: " + intent.getStringExtra("custom_content"));
                        sendEvent();
                        break;
                    case Constants.ACTION_ON_NOTIFICATION_CLICKED:
                        mEvent = Constants.EVENT_OPEN_NOTIFICATION;
                        if (isApplicationRunningBackground(context)) {
                            intent = new Intent();
                            intent.setClassName(context.getPackageName(), context.getPackageName() + ".MainActivity");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        } else {
                            intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        Logger.d(TAG, "用户点击打开了通知");
                        Logger.i(TAG, "用户点击打开了通知: title:" + intent.getStringExtra("title") + " content" + intent.getStringExtra("content"));
                        Logger.i(TAG, "用户点击打开了通知: custom_content: " + intent.getStringExtra("custom_content"));
                        sendEvent();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean isApplicationRunningBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
