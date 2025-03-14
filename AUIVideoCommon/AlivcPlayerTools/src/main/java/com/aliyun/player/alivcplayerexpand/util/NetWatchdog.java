package com.aliyun.player.alivcplayerexpand.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;


/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 网络连接状态的监听器。通过注册broadcast实现的
 */
/**
 * Listener for network connection status. This is achieved by registering a broadcast
 */
public class NetWatchdog {

    private static final String TAG = NetWatchdog.class.getSimpleName();


    private Context mContext;
    //网络变化监听
    //Network Change Listening
    private NetChangeListener mNetChangeListener;
    private NetConnectedListener mNetConnectedListener;

    //广播过滤器，监听网络变化
    //Broadcast Filter, listen network change
    private IntentFilter mNetIntentFilter = new IntentFilter();

    /**
     * 网络变化监听事件
     */
    /****
     * Network Change Listening
     */
    public interface NetChangeListener {
        /**
         * wifi变为4G
         */
        /****
         * wifi to 4G
         */
        void onWifiTo4G();

        /**
         * 4G变为wifi
         */
        /****
         * 4G to wifi
         */
        void on4GToWifi();

        /**
         * 网络断开
         */
        /****
         * Network Disconnected
         */
        void onNetDisconnected();
    }

    private boolean isReconnect;
    /**
     * 判断是否有网络的监听
     */
    /****
     * Determine if there is a network listener
     */
    public interface NetConnectedListener {
        /**
         * 网络已连接
         */
        /****
         * Network Connected
         */
        void onReNetConnected(boolean isReconnect);

        /**
         * 网络未连接
         */
        /****
         * Network UnConnected
         */
        void onNetUnConnected();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取手机的连接服务管理器，这里是连接管理器类
            //Get the connection service manager of the phone, here is the connection manager class
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

            NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
            NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;

            Bundle bundle = intent.getExtras();
            NetworkInfo netInfo = null;
            if(bundle != null){
                netInfo = (NetworkInfo) bundle.get(ConnectivityManager.EXTRA_NETWORK_INFO);
            }


            if (wifiNetworkInfo != null) {
                wifiState = wifiNetworkInfo.getState();
            }
            if (mobileNetworkInfo != null) {
                mobileState = mobileNetworkInfo.getState();
            }

            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                if (mNetConnectedListener != null) {
                    mNetConnectedListener.onReNetConnected(isReconnect);
                    isReconnect = false;
                }
            } else if (activeNetworkInfo == null){
                if (mNetConnectedListener != null) {
                    isReconnect = true;
                    mNetConnectedListener.onNetUnConnected();
                }
            }

            if (NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
                Log.d(TAG, "onWifiTo4G()");
                if (mNetChangeListener != null) {
                    mNetChangeListener.onWifiTo4G();
                }
            } else if (NetworkInfo.State.CONNECTED == wifiState && NetworkInfo.State.CONNECTED != mobileState
                    && netInfo != null && netInfo.getType() == 1) {
                /*
                    4G 切换 wifi 时候，首先会收到 4G 断开的通知，再收到 wifi 连接的通知，会导致
                    该回调被调用两次。netInfo.getType()：1：wifi，0：4G
                 */
                /*
                    When 4G switches wifi, first we will receive 4G disconnect notification, then we will receive wifi connect notification, which will lead to
                    This callback is called twice. netInfo.getType(): 1: wifi, 0: 4G
                 */
                if (mNetChangeListener != null) {
                    mNetChangeListener.on4GToWifi();
                }
            } else if (NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED != mobileState) {
                if (mNetChangeListener != null) {
                    mNetChangeListener.onNetDisconnected();
                }
            }

        }
    };


    public NetWatchdog(Context context) {
        mContext = context.getApplicationContext();
        mNetIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    /**
     * 设置网络变化监听
     *
     * @param l 监听事件
     */
    /****
     * Set Network Change Listener
     *
     * @param l listener
     */
    public void setNetChangeListener(NetChangeListener l) {
        mNetChangeListener = l;
    }

    public void setNetConnectedListener(NetConnectedListener mNetConnectedListener) {
        this.mNetConnectedListener = mNetConnectedListener;
    }

    /**
     * 开始监听
     */
    /****
     * Start Watch
     */
    public void startWatch() {
        try {
            mContext.registerReceiver(mReceiver, mNetIntentFilter);
        } catch (Exception e) {
        }
    }

    /**
     * 结束监听
     */
    /****
     * Stop Watch
     */
    public void stopWatch() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }


    /**
     * 静态方法获取是否有网络连接
     *
     * @param context 上下文
     * @return 是否连接
     */
    /****
     * Static method to get whether there is a network connection
     *
     * @param context context
     * @return whether connected
     */
    public static boolean hasNet(Context context) {
        //获取手机的连接服务管理器，这里是连接管理器类
        //Get the connection service manager of the phone, here is the connection manager class
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
        NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;

        if (wifiNetworkInfo != null) {
            wifiState = wifiNetworkInfo.getState();
        }
        if (mobileNetworkInfo != null) {
            mobileState = mobileNetworkInfo.getState();
        }

        if (NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED != mobileState) {
            return false;
        }
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            return false;
        }

        return true;
    }

    /**
     * 静态判断是不是4G网络
     *
     * @param context 上下文
     * @return 是否是4G
     */
    /****
     * Static judge whether it is 4G network
     *
     * @param context context
     * @return whether 4G
     */
    public static boolean is4GConnected(Context context) {
        //获取手机的连接服务管理器，这里是连接管理器类
        //Get the connection service manager of the phone, here is the connection manager class
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;

        if (mobileNetworkInfo != null) {
            mobileState = mobileNetworkInfo.getState();
        }

        return NetworkInfo.State.CONNECTED == mobileState;
    }

}
