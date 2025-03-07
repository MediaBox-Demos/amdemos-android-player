package com.aliyun.player.alivcplayerexpand.view.gesturedialog;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import com.aliyun.player.alivcplayerexpand.R;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的时的亮度提示框
 */
/**
 * Gesture sliding when the brightness prompt box
 */
public class BrightnessDialog extends BaseGestureDialog {


    private static final String TAG = BrightnessDialog.class.getSimpleName();

    // 当前亮度。0~100
    // Current brightness. 0~100
    private int mCurrentBrightness = 0;

    public BrightnessDialog(Activity activity, int percent) {
        super(activity);

        mCurrentBrightness = percent;

        //设置亮度图片
        //Set brightness image
        mImageView.setImageResource(R.drawable.alivc_brightness);
        updateBrightness(percent);
    }

    /**
     * 更新对话框上的亮度百分比
     * @param percent  亮度百分比
     */
    /****
     * Update the brightness percentage in the dialog box
     *
     * @param percent Brightness percentage
     */
    public void updateBrightness(int percent) {
        mTextView.setText(percent + "%");
    }


    /**
     * 获取当前亮度百分比
     * @param activity 活动
     * @return 当前亮度百分比
     */
    /****
     * Get the current brightness percentage
     *
     * @param activity Activity
     * @return Current brightness percentage
     */
    public static int getActivityBrightness(Activity activity) {
        if (activity != null) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();

            float screenBrightness = layoutParams.screenBrightness;
            if (screenBrightness > 1) {
                screenBrightness = 1;
            } else if (screenBrightness < 0.1f) {
                //解决三星某些手机亮度值等于0自动锁屏的bug
                //Solve the bug of some Samsung smartphones with brightness value equal to 0 automatically locked
                screenBrightness = 0.1f;
            }
            return (int) (screenBrightness * 100);
        }
        return 0;
    }

    /**
     * 计算最终的亮度百分比
     * @param changePercent 变化的百分比
     * @return 最终的亮度百分比
     */
    /****
     * Calculate the final brightness percentage
     *
     * @param changePercent Change percent
     * @return Final brightness percentage
     */
    public int getTargetBrightnessPercent(int changePercent) {

        int newBrightness = mCurrentBrightness - changePercent;
        if (newBrightness > 100) {
            newBrightness = 100;
        } else if (newBrightness < 0) {
            newBrightness = 0;
        }
        return newBrightness;
    }

}
