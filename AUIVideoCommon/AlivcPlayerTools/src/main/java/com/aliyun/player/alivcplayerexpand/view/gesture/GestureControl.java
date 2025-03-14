package com.aliyun.player.alivcplayerexpand.view.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.aliyun.player.alivcplayerexpand.util.ScreenUtils;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 播放控手势控制。通过对view的GestureDetector事件做监听，判断水平滑动还是垂直滑动。
 * 最后的结果通过{@link GestureView.GestureListener}返回出去。
 * 主要在{@link GestureView}中使用到此类。
 */
/**
 * Playback control gesture control. By listening to the view's GestureDetector event, it determines whether to slide horizontally or vertically.
 * The final result is returned via {@link GestureView.GestureListener}.
 * This class is mainly used in {@link GestureView}.
 */
public class GestureControl {

    private static final String TAG = GestureControl.class.getSimpleName();

    public Context mContext;
    /**
     * 播放控制层
     **/
    /****
     * Playback Control Layer
     */
    private View mGesturebleView;

    /**
     * 是否允许触摸 //TODO 可以删掉
     */
    /****
     * Whether to allow touch //TODO can be deleted
     */
    private boolean isGestureEnable = true;
    //是否水平
    //Horizontal or not
    private boolean isInHorizenalGesture = false;
    //是否右边垂直
    //Right vertical or not
    private boolean isInRightGesture = false;
    //是否左边垂直
    //Left vertical or not
    private boolean isInLeftGesture = false;

    //手势决定器
    //Gesture detector
    private GestureDetector mGestureDetector;
    //手势监听
    //Gesture listener
    private GestureView.GestureListener mGestureListener;
    //当前是否处于分屏模式
    //Current whether it is in split screen mode
    private boolean mIsMultiWindow;
    private View mView;

    /**
     * @param mContext
     * @param gestureView 播放控制层
     */
    /****
     * @param mContext
     * @param gestureView Playback Control Layer
     */
    public GestureControl(Context mContext, View gestureView) {
        this.mContext = mContext;
        this.mGesturebleView = gestureView;
        init();
    }

    public void setMultiWindow(boolean isMultiWindow) {
        this.mIsMultiWindow = isMultiWindow;
    }

    public void setView(View view){
        this.mView = view;
    }

    private void init() {

        mGestureDetector = new GestureDetector(mContext, mOnGestureListener);
        mGesturebleView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
//对结束事件的监听
                        //Listener to end events
                        if (mGestureListener != null) {
                            mGestureListener.onGestureEnd();
                        }

                        isInLeftGesture = false;
                        isInRightGesture = false;
                        isInHorizenalGesture = false;
                        break;

                    default:
                        break;
                }
//其他的事件交给GestureDetector。
                //Other events are passed to GestureDetector.
                return mGestureDetector.onTouchEvent(event);

            }
        });

        //GestureDetector增加双击事件的监听。。里面包含了单击事件
        //GestureDetector adds a listener for double-click events. It contains a listener for single-click events.
        mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //			处理点击事件
                //            Handle click events
                if (mGestureListener != null) {
                    mGestureListener.onSingleTap();
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mGestureListener != null) {
                    mGestureListener.onDoubleTap();
                }
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {

                return false;
            }
        });
    }


    /**
     * 开启关闭手势控制。
     * @param enable  开启
     */
    /****
     * Enable or disable gesture control.
     * @param enable  Enable
     */
    void enableGesture(boolean enable) {
        this.isGestureEnable = enable;
    }

    /**
     * 设置手势监听事件
     * @param mGestureListener 手势监听事件
     */
    /****
     * Set gesture listener event
     * @param mGestureListener Gesture listener event
     */
    void setOnGestureControlListener(GestureView.GestureListener mGestureListener) {
        this.mGestureListener = mGestureListener;
    }

    /**
     * 绑定到GestureDetector的。
     */
    /****
     * Bind to GestureDetector.
     */
    private final OnGestureListener mOnGestureListener = new OnGestureListener() {
        private float mXDown;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //如果关闭了手势。则不处理。
            //If the gesture is closed, it is not processed.
            if (!isGestureEnable || e1 == null || e2 == null) {
                return false;
            }

            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                //水平滑动
                //Horizontal sliding
                if (isInLeftGesture || isInRightGesture) {
                    //此前已经是竖直滑动了，不管
                    //This has been vertically slid before, no matter
                } else {
                    isInHorizenalGesture = true;
                }

            } else {
                //垂直滑动
                //Vertical sliding
                if (isInHorizenalGesture) {
                } else {

                }
            }

            if (isInHorizenalGesture) {
                if (mGestureListener != null) {
                    mGestureListener.onHorizontalDistance(e1.getX(), e2.getX());
                }
            } else {
                /*
                    如果是分屏模式,则根据当前展示的View的宽度来计算手势是处于右侧还是左侧
                 */
                /*
                    If it is split screen mode, then according to the width of the current displayed View to calculate the gesture is on the right or left side
                 */
                if (mIsMultiWindow) {
                    if (ScreenUtils.isInLeft(mView, (int) mXDown)) {
                        isInLeftGesture = true;
                        if (mGestureListener != null) {
                            mGestureListener.onLeftVerticalDistance(e1.getY(), e2.getY());
                        }
                    } else if (ScreenUtils.isInRight(mView, (int) mXDown)) {
                        isInRightGesture = true;
                        if (mGestureListener != null) {
                            mGestureListener.onRightVerticalDistance(e1.getY(), e2.getY());
                        }
                    }
                } else {
                    if (ScreenUtils.isInLeft(mContext, (int) mXDown)) {
                        isInLeftGesture = true;
                        if (mGestureListener != null) {
                            mGestureListener.onLeftVerticalDistance(e1.getY(), e2.getY());
                        }
                    } else if (ScreenUtils.isInRight(mContext, (int) mXDown)) {
                        isInRightGesture = true;
                        if (mGestureListener != null) {
                            mGestureListener.onRightVerticalDistance(e1.getY(), e2.getY());
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mGestureListener != null) {
                mGestureListener.onLongPress();
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            this.mXDown = e.getX();
            return true;
        }

    };

}
