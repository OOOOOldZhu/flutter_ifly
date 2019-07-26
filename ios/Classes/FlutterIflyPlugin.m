#import "FlutterIflyPlugin.h"

@implementation FlutterIflyPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_ifly"
            binaryMessenger:[registrar messenger]];
  FlutterIflyPlugin* instance = [[FlutterIflyPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if([@"speak" isEqualToString:call.method]){
    //朗读
    NSLog(@"朗读。 。 。 。 。 。");
  }else if([@"" isEqualToString:call.method]){
    //语音识别
    NSLog(@"语音识别。 。 。 。 。 。");
  }else{
    result(FlutterMethodNotImplemented);
  }
}

@end
