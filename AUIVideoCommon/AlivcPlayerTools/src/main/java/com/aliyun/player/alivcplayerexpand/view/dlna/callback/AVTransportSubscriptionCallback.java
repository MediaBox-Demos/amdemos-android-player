package com.aliyun.player.alivcplayerexpand.view.dlna.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Config;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Intents;
import com.aliyun.player.alivcplayerexpand.util.Formatter;

import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;

import java.util.Map;

/**
 * tv控制回调
 */
/****
 * tv control callbacks
 */

public class AVTransportSubscriptionCallback extends BaseSubscriptionCallback {

    private static final String TAG = AVTransportSubscriptionCallback.class.getSimpleName();

    public AVTransportSubscriptionCallback(org.fourthline.cling.model.meta.Service service, Context context) {
        super(service, context);
    }

    @Override
    protected void eventReceived(GENASubscription subscription) { // 这里进行 事件接收处理
        if (mContext == null)
            return;

        Map values = subscription.getCurrentValues();
        if (values != null && values.containsKey("LastChange")) {
            String lastChangeValue = values.get("LastChange").toString();
            Log.i(TAG, "LastChange:" + lastChangeValue);
            doAVTransportChange(lastChangeValue);
        }
    }

    private void doAVTransportChange(String lastChangeValue) {
        try {
            LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeValue);

            //Parse TransportState value.
            AVTransportVariable.TransportState transportState = lastChange.getEventedValue(0, AVTransportVariable.TransportState.class);
            if (transportState != null) {
                TransportState ts = transportState.getValue();
                if (ts == TransportState.PLAYING) {
                    Log.e(TAG, "PLAYING");
                    Intent intent = new Intent(Intents.ACTION_PLAYING);
                    mContext.sendBroadcast(intent);
                    return;
                } else if (ts == TransportState.PAUSED_PLAYBACK) {
                    Log.e(TAG, "PAUSED_PLAYBACK");
                    Intent intent = new Intent(Intents.ACTION_PAUSED_PLAYBACK);
                    mContext.sendBroadcast(intent);
                    return;
                } else if (ts == TransportState.STOPPED) {
                    Log.e(TAG, "STOPPED");
                    Intent intent = new Intent(Intents.ACTION_STOPPED);
                    mContext.sendBroadcast(intent);
                    return;
                } else if (ts == TransportState.TRANSITIONING) { // 转菊花状态
                    Log.e(TAG, "BUFFER");
                    Intent intent = new Intent(Intents.ACTION_TRANSITIONING);
                    mContext.sendBroadcast(intent);
                    return;
                }
            }

            //RelativeTimePosition
            String position = "00:00:00";
            AVTransportVariable.RelativeTimePosition eventedValue = lastChange.getEventedValue(0, AVTransportVariable.RelativeTimePosition.class);
            if (eventedValue != null) {
                position = lastChange.getEventedValue(0, AVTransportVariable.RelativeTimePosition.class).getValue();
                int intTime = Formatter.getIntTime(position);
                Log.e(TAG, "position: " + position + ", intTime: " + intTime);

                // 该设备支持进度回传
                // The device supports progress back
                Config.getInstance().setHasRelTimePosCallback(true);

                Intent intent = new Intent(Intents.ACTION_POSITION_CALLBACK);
                intent.putExtra(Intents.EXTRA_POSITION, intTime);
                mContext.sendBroadcast(intent);

                // TODO: 17/7/20 ACTION_PLAY_COMPLETE 播完了

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}