import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_ifly/flutter_ifly.dart';

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
    await FlutterIfly.speak("从这里我们可看出来是一个函数,既然知道是个函数，那我们该如何使用呢");
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
                         child: Text("dianji"),
                     )
                ),
        ),
      ),
    );
  }
}
