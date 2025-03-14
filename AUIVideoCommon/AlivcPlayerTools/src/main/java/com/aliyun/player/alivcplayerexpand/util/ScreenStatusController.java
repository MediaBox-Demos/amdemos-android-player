package com.aliyun.player.alivcplayerexpand.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by lifujun on 2017/9/12.
 * 屏幕开屏/锁屏监听工具类
 */
/****
 * Created by lifujun on 2017/9/12.
 * Screen On/Lock Listening Utility class
 */

public class ScreenStatusController {

    private String TAG = ScreenStatusController.class.getSimpleName();


    private Context mContext;
    private IntentFilter mScreenStatusFilter = null;
    private ScreenStatusListener mScreenStatusListener = null;

    public ScreenStatusController(Context context) {
        mContext = context;

        mScreenStatusFilter = new IntentFilter();
        mScreenStatusFilter.addAction(Intent.ACTION_SCREEN_ON);
        mScreenStatusFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenStatusFilter.addAction(Intent.ACTION_USER_PRESENT);
    }


    private BroadcastReceiver mScreenStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                Log.d(TAG,"ACTION_SCREEN_ON");
                if (mScreenStatusListener != null) {
                    mScreenStatusListener.onScreenOn();
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                Log.d(TAG,"ACTION_SCREEN_OFF");
                if (mScreenStatusListener != null) {
                    mScreenStatusListener.onScreenOff();
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            }
        }
    };


    //监听事件
    //Event listener
    public interface ScreenStatusListener {
        void onScreenOn();

        void onScreenOff();
    }
    //设置监听
    //Set listener
    public void setScreenStatusListener(ScreenStatusListener l) {
        mScreenStatusListener = l;
    }

    //开始监听
    //Start listening
    public void startListen() {
        if (mContext != null) {
            mContext.registerReceiver(mScreenStatusReceiver, mScreenStatusFilter);
        }
    }

    //结束监听
    //Stop listening
    public void stopListen() {
        if (mContext != null) {
            mContext.unregisterReceiver(mScreenStatusReceiver);
        }
    }

}
