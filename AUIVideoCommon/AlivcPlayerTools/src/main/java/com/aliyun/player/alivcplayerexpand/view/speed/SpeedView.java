package com.aliyun.player.alivcplayerexpand.view.speed;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.listener.LockPortraitListener;
import com.aliyun.player.alivcplayerexpand.theme.ITheme;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 倍速播放界面。用于控制倍速。
 * 在{@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView}中使用。
 */
/**
 * Multiplier playback interface. Used to control the multiplier speed.
 * Used in {@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView}
 */

public class SpeedView extends RelativeLayout implements ITheme {

    private static final String TAG = SpeedView.class.getSimpleName();

    private SpeedValue mSpeedValue;

    private View mMainSpeedView;
    //显示动画
    //show animation
    private Animation showAnim;
    //隐藏动画
    //hide animation
    private Animation hideAnim;
    //动画是否结束
    //animation end or not
    private boolean animEnd = true;

    // 正常倍速
    //normal speed
    private RadioButton mNormalBtn;
    //1.25倍速
    //1.25x
    private RadioButton mOneQrtTimeBtn;
    //1.5倍速
    //1.5x
    private RadioButton mOneHalfTimeBtn;
    //2倍速
    //2x
    private RadioButton mTwoTimeBtn;

    //切换结果的提示
    //switch result tips
    private TextView mSpeedTip;
    //屏幕模式
    //screen mode
    private AliyunScreenMode mScreenMode;
    //倍速选择事件
    //Multiple Speed Selection Event
    private OnSpeedClickListener mOnSpeedClickListener = null;
    //倍速是否变化
    //Multiple Speed Change or not
    private boolean mSpeedChanged = false;
    //选中的倍速的指示点的方块
    //Selected Speed Indicator Block
    private int mSpeedDrawable = R.drawable.alivc_speed_dot_blue;
    //选中的倍速的指示点的文字
    //Selected Speed Indicator Text
    private int mSpeedTextColor = R.color.alivc_blue;

    public SpeedView(Context context) {
        super(context);
        init();
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public SpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化布局
        //Initialize Layout
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_view_speed, this, true);
        mMainSpeedView = findViewById(R.id.speed_view);
        mMainSpeedView.setVisibility(INVISIBLE);

        //找出控件
        //Find out the control
        mOneQrtTimeBtn = (RadioButton) findViewById(R.id.one_quartern);
        mNormalBtn = (RadioButton) findViewById(R.id.normal);
        mOneHalfTimeBtn = (RadioButton) findViewById(R.id.one_half);
        mTwoTimeBtn = (RadioButton) findViewById(R.id.two);

        mSpeedTip = (TextView) findViewById(R.id.speed_tip);
        mSpeedTip.setVisibility(INVISIBLE);

        //对每个倍速项做点击监听
        //Listen for each speed item click
        mOneQrtTimeBtn.setOnClickListener(mClickListener);
        mNormalBtn.setOnClickListener(mClickListener);
        mOneHalfTimeBtn.setOnClickListener(mClickListener);
        mTwoTimeBtn.setOnClickListener(mClickListener);

        //倍速view使用到的动画
        //Animation used by the speed view
        showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_speed_show);
        hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.view_speed_hide);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //显示动画开始的时候，将倍速view显示出来
                //When the show animation starts, show the speed view
                animEnd = false;
                mMainSpeedView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animEnd = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //隐藏动画结束的时候，将倍速view隐藏掉
                //When the hide animation ends, hide the speed view
                mMainSpeedView.setVisibility(INVISIBLE);
                if (mOnSpeedClickListener != null) {
                    mOnSpeedClickListener.onHide();
                }

                //如果倍速有变化，会提示倍速变化的消息
                //If the speed has changed, will prompt the message of the speed change
                if (mSpeedChanged) {
                    String times = "";
                    if (mSpeedValue == SpeedValue.OneQuartern) {
                        times = getResources().getString(R.string.alivc_speed_optf_times);
                    } else if (mSpeedValue == SpeedValue.Normal) {
                        times = getResources().getString(R.string.alivc_speed_one_times);
                    } else if (mSpeedValue == SpeedValue.OneHalf) {
                        times = getResources().getString(R.string.alivc_speed_opt_times);
                    } else if (mSpeedValue == SpeedValue.Twice) {
                        times = getResources().getString(R.string.alivc_speed_twice_times);
                    }
                    String tips = getContext().getString(R.string.alivc_speed_tips, times);
                    mSpeedTip.setText(tips);
                    mSpeedTip.setVisibility(VISIBLE);
                    mSpeedTip.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeedTip.setVisibility(INVISIBLE);
                        }
                    }, 1000);
                }
                animEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        setSpeed(SpeedValue.Normal);
