import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_ifly/flutter_ifly.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {

      await PermissionHandler().requestPermissions([PermissionGroup.microphone]);
      await PermissionHandler().requestPermissions([PermissionGroup.storage]);

      platformVersion = 'success . . . .';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });

  }

  _onClick() async {
    print('_onClick - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - > ');
//    await FlutterIfly.speak("点击这的文字,播放语音.点击这的文字,播放语音.点击这的文字,播放语音.点击这的文字,播放语音.");
    String str = await FlutterIfly.listen();
    print("语音识别结果 - - - - - - - - - - - - - - - - - - - -  > "+str);
  }

  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: new Container(
                child:
                     GestureDetector(
                         onTap: _onClick,//写入方法名称就可以了，但是是无参的
                         child: Text("点击这的文字，\r\n播放语音"),
                     )
                ),
        ),
      ),
    );
  }
}
