// 'android/settings.gradle': {
module.exports = {
  //   pattern: `rootProject.name.*`,
  //   patch: `
  //     include ':react-native-tencent-push'
  //     project(':react-native-tencent-push').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-tencent-push/android')
  //   `
  // },

  'android/app/build.gradle': [
    // {
    //   pattern: 'dependencies {',
    //   patch: `
    //     compile project(':react-native-tencent-push')
    //   `
    // },
    // {
    //   pattern: `versionName .*`,
    //   patch: `
    //     manifestPlaceholders = [
    //       XG_ACCESS_ID:"注册应用的accessid",
    //       XG_ACCESS_KEY : "注册应用的accesskey",
    //     ]
    //   `
    // }
  ],

  // 'ios/**/AppDelegate.m': [
  //   {
  //     pattern: `#import "AppDelegate.h"`,
  //     patch: `
  //       #import <RCTJPushModule.h>
  //       #ifdef NSFoundationVersionNumber_iOS_9_x_Max
  //       #import <UserNotifications/UserNotifications.h>
  //       #endif
  //     `
  //   },
  //   {
  //     pattern: `didFinishLaunchingWithOptions[^{]*{`,
  //     patch: `
  //       [JPUSHService setupWithOption:launchOptions appKey:@"${ctx.appKey}"
  //                             channel:nil apsForProduction:nil];
  //     `
  //   },
  //   {
  //     pattern: `@implementation AppDelegate`,
  //     patch: `
  //       - (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
  //       {
  //         [JPUSHService registerDeviceToken:deviceToken];
  //       }
  //       - (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
  //       {
  //         [[NSNotificationCenter defaultCenter] postNotificationName:kJPFDidReceiveRemoteNotification object:userInfo];
  //       }
  //       - (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
  //       {
  //         [[NSNotificationCenter defaultCenter] postNotificationName:kJPFDidReceiveRemoteNotification object: notification.userInfo];
  //       }
  //       - (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)   (UIBackgroundFetchResult))completionHandler
  //       {
  //         [[NSNotificationCenter defaultCenter] postNotificationName:kJPFDidReceiveRemoteNotification object:userInfo];
  //       }
  //       - (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler
  //       {
  //         NSDictionary * userInfo = notification.request.content.userInfo;
  //         if ([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
  //           [JPUSHService handleRemoteNotification:userInfo];
  //           [[NSNotificationCenter defaultCenter] postNotificationName:kJPFDidReceiveRemoteNotification object:userInfo];
  //         }
  //         completionHandler(UNNotificationPresentationOptionAlert);
  //       }
  //       - (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler
  //       {
  //         NSDictionary * userInfo = response.notification.request.content.userInfo;
  //         if ([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
  //           [JPUSHService handleRemoteNotification:userInfo];
  //           [[NSNotificationCenter defaultCenter] postNotificationName:kJPFOpenNotification object:userInfo];
  //         }
  //         completionHandler();
  //       } 
  //     `
  //   }
  // ]
};