//监听view的Layout事件
        //Listen for the layout event of the view
        getViewTreeObserver().addOnGlobalLayoutListener(new MyLayoutListener());
    }


    /**
     * 设置主题
     *
     * @param theme 支持的主题
     */
    /****
     * Set the theme
     *
     * @param theme Supported themes
     */
    @Override
    public void setTheme(Theme theme) {

        mSpeedDrawable = R.drawable.alivc_speed_dot_blue;
        mSpeedTextColor = R.color.alivc_blue;
        //根据主题变化对应的颜色
        //Change the corresponding color according to the theme
        if (theme == Theme.Blue) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_blue;
            mSpeedTextColor = R.color.alivc_blue;
        } else if (theme == Theme.Green) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_green;
            mSpeedTextColor = R.color.alivc_green;
        } else if (theme == Theme.Orange) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_orange;
            mSpeedTextColor = R.color.alivc_orange;
        } else if (theme == Theme.Red) {
            mSpeedDrawable = R.drawable.alivc_speed_dot_red;
            mSpeedTextColor = R.color.alivc_red;
        }

        updateBtnTheme();
    }

    /**
     * 更新按钮的颜色之类的
     */
    /****
     * Update the color of the button and other things
     */
    private void setRadioButtonTheme(RadioButton button) {
        if (button.isChecked()) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, mSpeedDrawable, 0, 0);
            button.setTextColor(ContextCompat.getColor(getContext(),mSpeedTextColor));
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            button.setTextColor(ContextCompat.getColor(getContext(),R.color.alivc_common_font_white_light));
        }
    }

    private class MyLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private AliyunScreenMode lastLayoutMode = null;

        @Override
        public void onGlobalLayout() {
            if (mMainSpeedView.getVisibility() == VISIBLE) {

                //防止重复设置
                //Prevent repeated setting
                if (lastLayoutMode == mScreenMode) {
                    return;
                }

                setScreenMode(mScreenMode);
                lastLayoutMode = mScreenMode;
            }
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOnSpeedClickListener == null) {
                return;
            }

            if (view == mNormalBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Normal);
            } else if (view == mOneQrtTimeBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.OneQuartern);
            } else if (view == mOneHalfTimeBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.OneHalf);
            } else if (view == mTwoTimeBtn) {
                mOnSpeedClickListener.onSpeedClick(SpeedValue.Twice);
            }
        }

    };

    /**
     * 设置倍速点击事件
     *
     * @param l
     */
    /****
     * Set the speed click event
     *
     * @param l
     */
    public void setOnSpeedClickListener(OnSpeedClickListener l) {
        mOnSpeedClickListener = l;
    }

    /**
     * 设置当前屏幕模式。不同的模式，speedView的大小不一样
     *
     * @param screenMode
     */
    /****
     * Set the current screen mode. The size of the speed view varies according to the mode
     *
     * @param screenMode
     */
    public void setScreenMode(AliyunScreenMode screenMode) {
        ViewGroup.LayoutParams speedViewParam = mMainSpeedView.getLayoutParams();


        if (screenMode == AliyunScreenMode.Small) {
            //小屏的时候，是铺满整个播放器的
            //When it is small, it is filled up to the entire player
            speedViewParam.width = getWidth();
            speedViewParam.height = getHeight();
        } else if (screenMode == AliyunScreenMode.Full) {
            //如果是全屏的，就显示一半
            //If it is full screen, it shows half
            AliyunVodPlayerView parentView = (AliyunVodPlayerView) getParent();
            LockPortraitListener lockPortraitListener = parentView.getLockPortraitMode();
            if (lockPortraitListener == null) {
                //没有设置这个监听，说明不是固定模式，按正常的界面显示就OK
                //If there is no listener set, it means that it is not a fixed mode, so it is OK
                speedViewParam.width = getWidth() / 2;
            } else {
                speedViewParam.width = getWidth();
            }
            speedViewParam.height = getHeight();
        }

        mScreenMode = screenMode;
        mMainSpeedView.setLayoutParams(speedViewParam);
    }

    /**
     * 倍速监听
     */
    /****
     * speed listener
     */
    public interface OnSpeedClickListener {
        /**
         * 选中某个倍速
         *
         * @param value 倍速值
         */
        /****
         * Select a certain speed
         *
         * @param value Speed value
         */
        void onSpeedClick(SpeedValue value);

        /**
         * 倍速界面隐藏
         */
        /****
         * Speed interface hide
         */
        void onHide();
    }

    /**
     * 倍速值
     */
    /****
     * Speed value
     */
    public static enum SpeedValue {
        /**
         * 正常倍速
         */
        /****
         * Normal speed
         */
        Normal,
        /**
         * 1.25倍速
         */
        /****
         * 1.25x
         */
        OneQuartern,
        /**
         * 1.5倍速
         */
        /****
         * 1.5x
         */
        OneHalf,
        /**
         * 2倍速
         */
        /****
         * 2x
         */
        Twice
    }


    /**
     * 设置显示的倍速
     *
     * @param speedValue 倍速值
     */
    /****
     * Set the displayed speed
     *
     * @param speedValue Speed value
     */
    public void setSpeed(SpeedValue speedValue) {
        if (speedValue == null) {
            return;
        }

        if (mSpeedValue != speedValue) {
            mSpeedValue = speedValue;
            mSpeedChanged = true;
            updateSpeedCheck();
        } else {
            mSpeedChanged = false;
        }

        hide();

    }

    /**
     * 更新倍速选项的状态
     */
    /****
     * Update the status of the speed option
     */
    private void updateSpeedCheck() {
        mOneQrtTimeBtn.setChecked(mSpeedValue == SpeedValue.OneQuartern);
        mNormalBtn.setChecked(mSpeedValue == SpeedValue.Normal);
        mOneHalfTimeBtn.setChecked(mSpeedValue == SpeedValue.OneHalf);
        mTwoTimeBtn.setChecked(mSpeedValue == SpeedValue.Twice);

        updateBtnTheme();
    }

    /**
     * 更新选项的Theme
     */
    /****
     * Update the theme of the option
     */
    private void updateBtnTheme() {
        setRadioButtonTheme(mNormalBtn);
        setRadioButtonTheme(mOneQrtTimeBtn);
        setRadioButtonTheme(mOneHalfTimeBtn);
        setRadioButtonTheme(mTwoTimeBtn);
    }

    /**
     * 显示倍速view
     *
     * @param screenMode 屏幕模式
     */
    /****
     * Show the speed view
     *
     * @param screenMode Screen mode
     */
    public void show(AliyunScreenMode screenMode) {

        setScreenMode(screenMode);

        mMainSpeedView.startAnimation(showAnim);

    }

    /**
     * 隐藏
     */
    /****
     * Hide
     */
    private void hide() {
        if (mMainSpeedView.getVisibility() == VISIBLE) {
            mMainSpeedView.startAnimation(hideAnim);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //动画没有结束的时候，触摸是没有效果的
        //When the animation is not over, touching has no effect
        if (mMainSpeedView.getVisibility() == VISIBLE && animEnd) {
            hide();
            return true;
        }

        return super.onTouchEvent(event);
    }


}
