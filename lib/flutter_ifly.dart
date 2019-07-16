import 'dart:async';

import 'package:flutter/services.dart';

class FlutterIfly {
  static const MethodChannel _channel =
      const MethodChannel('flutter_ifly');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> speak(String dataFromJs) async {
    // res = "complete"
    final String res = await _channel.invokeMethod('speak', dataFromJs);
    return res;
  }

}
