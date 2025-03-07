package com.aliyun.player.alivcplayerexpand.listener;

/**
 * 清晰度切换监听
 */
/****
 * Sharpness toggle listener
 */
public interface OnChangeQualityListener {

    void onChangeQualitySuccess(String quality);

    void onChangeQualityFail(int code, String msg);
}
