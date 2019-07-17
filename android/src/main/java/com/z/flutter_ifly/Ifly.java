package com.z.flutter_ifly;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/*
 * ：Created by z on 2019/2/23
 */

public class Ifly {

    public static int ERR = 0;
    public static int ON_MSG = 1;
    public static int ON_END = 2;

    private static String TAG = "xiaoqiang";
    private static boolean isCN;
    private static RecognizerListener mRecognizerListener;
    private static SpeechRecognizer speechRecognizer;
    private static VoiceListener staticVoiceListener;

    public static void initIfly(boolean withUI, boolean isCn, Activity activity, VoiceListener voiceListener) {

        //if (speechRecognizer != null) return;//这句话应该删除，因为创建两个fragment的时候，第二个fragment不会new Ifly
        staticVoiceListener = voiceListener;
        isCN = isCn;

        InitListener initListener = new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "对象初始化 - - - - - - > " + code);
                if (code != ErrorCode.SUCCESS) {
                    Log.d(TAG, "对象初始化失败，错误码" + code);
                    staticVoiceListener.onStatus(ERR, code);
                } else {
                    //Log.d(TAG, "对象初始化成功，状态码" + code);
                }
            }
        };

        Log.d(TAG, "initIfly: - - - - - - - - - - - - - - - - - - - - - - - - - > " + activity);

        if (withUI) {
            initListenWithUI(isCn, activity, initListener);
        } else {
            initListen(activity, voiceListener, initListener);
        }

        if (speechSynthesizer == null) {
            initSpeak(activity, new InitListener() {
                @Override
                public void onInit(int i) {
                    Log.d(TAG, "onInit: " + i);
                }
            });
        }
    }

    public static void initListen(Activity activity, VoiceListener voiceListener, InitListener initListener) {
        speechRecognizer = SpeechRecognizer.createRecognizer(activity, initListener);
        Log.d(TAG, "initIfly: - - - - - - - - - - - - - - - - - - - - - - - - - > " + speechRecognizer);
        speechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        //zh_cn en_GB  com.sun.javafx.font  PrismFontFile.java
        // http://mscdoc.xfyun.cn/android/api/    中的SpeechRecognizer类

        if (isCN) {
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin ");
        } else {
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
            speechRecognizer.setParameter(SpeechConstant.ACCENT, null);
        }

        mRecognizerListener = new RecognizerListener() {
            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                try {
                    String json = results.getResultString();
                    String text = JsonParser.parseIatResult(json);
                    if (text.equals(".") || text.equals("。") || text.equals("？") || text.equals("")) {

                    } else {
                        staticVoiceListener.onStatus(ON_MSG, text);
                    }
                    if (isLast) {
                        Log.d(TAG, "onResult()最后一次 : " + results.getResultString());
                        speechRecognizer.stopListening();
                    }
                } catch (Exception e) {
                    Log.i(TAG, "voice返回结果的异常 : " + e);
                    staticVoiceListener.onStatus(ERR, e);
                }
            }


            @Override
            public void onError(SpeechError error) {
                try {
                    Log.w(TAG, "onError: " + error.getHtmlDescription(true));
                    Log.d(TAG, "语音识别报错onError() :" + error.getErrorCode() + "  " + error.getErrorDescription());
                    staticVoiceListener.onStatus(ERR, error);
                    speechRecognizer.stopListening();
                    // Tips：
                    // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
                    // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
                    //抬起 ，关闭动画
                } catch (Exception e) {
                    Log.i(TAG, "onError()异常 : " + e);
                }
            }

            @Override
            public void onEndOfSpeech() {
                try {
                    // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
                    Log.d(TAG, "onEndOfSpeech() : 说话结束 ");
//                    adapter.addString(getString(R.string.speech_end), true);
//                    recyclerView.scrollToPosition(adapter.key.size() - 1);

                    staticVoiceListener.onStatus(ON_END, "");

                } catch (Exception e) {
                    Log.i(TAG, "onEndOfSpeech()异常 : " + e);
                }
            }

            @Override
            public void onBeginOfSpeech() {
            }

            @Override
            public void onVolumeChanged(int volume, byte[] data) {
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            }
        };
    }

    public static void startListening() {
        if (speechRecognizer != null && mRecognizerListener != null) {
            int ret = speechRecognizer.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.d(TAG, "识别失败,错误码: " + ret);
                staticVoiceListener.onStatus(ERR, ret);
            }
        }
    }

    public static void stopListening() {
        if (speechRecognizer != null) speechRecognizer.stopListening();
    }

    static SpeechSynthesizer speechSynthesizer;

    private static void initSpeak(Context mContext, InitListener mInitListener) {
        // todo 设置发言人
        String speechPerson = null;
        if (speechPerson == null || speechPerson == "") {
            speechPerson = "xiaoyan";
        }

        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, mInitListener);
        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, speechPerson);//设置发音人
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "70");//设置语速
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        //speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
    }

    public static void speak(String str, final SpeakListenner speakListenner) {
        if (speechSynthesizer != null) {
            speechSynthesizer.startSpeaking(str, new SynthesizerListener() {

                //会话结束回调接口，没有错误时，error为null
                public void onCompleted(SpeechError error) {
                    speakListenner.onEnd();
                }

                //缓冲进度回调
                //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
                public void onBufferProgress(int percent, int beginPos, int endPos, String info) {

                }

                //开始播放
                public void onSpeakBegin() {

                }

                //暂停播放
                public void onSpeakPaused() {

                }

                //播放进度回调
                //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
                public void onSpeakProgress(int percent, int beginPos, int endPos) {
                    if (percent == 100) {
                        //speakListenner.onEnd();
                    }
                }

                //恢复播放回调接口
                public void onSpeakResumed() {

                }

                //会话事件回调接口
                public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

                }
            });
        }
    }

    private static RecognizerDialog mIatDialog;

    private static void initListenWithUI(boolean isCN, Context context, InitListener initListener) {

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(context, initListener);

        //以下为dialog设置听写参数
        mIatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        //zh_cn en_GB  com.sun.javafx.font  PrismFontFile.java
        // http://mscdoc.xfyun.cn/android/api/    中的SpeechRecognizer类

        if (isCN) {
            mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
        } else {
            mIatDialog.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIatDialog.setParameter(SpeechConstant.ACCENT, null);
        }

        //开始识别并设置监听器
        mIatDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                Log.d(TAG, "onResult: ");
                try {
                    String json = results.getResultString();
                    String text = JsonParser.parseIatResult(json);
                    if (text.equals(".") || text.equals("。") || text.equals("？") || text.equals("")) {

                    } else {
                        staticVoiceListener.onStatus(ON_MSG, text);
                    }
                    if (isLast) {
                        Log.d(TAG, "onResult()最后一次 : " + results.getResultString());
                        //mIatDialog.stop();
                    }
                } catch (Exception e) {
                    Log.i(TAG, "voice返回结果的异常 : " + e);
                    staticVoiceListener.onStatus(ERR, e);
                }
            }

            @Override
            public void onError(SpeechError error) {
                try {
                    Log.w(TAG, "onError: " + error.getHtmlDescription(true));
                    Log.d(TAG, "语音识别报错onError() :" + error.getErrorCode() + "  " + error.getErrorDescription());
                    staticVoiceListener.onStatus(ERR, error);
                    //mIatDialog.stopListening();
                    // Tips：
                    // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
                    // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
                    //抬起 ，关闭动画
                } catch (Exception e) {
                    Log.i(TAG, "onError()异常 : " + e);
                }
            }
        });

    }

    public static void listenWithUI(/*boolean isCN, Context context*/) {
//        if (mIatDialog == null) {
//            initListenWithUI(isCN, context);
//        }
        //显示听写对话框
        //mIatDialog.set
        mIatDialog.show();
        TextView txt = (TextView) mIatDialog.getWindow().getDecorView().findViewWithTag("textlink");
        txt.setText("power by microduino");
    }

}
