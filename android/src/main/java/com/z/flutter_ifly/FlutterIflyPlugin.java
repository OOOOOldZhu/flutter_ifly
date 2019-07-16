package com.z.flutter_ifly;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterIflyPlugin
 */

public class FlutterIflyPlugin implements MethodCallHandler {

    /*
     * Plugin registration.
     */

    public static void registerWith(Registrar registrar) {

        SpeechUtility.createUtility(registrar.activity(), SpeechConstant.APPID +"=5d2db447");
        // boolean isCn, Activity activity, VoiceListener voiceListener
        Ifly.initIfly(true,true, registrar.activity(), new VoiceListener() {
            @Override
            public void onStatus(int status, Object msg) {
                if(status == Ifly.ON_MSG){
                    listenResult.success(msg.toString());
                }
            }
        });

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_ifly");
        channel.setMethodCallHandler(new FlutterIflyPlugin());
    }

    static Result listenResult;

    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("listen")) {

            listenResult = result;
            //Ifly.startListening();
            Ifly.listenWithUI();

        }else if (call.method.equals("speak")) {

            Ifly.speak(call.arguments.toString(), new SpeakListenner() {
                @Override
                public void onEnd() {
                    result.success("complete");
                }
            });

        } else {
            result.notImplemented();
        }
    }

}
