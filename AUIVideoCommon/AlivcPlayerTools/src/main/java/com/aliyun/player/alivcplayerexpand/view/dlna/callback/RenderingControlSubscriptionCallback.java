package com.aliyun.player.alivcplayerexpand.view.dlna.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Intents;

import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.util.Map;

/**
 * RenderingControl 事件回传
 */
/****
 * RenderingControl event callbacks
 */

public class RenderingControlSubscriptionCallback extends BaseSubscriptionCallback {

    private static final String TAG = RenderingControlSubscriptionCallback.class.getSimpleName();

    public RenderingControlSubscriptionCallback(Service service, Context context) {
        super(service, context);
    }

    @Override
    protected void eventReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        if (values == null || values.size() == 0) {
            return;
        }
        if (mContext == null) {
            return;
        }
        if (!values.containsKey("LastChange")) {
            return;
        }

        String lastChangeValue = values.get("LastChange").toString();
        Log.i(TAG, "LastChange:" + lastChangeValue);
        LastChange lastChange;
        try {
            lastChange = new LastChange(new RenderingControlLastChangeParser(), lastChangeValue);
            //获取音量 volume
            // Get volume
            int volume = 0;
            if (lastChange.getEventedValue(0, RenderingControlVariable.Volume.class) != null) {

                volume = lastChange.getEventedValue(0, RenderingControlVariable.Volume.class).getValue().getVolume();

                Log.e(TAG, "onVolumeChange volume: " + volume);
                Intent intent = new Intent(Intents.ACTION_VOLUME_CALLBACK);
                intent.putExtra(Intents.EXTRA_VOLUME, volume);
                mContext.sendBroadcast(intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

