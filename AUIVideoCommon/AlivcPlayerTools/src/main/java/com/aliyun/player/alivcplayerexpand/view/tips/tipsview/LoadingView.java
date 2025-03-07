package com.aliyun.player.alivcplayerexpand.view.tips.tipsview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 加载提示对话框。加载过程中，缓冲过程中会显示。
 */
/**
 * Loading Tip dialog box. It is displayed during loading and buffering.
 */
public class LoadingView extends RelativeLayout {
    private static final String TAG = LoadingView.class.getSimpleName();
    //加载提示文本框
    //loading tip text box
    private TextView mLoadPercentView;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alivc_dialog_loading, this);

        mLoadPercentView = (TextView) view.findViewById(R.id.net_speed);
        mLoadPercentView.setText(getContext().getString(R.string.alivc_loading) + " 0%");
    }

    /**
     * 更新加载进度
     *
     * @param percent 百分比
     */
    /****
     * Update the loading progress
     *
     * @param percent Percentage
     */
    public void updateLoadingPercent(int percent) {
        mLoadPercentView.setText(getContext().getString(R.string.alivc_loading) + percent + "%");
    }

    /**
     * 只显示loading，不显示进度提示
     */
    /****
     * Only display loading, without progress prompt
     */
    public void setOnlyLoading() {
        findViewById(R.id.loading_layout).setVisibility(GONE);
    }

}
