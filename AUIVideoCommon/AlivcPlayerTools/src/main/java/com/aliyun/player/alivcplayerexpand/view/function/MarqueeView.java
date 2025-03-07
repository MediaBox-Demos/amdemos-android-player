package com.aliyun.player.alivcplayerexpand.view.function;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;
import com.aliyun.player.alivcplayerexpand.util.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * 跑马灯
 */
/****
 * MarqueeView
 */
public class MarqueeView extends FrameLayout {

    private static final int START = 1;
    private static final int PAUSE = 2;
    private static final int STOP = 3;

    private int mCurrentState = -1;

    /**
     * 跑马灯默认时长
     */
    /****
     * Marquee default duration
     */
    private int mInterval = 5000;
    /**
     * 跑马灯默认字体大小
     */
    /****
     * Marquee default text size
     */
    private int mTextSize = 14;

    /**
     * 跑马灯默认字体颜色
     */
    /****
     * Marquee default text color
     */
    private int mTextColor = getResources().getColor(R.color.alivc_common_font_white_light);

    /**
     * 跑马灯默认内容
     */
    /****
     * Marquee default text
     */
    private String mFlipText = getResources().getString(R.string.alivc_marquee_test);
    /**
     * 跑马灯的动画
     */
    /****
     * Marquee animation
     */
    private ObjectAnimator objectAnimator;

    /**
     * 跑马灯是否开启
     */
    /****
     * Marquee is or not start
     */
    private boolean isStart = false;

    /**
     * 动画是否在执行
     */
    /****
     * Animation is or not start
     */
    private boolean isAnimStart = false;

    /**
     * Context
     */
    private Context mContext;
    private MarQueeHandler mMarQueeHandler;
    /**
     * 根View
     */
    /****
     * RootView
     */
    private View view;
    /**
     * 内容TextView
     */
    /****
     * ContentTextView
     */
    private TextView mContentTextView;
    /**
     * root
     */
    private RelativeLayout mMarqueeRootRelativeLayout;
    /**
     * 当前屏幕模式
     */
    /****
     * Current screen mode
     */
    private AliyunScreenMode mScreenMode = AliyunScreenMode.Small;

    private static class MarQueeHandler extends Handler {

        private WeakReference<MarqueeView> weakReference;

        public MarQueeHandler(MarqueeView marqueeView) {
            this.weakReference = new WeakReference<>(marqueeView);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    MarqueeView marqueeView = weakReference.get();
                    if (marqueeView != null) {
                        if(marqueeView.mScreenMode == AliyunScreenMode.Small){
                            marqueeView.stopFlip();
                            return ;
                        }
                        if (!marqueeView.isAnimStart && marqueeView.mCurrentState == START) {
                            marqueeView.objectAnimator.start();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public MarqueeView(Context context) {
        super(context);
        init(context);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.alivc_marquee_view, this);
        initView();

        initHandler();
    }

    private void initView() {
        mContentTextView = view.findViewById(R.id.tv_content);
        mMarqueeRootRelativeLayout = view.findViewById(R.id.marquee_root);
        mContentTextView.setText(mFlipText);
    }

    private void initHandler() {
        mMarQueeHandler = new MarQueeHandler(this);
    }

    /**
     * 设置跑马灯时长
     *
     * @param milliseconds 跑马灯时长,毫秒
     */
    /****
     * Set marquee duration
     *
     * @param milliseconds Marquee duration, milliseconds
     */
    public void setInterval(int milliseconds) {
        if (milliseconds < 5000) {
            milliseconds = 5000;
        }
        this.mInterval = milliseconds;
        objectAnimator.setDuration(mInterval);
    }

    /**
     * 设置字体大小
     */
    /****
     * Set text size
     */
    public void setTextSize(int size) {
        this.mTextSize = size;
        mContentTextView.setText(mTextSize);
    }

    /**
     * 设置字体颜色
     */
    /****
     * Set text color
     */
    public void setTextColor(int color) {
        this.mTextColor = color;
        mContentTextView.setTextColor(mTextColor);
    }

    /**
     * 设置跑马灯内容
     */
    /****
     * Set marquee content
     */
    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        this.mFlipText = text;
        mContentTextView.setText(mFlipText);
    }

    /**
     * 跑马灯是否开启
     */
    /****
     * Marquee is or not start
     */
    public boolean isStart() {
        return isStart;
    }


    /**
     * 开启跑马灯
     */
    /****
     * Start marquee
     */
    public void startFlip() {
        if(mScreenMode == AliyunScreenMode.Small){
            return ;
        }
        this.mCurrentState = START;
        mMarqueeRootRelativeLayout.setVisibility(View.VISIBLE);
        isStart = true;
        if (mMarQueeHandler != null) {
            mMarQueeHandler.sendEmptyMessage(START);
        }
    }

    /**
     * 关闭跑马灯
     */
    /****
     * Stop marquee
     */
    public void stopFlip() {
        this.mCurrentState = STOP;
        mMarqueeRootRelativeLayout.setVisibility(View.INVISIBLE);
        isStart = false;
        if (mMarQueeHandler != null) {
            mMarQueeHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 暂停
     */
    /****
     * Pause
     */
    public void pause() {
        this.mCurrentState = PAUSE;
        mMarqueeRootRelativeLayout.setVisibility(View.INVISIBLE);
        if (mMarQueeHandler != null) {
            mMarQueeHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 设置当前屏幕状态
     */
    /****
     * Set current screen mode
     */
    public void setScreenMode(AliyunScreenMode screenMode) {
        this.mScreenMode = screenMode;
    }

    /**
     * 创建动画
     */
    /****
     * Create animation
     */
    public void createAnimation() {
        int textWidth = mContentTextView.getMeasuredWidth();
        int screenWidth = ScreenUtils.getWidth(getContext());
        if(objectAnimator == null){
            objectAnimator = ObjectAnimator.ofFloat(mContentTextView, "translationX", textWidth, -screenWidth);
            objectAnimator.setDuration(mInterval);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mMarqueeRootRelativeLayout != null) {
                        mMarqueeRootRelativeLayout.setVisibility(View.VISIBLE);
                    }
                    isAnimStart = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mMarqueeRootRelativeLayout != null) {
                        mMarqueeRootRelativeLayout.setVisibility(View.INVISIBLE);
                    }
                    isAnimStart = false;
                    mMarQueeHandler.sendEmptyMessageDelayed(START, 2000);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimStart = false;
                    if (mMarqueeRootRelativeLayout != null) {
                        mMarqueeRootRelativeLayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * 跑马灯显示区域
     */
    /****
     * Marquee display area
     */
    public enum MarqueeRegion{
        /**
         * 顶部
         */
        /****
         * Top
         */
        TOP,
        /**
         * 中间
         */
        /****
         * Middle
         */
        MIDDLE,
        /**
         * 底部
         */
        /****
         * Bottom
         */
        BOTTOM;
    }

}
