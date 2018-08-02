//
//  RCTXGActionQueue.h
//  RCTXGPushModule
//
//  Created by shixiaoquan on 2018/3/21.
//  Copyright © 2018年 com.shixiaoquan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTXGPushModule.h"

@interface RCTXGActionQueue : NSObject

@property BOOL isReactDidLoad;
@property NSDictionary* openedRemoteNotification;
@property NSDictionary* openedLocalNotification;
@property(strong,nonatomic)NSMutableArray<RCTResponseSenderBlock>* getRidCallbackArr;

+ (nonnull instancetype)sharedInstance;

- (void)postNotification:(NSNotification *)notification;
- (void)scheduleNotificationQueue;

- (void)postGetRidCallback:(RCTResponseSenderBlock) getRidCallback;
- (void)scheduleGetRidCallbacks;

@end
