package com.aliyun.player.alivcplayerexpand.view.tips.tipsview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.view.tips.OnTipsViewBackClickListener;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 网络变化提示对话框。当网络由wifi变为4g的时候会显示。
 */
/**
 * Network change alert dialog. Displayed when the network changes from wifi to 4g.
 */
public class NetChangeView extends RelativeLayout {
    //结束播放的按钮
    //Button to end playback
    private TextView mStopPlayBtn;
    //界面上的操作按钮事件监听
    //Interface for listening to events on the interface
    private OnNetChangeClickListener mOnNetChangeClickListener = null;
    private OnTipsViewBackClickListener mOnTipsViewBackClickListener = null;
    private ImageView mBackImageView;

    public NetChangeView(Context context) {
        super(context);
        init();
    }

    public NetChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NetChangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alivc_dialog_netchange, this);

        //继续播放的点击事件
        //Continue playback click event
        view.findViewById(R.id.continue_play).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnNetChangeClickListener != null) {
                    mOnNetChangeClickListener.onContinuePlay();
                }
            }
        });

        //停止播放的点击事件
        //Stop playback click event
        mStopPlayBtn = (TextView) view.findViewById(R.id.stop_play);
        mBackImageView = view.findViewById(R.id.iv_back);
        mStopPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnNetChangeClickListener != null) {
                    mOnNetChangeClickListener.onStopPlay();
                }
            }
        });

        mBackImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnTipsViewBackClickListener != null){
                    mOnTipsViewBackClickListener.onBackClick();
                }
            }
        });
    }

//    @Override
//    public void setTheme(Theme theme) {
//        //更新停止播放按钮的主题
//        if (theme == Theme.Blue) {
//            mStopPlayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_blue);
//            mStopPlayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_blue));
//        } else if (theme == Theme.Green) {
//            mStopPlayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_green);
//            mStopPlayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_green));
//        } else if (theme == Theme.Orange) {
//            mStopPlayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_orange);
//            mStopPlayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_orange));
//        } else if (theme == Theme.Red) {
//            mStopPlayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_red);
//            mStopPlayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_red));
//        }
//    }

    /**
     * 界面中的点击事件
     */
    /****
     * Interface for listening to events on the interface
     */
    public interface OnNetChangeClickListener {
        /**
         * 继续播放
         */
        /****
         * Continue playback
         */
        void onContinuePlay();

        /**
         * 停止播放
         */
        /****
         * Stop playback
         */
        void onStopPlay();
    }

    /**
     * 设置界面的点击监听
     *
     * @param l 点击监听
     */
    /****
     * Set the click listener of the interface
     *
     * @param l Click listener
     */
    public void setOnNetChangeClickListener(OnNetChangeClickListener l) {
        mOnNetChangeClickListener = l;
    }

    /**
     * 设置返回按钮监听
     */
    /****
     * Set the back button listener
     */
    public void setOnBackClickListener(OnTipsViewBackClickListener listener){
        this.mOnTipsViewBackClickListener = listener;
    }

}
