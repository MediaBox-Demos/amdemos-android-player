package com.aliyun.player.alivcplayerexpand.view.control;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

import com.aliyun.player.alivcplayerexpand.util.DensityUtil;
import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.bean.DotBean;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.theme.ITheme;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;
import com.aliyun.player.alivcplayerexpand.util.FastClickUtil;
import com.aliyun.player.alivcplayerexpand.util.TimeFormater;
import com.aliyun.player.alivcplayerexpand.view.dot.VideoDotLayout;
import com.aliyun.player.alivcplayerexpand.view.function.AdvVideoView;
import com.aliyun.player.alivcplayerexpand.view.function.MutiSeekBarView;
import com.aliyun.player.alivcplayerexpand.view.interfaces.ViewAction;
import com.aliyun.player.alivcplayerexpand.view.quality.QualityItem;
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.TrackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 控制条界面。包括了顶部的标题栏，底部 的控制栏，锁屏按钮等等。是界面的主要组成部分。
 */
/**
 * The control bar interface. This includes the title bar at the top, the control bar at the bottom, the lock button, etc.
 * It is the main part of the interface.
 */
public class ControlView extends RelativeLayout implements ViewAction, ITheme {

    private static final String TAG = ControlView.class.getSimpleName();

    private static final int WHAT_HIDE = 0;
    private static final int DELAY_TIME = 5 * 1000; //5秒后隐藏 Hide after 5 seconds


    //标题，控制条单独控制是否可显示
    //Title, control bar can be individually controlled whether or not it can be displayed
    private boolean mTitleBarCanShow = true;
    private boolean mControlBarCanShow = true;
    private View mTitleBar;
    private View mControlBar;

    //这些是大小屏都有的==========START========
    //These are for both small and large screens ==========START========
    //返回按钮
    //back button
    private ImageView mTitlebarBackBtn;
    //标题
    //title
//    private TextView mTitlebarText;
    //视频播放状态
    //video play state
    private PlayState mPlayState = PlayState.NotPlaying;
    //播放按钮
    //play button
    private ImageView mPlayStateBtn,mPlayStateFullBtn;

    //锁定屏幕方向相关
    //Lock screen orientation related
    // 屏幕方向是否锁定
    //Screen orientation is or not locked
    private boolean mScreenLocked = false;
    //锁屏按钮
    //Screen lock button
    private ImageView mScreenLockBtn;


    //切换大小屏相关
    //Switch size screen related
    private AliyunScreenMode mAliyunScreenMode = AliyunScreenMode.Small;
    //全屏/小屏按钮
    //Full-screen/small-screen button
    private ImageView mScreenModeBtn,mScreenModeLargeImageView;

    //大小屏公用的信息
    //Common information for big and small screens
    //视频信息，info显示用。
    //Video information, used for info display.
    private MediaInfo mAliyunMediaInfo;
    //播放的进度
    //Playback progress
    private int mVideoPosition = 0;
    //带视频广告的视频的播放进度
    //Playback progress of videos with video ads
    private int mAdvVideoPosition = 0;
    //seekbar拖动状态
    //seekbar drag status
    private boolean isSeekbarTouching = false;
    //视频缓冲进度
    //Video buffer progress
    private int mVideoBufferPosition;
    //这些是大小屏都有的==========END========
    //These are for both small and large screens ==========END========


    //这些是大屏时显示的
    //These are for big screens
    //大屏的底部控制栏
    //Bottom control bar for big screens
    private View mLargeInfoBar;
    private View mFullScreenBottomLayout;
    //当前位置文字
    //Current position text
    private TextView mLargePositionText;
    //时长文字
    //Duration text
    private TextView mLargeDurationText;
    //进度条
    //Progress bar
    private SeekBar mLargeSeekbar;
    //当前的清晰度
    //Current quality
    private String mCurrentQuality;
    //是否固定清晰度
    //Fixed quality or not
    private boolean mForceQuality = false;
    //改变清晰度的按钮
    //Button to change quality
    private Button mLargeChangeQualityBtn;
    //更多弹窗按钮
    //More pop-up button
    private ImageView mTitleMore;
    private ImageView mFloatViewView;
    private ImageView mFullScreenFloatView;
    //这些是小屏时显示的
    //These are for small screens
    //底部控制栏
    //Bottom control bar
    private View mSmallInfoBar;
    //当前位置文字
    //Current position text
    private TextView mSmallPositionText;
    //时长文字
    //Duration text
    private TextView mSmallDurationText;
    //seek进度条
    //Seek progress bar
    private SeekBar mSmallSeekbar;
    //跑马灯是否开启
    //Is the marquee on?
    private boolean mMarqueeShow = false;
    //弹幕是否开启
    //Is danmu on?
    private boolean mDanmuShow = true;
    private boolean checkBoxInitCheck = false;
    //    private ImageView audioModeBg;
    private View audioModeBg;


    //整个view的显示控制：
    //The display control of the entire view:
    //不显示的原因。如果是错误的，那么view就都不显示了。
    //The reason for not being displayed. If it is an error, then the view will not be displayed.
    private HideType mHideType = null;

    //saas,还是mts资源,清晰度的显示不一样
    //Saas or MTS resource, the display of the quality is different
    private boolean isMtsSource;

    //各种监听
    //Various listeners
    // 进度拖动监听
    //Progress drag listener
    private OnSeekListener mOnSeekListener;
    //标题返回按钮监听
    //Title back button listener
    private OnBackClickListener mOnBackClickListener;
    //播放按钮点击监听
    //Play button click listener
    private OnPlayStateClickListener mOnPlayStateClickListener;
    //清晰度按钮点击监听
    //Quality button click listener
    private OnQualityBtnClickListener mOnQualityBtnClickListener;
    //锁屏按钮点击监听
    //Screen lock button click listener
    private OnScreenLockClickListener mOnScreenLockClickListener;
    //大小屏按钮点击监听
    //Screen size button click listener
    private OnScreenModeClickListener mOnScreenModeClickListener;
    // 显示更多
    //Show more
    private OnShowMoreClickListener mOnShowMoreClickListener;
    //屏幕截图
    //Screen shot
    private OnScreenShotClickListener mOnScreenShotClickListener;
    private OnVideoSpeedClickListener mOnVideoSpeedClickListener;
    //录制
    //Record
    private OnScreenRecoderClickListener mOnScreenRecoderClickListener;
    //输入弹幕
    //Input danmu
    private OnInputDanmakuClickListener mOnInputDanmakuClickListener;
    //投屏退出
    //Cast Screen Exit
    private OnDLNAControlListener mOnDLNAControlListener;
    //ContentView隐藏监听
    //Contentview hiding listener
    private OnControlViewHideListener mOnControlViewHideListener;
    //清晰度、码率、字幕、音轨点击事件
    //Event: Quality, bitrate, subtitle, audio track click
    private OnTrackInfoClickListener mOnTrackInfoClickListener;

    //视频广告时长
    //Video ad duration
    private long mAdvDuration;
    //原视频时长
    //Original video duration
    private long mSourceDuration;
    //视频广告需要添加的位置
    //Video ad position
    private MutiSeekBarView.AdvPosition mAdvPosition;
    //输入弹幕按钮
    //Input danmu button
    private View mInputDanmkuImageView;
    //广告视频总时长
    //Total duration of video ads
    private long mAdvTotalPosition;
    //当前播放器播放的是哪个视频
    //Current video playing by the player
    private AdvVideoView.VideoState mCurrentVideoState;
    private ImageView mScreenShot;
    private ImageView mScreenRecorder;
    private MutiSeekBarView mSmallMutiSeekbar;
//    private MutiSeekBarView mLargeMutiSeekbar;
    //投屏根布局
    //Root layout of the screen cast
    private FrameLayout mScreenCostFrameLayout;
    //投屏退出
    //Screen cast exit
    private TextView mScreenCostExitTextView;
    //是否是在投屏中
    //Is it in screen casting
    private boolean mInScreenCosting;
    //视频广告当前进度条的进度(包含广告视频的时长)
    //Video ad current progress bar progress (including the duration of the video ad)
    private int mMutiSeekBarCurrentProgress;
    //打点信息
    //Dot information
    private List<DotBean> mDotBean;
    private VideoDotLayout videoDotLayout;
    //视频时长
    //Video duration
    private int mMediaDuration;
    //底部，字幕，清晰度，码率等选项的rootView
    //RootView of the bottom, subtitle, quality, bitrate, etc.
    private Group mTrackLinearLayout;
    //字幕，清晰度，码率，音轨
    //Subtitle, quality, bitrate, audio track
    private TextView mAudioTextView, mBitrateTextView, mSubtitleTextView, mDefinitionTextView, mVideoSpeedTextView;
    //音轨流集合
    //Audio track stream collection
    private List<TrackInfo> mAudioTrackInfoList;
    //码率流集合
    //Bitrate stream collection
    private List<TrackInfo> mBitrateTrackInfoList;
    //清晰度流集合
    //Quality stream collection
    private List<TrackInfo> mDefinitionTrackInfoList;
    //字幕流集合
    //Subtitle stream collection
    private List<TrackInfo> mSubtitleTrackInfoList;
    private View mSeriesView;
    private CheckBox mDanmakuBtn;
    private ImageView mCastScreenBtn;
    private ImageView mAudioModeBtn;
    private boolean mCastScreenState = false;
    private boolean mAudioMode = false;
    private ShowType mShowType;
    private View mChangeCostDevicevBtn;
    private View mBackDetailBtn;
    private View mCostReplayView;
    private View mNextSeries;
    private View mTopShadow;
    private View mBottomShadow;
    private View mVideoSpeedTipView;
    private ConstraintLayout mSeekDurationTipLayout;
    private TextView mSeekPlayDuration;
    private TextView mVideoDurationSeek;
    private ProgressBar mPlayDurationProgress;

    public ControlView(Context context) {
        super(context);
        init();
    }

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Inflate布局
        //Inflate layout
        LayoutInflater.from(getContext()).inflate(R.layout.aui_custom_player_view_control, this, true);
        findAllViews(); //找到所有的view Find all views

        setViewListener(); //设置view的监听事件 Set the listener events of the view

        updateAllViews(); //更新view的显示 Update the display of the view
    }

    public void setUpConfig(boolean danmuShow) {
        this.mDanmuShow = danmuShow;
        updateDanmuBtn();
        if (!checkBoxInitCheck) {
            setDanmuCheckBoxListener();
            checkBoxInitCheck = true;
        }
    }


    private void findAllViews() {
        mTitleBar = findViewById(R.id.titlebar);
        mControlBar = findViewById(R.id.controlbar);
        mTrackLinearLayout = findViewById(R.id.ll_track);

        mSeriesView = findViewById(R.id.video_series_select_tv);

        mAudioTextView = findViewById(R.id.tv_audio);
        mBitrateTextView = findViewById(R.id.tv_bitrate);
        mSubtitleTextView = findViewById(R.id.tv_subtitle);
        mDefinitionTextView = findViewById(R.id.tv_definition);
        mVideoSpeedTextView = findViewById(R.id.video_speed);

        mTitlebarBackBtn = (ImageView) findViewById(R.id.alivc_title_back);
        mTitleMore = findViewById(R.id.alivc_title_more);
        mScreenModeLargeImageView = findViewById(R.id.alivc_screen_large_mode);
        mScreenModeBtn = (ImageView) findViewById(R.id.alivc_screen_mode);
        mScreenLockBtn = (ImageView) findViewById(R.id.alivc_screen_lock);
        mPlayStateBtn = (ImageView) findViewById(R.id.video_play_state_icon);
        mPlayStateFullBtn = findViewById(R.id.video_play_state_full_icon);
        mScreenShot = findViewById(R.id.alivc_screen_shot);
        mScreenRecorder = findViewById(R.id.alivc_screen_recoder);

        mLargeInfoBar = findViewById(R.id.alivc_info_large_bar);
        mFullScreenBottomLayout = findViewById(R.id.full_screen_bottom_function_layout);
        mLargePositionText = (TextView) findViewById(R.id.alivc_info_large_position);
        mLargeDurationText = (TextView) findViewById(R.id.alivc_info_large_duration);
        mLargeSeekbar = (SeekBar) findViewById(R.id.alivc_info_large_seekbar);
        mLargeChangeQualityBtn = (Button) findViewById(R.id.alivc_info_large_rate_btn);

        mSmallInfoBar = findViewById(R.id.alivc_info_small_bar);
        mSmallPositionText = (TextView) findViewById(R.id.alivc_info_small_position);
        mSmallDurationText = (TextView) findViewById(R.id.alivc_info_small_duration);
        mSmallSeekbar = (SeekBar) findViewById(R.id.alivc_info_small_seekbar);

        mSmallMutiSeekbar = findViewById(R.id.alivc_info_small_mutiseekbar);
//        mLargeMutiSeekbar = findViewById(R.id.alivc_info_large_mutiseekbar);

        mInputDanmkuImageView = findViewById(R.id.iv_input_danmaku);

        mScreenCostFrameLayout = findViewById(R.id.screen_cost_fl);
        mScreenCostExitTextView = findViewById(R.id.tv_exit);
        mFloatViewView = findViewById(R.id.half_float_video);
        mFullScreenFloatView = findViewById(R.id.full_float_video);
        mDanmakuBtn = findViewById(R.id.danmaku_btn);
        mCastScreenBtn = findViewById(R.id.alivc_screen_cost);
        mAudioModeBtn = findViewById(R.id.alivc_audio_mode);
        audioModeBg = findViewById(R.id.audio_mode_bg);

        mChangeCostDevicevBtn = findViewById(R.id.tv_screen_cost_change_device);
        mBackDetailBtn = findViewById(R.id.tv_back_detail);
        mCostReplayView = findViewById(R.id.tv_restart);
        videoDotLayout = findViewById(R.id.video_dots_layout);
        mNextSeries = findViewById(R.id.next_video);
        mTopShadow = findViewById(R.id.top_shadow);
        mBottomShadow = findViewById(R.id.bottom_shadow);
        mVideoSpeedTipView = findViewById(R.id.video_speed_up_tip);

        //center seekBar
        mSeekDurationTipLayout = findViewById(R.id.seek_duration_tip_layout);
        mSeekPlayDuration = findViewById(R.id.seek_play_duration);
        mVideoDurationSeek = findViewById(R.id.seek_video_duration);
        mPlayDurationProgress = findViewById(R.id.seek_progress_play_duration);
    }

    private void setViewListener() {
        //标题的返回按钮监听
        //Title back button listener
        mTitlebarBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBackClickListener != null) {
                    mOnBackClickListener.onClick();
                }
            }
        });

        //控制栏的播放按钮监听
        //play button of control bar listener
        mPlayStateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPlayStateClickListener != null) {
                    mOnPlayStateClickListener.onPlayStateClick();
                }
            }
        });
        mPlayStateFullBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnPlayStateClickListener != null) {
                    mOnPlayStateClickListener.onPlayStateClick();
                }
            }
        });
        //锁屏按钮监听
        //Screen lock button listener
        mScreenLockBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnScreenLockClickListener != null) {
                    mOnScreenLockClickListener.onClick();
                }
            }
        });

        // 截图按钮监听
        //Screen shot button listener
        mScreenShot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mOnScreenShotClickListener != null) {
                    mOnScreenShotClickListener.onScreenShotClick();
                }
            }
        });

        // 录制按钮监听
        //Screen recoder button listener
        mScreenRecorder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnScreenRecoderClickListener != null && (!GlobalPlayerConfig.IS_TRAILER
                        && mVideoPosition < AliyunVodPlayerView.TRAILER)) {
                    mOnScreenRecoderClickListener.onScreenRecoderClick();
                }
            }
        });

        //大小屏按钮监听
        //Screen mode button listener
        mScreenModeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnScreenModeClickListener != null) {
                    mOnScreenModeClickListener.onClick();
                }
            }
        });

        mScreenModeLargeImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnScreenModeClickListener != null) {
                    mOnScreenModeClickListener.onClick();
                }
            }
        });

        //投屏退出
        //Screen cast exit
        mScreenCostExitTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnDLNAControlListener != null) {
                    mOnDLNAControlListener.onExit();
                }
            }
        });
        //音轨
        //Audio track
        mAudioTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnTrackInfoClickListener != null) {
                    mOnTrackInfoClickListener.onAudioClick(mAudioTrackInfoList);
                }
            }
        });
        //码率
        //Bitrate
        mBitrateTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnTrackInfoClickListener != null) {
                    mOnTrackInfoClickListener.onBitrateClick(mBitrateTrackInfoList);
                }
            }
        });
        //字幕
        //Subtitle
        mSubtitleTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnTrackInfoClickListener != null) {
                    mOnTrackInfoClickListener.onSubtitleClick(mSubtitleTrackInfoList);
                }
            }
        });
        //清晰度
        //Quality
        mDefinitionTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(HideType.Normal);
                if (mOnTrackInfoClickListener != null) {
                    mOnTrackInfoClickListener.onDefinitionClick(mDefinitionTrackInfoList);
                }
            }
        });

        //速度
        //Video speed
        mVideoSpeedTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnVideoSpeedClickListener != null) {
                    mOnVideoSpeedClickListener.onVideoSpeedClick();
                }
                if (!mAudioMode) {
                    hide(HideType.Normal);
                }
            }
        });

        //seekbar的滑动监听
        //Slide listener for seekbar
        final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    isSeekbarTouching = true;
                    //这里是用户拖动，直接设置文字进度就行，
                    // Here is the user dragging, just set the text progress directly.
                    // 无需去updateAllViews() ， 因为不影响其他的界面。
                    // No need to updateAllViews(), because it does not affect other interfaces.
                    if (mAliyunScreenMode == AliyunScreenMode.Full) {
                        //全屏状态.
                        //Full screen.
                        mLargePositionText.setText(TimeFormater.formatMs(progress));
                    } else if (mAliyunScreenMode == AliyunScreenMode.Small) {
                        //小屏状态
                        //Small screen
                        mSmallPositionText.setText(TimeFormater.formatMs(progress));
                    }
                    if (mOnSeekListener != null) {
                        mOnSeekListener.onProgressChanged(progress);
                    }
                    onSeeking(seekBar,progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarTouching = true;

                mMutiSeekBarCurrentProgress = seekBar.getProgress();
                mHideHandler.removeMessages(WHAT_HIDE);
                if (mOnSeekListener != null) {
                    mOnSeekListener.onSeekStart(seekBar.getProgress());
                }
                if (mAliyunScreenMode == AliyunScreenMode.Small) {
                    mSmallInfoBar.setVisibility(View.VISIBLE);
                } else {
                    mFullScreenBottomLayout.setVisibility(View.VISIBLE);
                    mLargeInfoBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnSeekListener != null) {
                    mOnSeekListener.onSeekEnd(seekBar.getProgress());
                }
                if(mSeekDurationTipLayout.isShown()){
                    mSeekDurationTipLayout.setVisibility(View.GONE);
                }
                isSeekbarTouching = false;
                mHideHandler.removeMessages(WHAT_HIDE);
                mHideHandler.sendEmptyMessageDelayed(WHAT_HIDE, DELAY_TIME);
            }
        };
        //seekbar的滑动监听
        //Slide listener for seekbar
        mLargeSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        mSmallSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
//        mLargeMutiSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        mSmallMutiSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        //全屏下的切换分辨率按钮监听
        //Switch resolution button listener for full screen
        mLargeChangeQualityBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //点击切换分辨率 显示分辨率的对话框
                //Click to switch resolution, show the resolution dialog
                if (mOnQualityBtnClickListener != null && mAliyunMediaInfo != null) {
                    List<TrackInfo> qualityTrackInfos = new ArrayList<>();
                    List<TrackInfo> trackInfos = mAliyunMediaInfo.getTrackInfos();
                    for (TrackInfo trackInfo : trackInfos) {
                        //清晰度
                        if (trackInfo.getType() == TrackInfo.Type.TYPE_VOD) {
                            qualityTrackInfos.add(trackInfo);
                        }
                    }
                    mOnQualityBtnClickListener.onQualityBtnClick(v, qualityTrackInfos, mCurrentQuality);
                }
                hide(HideType.Normal);
            }
        });

        // 更多按钮点击监听
        //More button click listener
        mTitleMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnShowMoreClickListener != null) {
                    mOnShowMoreClickListener.showMore();
                }
                hide(HideType.Normal);
            }
        });

        //输入弹幕按钮点击事件
        //Input danmaku button click event
        mInputDanmkuImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnInputDanmakuClickListener != null) {
                    mOnInputDanmakuClickListener.onInputDanmakuClick();
                }
            }
        });
        mFloatViewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFloatPlayViewClickListener != null) {
                    mOnFloatPlayViewClickListener.onFloatViewPlayClick();
                }
                hide(HideType.Normal);
            }
        });
        mFullScreenFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFloatPlayViewClickListener != null) {
                    mOnFloatPlayViewClickListener.onFloatViewPlayClick();
                }
                hide(HideType.Normal);
            }
        });

        mSeriesView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAudioMode) {
                    hide(HideType.Normal);
                }
                if (mOnSelectSeriesClickListener != null) {
                    mOnSelectSeriesClickListener.onSelectVideo();
                }

            }
        });
        //投屏
        //cast
        mCastScreenBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCastScreenBtnClick();
            }
        });
        //音频模式
        //Audio mode
        mAudioModeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAudioModeChangeListener != null) {
                    mAudioMode = !mAudioMode;
                    onAudioModeChangeListener.onAudioMode(mAudioMode);
                    updateAudioModeImage();
                }
            }
        });
        //投屏更换设备
        //Cast screen change device
        mChangeCostDevicevBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCastScreenBtnClick();
            }
        });

        //投屏返回详情页
        //Cast screen back detail
        mBackDetailBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBackClickListener != null) {
                    mOnBackClickListener.onClick();
                }
            }
        });
        //投屏重播
        //Cast screen replay
        mCostReplayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPlayStateClickListener != null) {
                    mOnPlayStateClickListener.onRePlayClick();
                }
            }
        });
        videoDotLayout.setOnVideoDotPlayClick(new VideoDotLayout.OnVideoDotPlayClick() {
            @Override
            public void onDotPlayClick(int seekSeconds) {
                updateSeekProgress(seekSeconds * 1000);
            }
        });
        mNextSeries.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNextSeriesClick != null) {
                    onNextSeriesClick.onNextSeries();
                }
            }
        });
    }

    private void setDanmuCheckBoxListener() {
        //弹幕开关
        //Danmaku switch
        mDanmakuBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDanmuShow = isChecked;
                if (onDamkuOpenListener != null) {
                    onDamkuOpenListener.onDamkuOpen(mDanmuShow);
                }
                String content;
                if (isChecked) content = getContext().getString(R.string.danmku_open_tip);
                else content = getContext().getString(R.string.danmku_close_tip);
                Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                updateDanmuBtn();
            }
        });
    }

    private void onCastScreenBtnClick() {
        if (onCastScreenListener != null) {
            mCastScreenState = !mCastScreenState;
            onCastScreenListener.onCastScreen(mCastScreenState);
//            updateCastImage();
        }
    }

    private void onSeeking(SeekBar seekBar,long progress){
        mSeekDurationTipLayout.setVisibility(View.VISIBLE);
        long videoDurationSeconds = mMediaDuration / 1000;
        long playDurationText = (long) (videoDurationSeconds * (progress * 1.0f/ mMediaDuration));
        mSeekPlayDuration.setText(DateUtils.formatElapsedTime(playDurationText));
        mVideoDurationSeek.setText("/"+DateUtils.formatElapsedTime(videoDurationSeconds));
        mPlayDurationProgress.setMax(mMediaDuration);
        mPlayDurationProgress.setProgress((int) progress);

        //隐藏play 按钮
        //Hide play button
        mPlayStateBtn.setVisibility(View.GONE);
        seekBar.setVisibility(View.VISIBLE);
    }


    private void updateSeekProgress(int progress) {
        mSmallSeekbar.setProgress(progress);
        mSmallMutiSeekbar.setProgress(progress);
        mLargeSeekbar.setProgress(progress);
//        mLargeMutiSeekbar.setProgress(progress);
        if (mOnSeekListener != null) {
            mOnSeekListener.onSeekEnd(progress);
        }
    }

    public void setAudioMode(boolean audioMode) {
        Log.i(TAG, "setAudioMode audioMode:" + audioMode);
        this.mAudioMode = audioMode;
        updateAudioModeImage();
        if (audioMode) {
            audioModeBg.setVisibility(VISIBLE);
            //全屏模式下 隐藏
            //Hide in full screen
            if (mAliyunScreenMode == AliyunScreenMode.Full) {
                showDanmukuView(false);
                mDefinitionTextView.setVisibility(GONE);
                mScreenLockBtn.setVisibility(GONE);
            }
        } else {
            audioModeBg.setVisibility(GONE);
            if (mAliyunScreenMode == AliyunScreenMode.Full) {
                showDanmukuView(true);
                mDefinitionTextView.setVisibility(VISIBLE);
                mScreenLockBtn.setVisibility(VISIBLE);
            }
        }
    }

    private void updateCastImage() {
        mCastScreenBtn.setVisibility(View.GONE);
    }

    private void updateAudioModeImage() {
        if (mAudioMode) {
            mAudioModeBtn.setImageResource(R.drawable.audio_state_open);
        } else {
            mAudioModeBtn.setImageResource(R.drawable.audio_state_close);
        }
        if (mAliyunScreenMode == AliyunScreenMode.Small || mScreenLocked || mInScreenCosting) {
            mAudioModeBtn.setVisibility(INVISIBLE);
        } else {
            if (mAliyunScreenMode == AliyunScreenMode.Full) {
                if (mShowType == ShowType.AudioMode) {
//                    mTitleMore.setVisibility(GONE);
                    mAudioModeBtn.setVisibility(VISIBLE);
                } else {
//                    mTitleMore.setVisibility(VISIBLE);
                    mAudioModeBtn.setVisibility(VISIBLE);
                }
            }
        }
    }

    private void showDanmukuView(boolean show) {
//        if (show) {
//            mDanmakuBtn.setVisibility(VISIBLE);
//            mInputDanmkuImageView.setVisibility(View.VISIBLE);
//        } else {
//            mDanmakuBtn.setVisibility(GONE);
//            mInputDanmkuImageView.setVisibility(View.GONE);
//        }
        mDanmakuBtn.setVisibility(GONE);
        mInputDanmkuImageView.setVisibility(View.GONE);
    }

    private void updateDanmuBtn() {
    }

    /**
     * 是不是MTS的源 //MTS的清晰度显示与其他的不太一样，所以这里需要加一个作为区分
     *
     * @param isMts true:是。false:不是
     */
    /****
     * Is not the source of MTS // MTS clarity display is not quite the same as the others, so here need to add one as a distinction
     /* @param isMts true:yes. false:no.
     /* @param isMts true:yes. false:no.
     */
    public void setIsMtsSource(boolean isMts) {
        isMtsSource = isMts;
    }

    /**
     * 设置当前播放的清晰度
     *
     * @param currentQuality 当前清晰度
     */
    /****
     * Set the current clarity of playback
     *
     * @param currentQuality Current clarity
     */
    public void setCurrentQuality(String currentQuality) {
        mCurrentQuality = currentQuality;
        updateLargeInfoBar();
    }

    /**
     * 设置是否强制清晰度。如果是强制，则不会显示切换清晰度按钮
     *
     * @param forceQuality true：是
     */
    /****
     * Set whether to force clarity. If it is forced, it will not display the switch clarity button
     *
     * @param forceQuality true:yes
     */
    public void setForceQuality(boolean forceQuality) {
        mForceQuality = forceQuality;
    }

    /**
     * 设置是否显示标题栏。
     *
     * @param show false:不显示
     */
    /****
     * Set whether to show the title bar.
     *
     * @param show false:not show
     */
    public void setTitleBarCanShow(boolean show) {
        mTitleBarCanShow = show;
        updateAllTitleBar();
    }

    /**
     * 设置是否显示控制栏
     *
     * @param show fase：不显示
     */
    /****
     * Set whether to show the control bar
     *
     * @param show fase:not show
     */
    public void setControlBarCanShow(boolean show) {
        mControlBarCanShow = show;
        updateAllControlBar();
    }

    /**
     * 设置当前屏幕模式：全屏还是小屏
     *
     * @param mode {@link AliyunScreenMode#Small}：小屏. {@link AliyunScreenMode#Full}:全屏
     */
    /****
     * Set the current screen mode: full screen or small screen
     *
     * @param mode {@link AliyunScreenMode#Small}:small screen. {@link AliyunScreenMode#Full}:full screen
     */
    @Override
    public void setScreenModeStatus(AliyunScreenMode mode) {
        mAliyunScreenMode = mode;
        updateTitleBackBtn();
        updateLargeInfoBar();
        updateSmallInfoBar();
        updateScreenLockBtn();
        updateScreenModeBtn();
        setTheme(Theme.Blue);
        updateShowMoreBtn();
        updatePIPIcon();
        updateScreenShotBtn();
        updateScreenRecorderBtn();
        updateInputDanmakuView();
        updateDotView();
        updateCostBackDetailView();
        if (mode == AliyunScreenMode.Full) {
            updateTrackInfoView();
        }
        updatePlayIcon();
        updateShadowBgLayout();
        updateCastImage();
        updateAudioModeImage();
    }

    /**
     * 屏幕旋转隐藏返回详情按钮
     */
    /****
     * Screen rotation hide back detail button
     */
    private void updateCostBackDetailView() {
        if (mAliyunScreenMode == AliyunScreenMode.Full) {
            if (mBackDetailBtn != null) {
                mBackDetailBtn.setVisibility(VISIBLE);
            }
        } else if (mAliyunScreenMode == AliyunScreenMode.Small) {
            if (mBackDetailBtn != null) {
                mBackDetailBtn.setVisibility(GONE);
            }
        }
    }

    /**
     * 更新投屏UI
     */
    /****
     * Update the screen cast UI
     */
    private void updateCostReplayView(boolean isPlayComplete) {
        if (isPlayComplete) {
            if (mCostReplayView != null) {
                mCostReplayView.setVisibility(VISIBLE);
            }
        } else {
            if (mCostReplayView != null) {
                mCostReplayView.setVisibility(GONE);
            }
        }
    }

    /**
     * 更改播放icon 的大小
     */
    /****
     * Change the size of the playing icon
     */
    private void updatePlayIcon() {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mPlayStateBtn.getLayoutParams();
        if (mAliyunScreenMode == AliyunScreenMode.Full) {
            lp.height = DensityUtil.dip2px(getContext(), 45);
            lp.width = DensityUtil.dip2px(getContext(), 43);
        } else {
            lp.height = DensityUtil.dip2px(getContext(), 25);
            lp.width = DensityUtil.dip2px(getContext(), 20);
        }
    }

    private void updateShadowBgLayout() {
        ViewGroup.LayoutParams topShadowLp = (ViewGroup.LayoutParams) mTopShadow.getLayoutParams();
        ViewGroup.LayoutParams bottomShadowLp = (ViewGroup.LayoutParams) mBottomShadow.getLayoutParams();
        if (mAliyunScreenMode == AliyunScreenMode.Full) {
            topShadowLp.height = DensityUtil.dip2px(getContext(), 84);
            bottomShadowLp.height = DensityUtil.dip2px(getContext(), 84);
        } else {
            topShadowLp.height = DensityUtil.dip2px(getContext(), 42);
            bottomShadowLp.height = DensityUtil.dip2px(getContext(), 42);
        }
    }

    /**
     * 更新录屏按钮的显示和隐藏
     */
    /****
     * Update the display and hiding of the recording button
     */
    private void updateScreenRecorderBtn() {
        mScreenRecorder.setVisibility(GONE);
    }

    /**
     * 更新截图按钮的显示和隐藏
     */
    /****
     * Update the display and hiding of the screenshot button
     */
    private void updateScreenShotBtn() {
        if (mAliyunScreenMode == AliyunScreenMode.Small || mScreenLocked || mInScreenCosting) {
            mScreenShot.setVisibility(INVISIBLE);
        } else {
            if (mAliyunScreenMode == AliyunScreenMode.Full) {
//                mScreenShot.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 更新更多按钮的显示和隐藏
     */
    /****
     * Update the display and hiding of the more button
     */
    private void updateShowMoreBtn() {
        mTitleMore.setVisibility(GONE);
    }

    private void updatePIPIcon() {
        mFullScreenFloatView.setVisibility(GONE);
        mFloatViewView.setVisibility(GONE);
    }

    /**
     * 更新弹幕输入按钮的展示和隐藏
     */
    /****
     * Update the display and hiding of the danmaku input button
     */
    private void updateInputDanmakuView() {
        showDanmukuView(mShowType != ShowType.AudioMode);
    }

    /**
     * 更新打点信息
     */
    /****
     * Update the dot information
     */
    private void updateDotView() {
        if (mAliyunScreenMode == AliyunScreenMode.Full && mLargeSeekbar != null) {
            mLargeSeekbar.post(new Runnable() {
                @Override
                public void run() {
                    int measuredWidth = mLargeSeekbar.getMeasuredWidth();
                    if (measuredWidth != 0 && mLargeSeekbar.isShown()) {
                        initDotView();
                        updateDotLayout();
                    }
                }
            });
        } else if (mAliyunScreenMode == AliyunScreenMode.Small) {
            updateDotLayout();
        }
    }

    /**
     * 设置主题色
     *
     * @param theme 支持的主题
     */
    /****
     * Set the theme color
     *
     * @param theme Supported theme
     */
    @Override
    public void setTheme(Theme theme) {
        updateSeekBarTheme(theme);
    }

    /**
     * 设置当前的播放状态
     *
     * @param playState 播放状态
     */
    /****
     * Set the current playing status
     *
     * @param playState Playing status
     */
    public void setPlayState(PlayState playState) {
        mPlayState = playState;
        updatePlayStateBtn();
    }

    /**
     * 设置是否可以seek
     */
    /****
     * Set whether to seek
     */
    public void setOtherEnable(boolean enable) {
        if (mSmallSeekbar != null) {
            mSmallSeekbar.setEnabled(enable);
        }
        if (mLargeSeekbar != null) {
            mLargeSeekbar.setEnabled(enable);
        }
        if (mPlayStateBtn != null) {
            mPlayStateBtn.setEnabled(enable);
        }
        if(mPlayStateFullBtn != null){
            mPlayStateFullBtn.setEnabled(enable);
        }
        if (mScreenLockBtn != null) {
            mScreenLockBtn.setEnabled(enable);
        }
        if (mLargeChangeQualityBtn != null) {
            mLargeChangeQualityBtn.setEnabled(enable);
        }
        if (mTitleMore != null) {
            mTitleMore.setEnabled(enable);
        }
        if (mInputDanmkuImageView != null) {
            mInputDanmkuImageView.setEnabled(enable);
        }
    }

    /**
     * 设置视频信息
     *
     * @param aliyunMediaInfo 媒体信息
     * @param currentQuality  当前清晰度
     */
    /****
     * Set video information
     *
     * @param aliyunMediaInfo Media information
     * @param currentQuality  Current clarity
     */
    public void setMediaInfo(MediaInfo aliyunMediaInfo, String currentQuality) {
        mAliyunMediaInfo = aliyunMediaInfo;
        mMediaDuration = mAliyunMediaInfo.getDuration();
        mCurrentQuality = currentQuality;
        updateLargeInfoBar();
    }

    public void setMediaInfo(MediaInfo aliyunMediaInfo) {
        mAliyunMediaInfo = aliyunMediaInfo;
    }

    public void setMediaDuration(int duration) {
        this.mMediaDuration = duration;
        updateLargeInfoBar();
        updateSmallInfoBar();
    }


    public void showMoreButton() {
//        mTitleMore.setVisibility(VISIBLE);
    }

    public void hideMoreButton() {
        mTitleMore.setVisibility(GONE);
    }

    /**
     * 设置是否是投屏中标记
     */
    /****
     * Set whether it is a screening mark
     */
    public void setInScreenCosting(boolean isScreenCosting) {
        this.mInScreenCosting = isScreenCosting;
    }

    /**
     * 根据TrackInfo.Type 获取对应的TrackInfo集合
     */
    /****
     * According to TrackInfo.Type get the corresponding TrackInfo collection
     */
    private List<TrackInfo> getTrackInfoListWithTrackInfoType(TrackInfo.Type trackInfoType) {
        List<TrackInfo> trackInfoList = new ArrayList<>();
        if (mAliyunMediaInfo != null && mAliyunMediaInfo.getTrackInfos() != null) {
            for (TrackInfo trackInfo : mAliyunMediaInfo.getTrackInfos()) {
                TrackInfo.Type type = trackInfo.getType();
                if (type == trackInfoType) {

                    if (trackInfoType == TrackInfo.Type.TYPE_SUBTITLE) {
                        //字幕
                        //subtitle
                        if (!TextUtils.isEmpty(trackInfo.getSubtitleLang())) {
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_AUDIO) {
                        //音轨
                        //audio track
                        if (!TextUtils.isEmpty(trackInfo.getAudioLang())) {
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_VIDEO) {
                        //码率
                        //video bitrate
                        if (trackInfo.getVideoBitrate() > 0) {
                            if (trackInfoList.size() == 0) {
                                //添加自动码率
                                //add auto bitrate
                                trackInfoList.add(trackInfo);
                            }
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_VOD) {
                        //清晰度
                        //definition
                        if (!TextUtils.isEmpty(trackInfo.getVodDefinition())) {
                            trackInfoList.add(trackInfo);
                        }
                    }

                }
            }
        }
        return trackInfoList;
    }

    /**
     * 更新当前主题色
     *
     * @param theme 设置的主题色
     */
    /****
     * Update the current theme color
     *
     * @param theme Set theme color
     */
    private void updateSeekBarTheme(Theme theme) {
        //获取不同主题的图片
        //Get images with different themes
        int progressDrawableResId = R.drawable.alivc_info_seekbar_bg_blue;
        int thumbResId = R.drawable.alivc_info_seekbar_thumb_blue;
        int largeThumbResId = R.drawable.shape_circle_video_full_screen_seek_bar;
        if (theme == Theme.Blue) {
            progressDrawableResId = (R.drawable.alivc_info_seekbar_bg_blue);
            largeThumbResId = R.drawable.shape_circle_video_full_screen_seek_bar;
            thumbResId = R.drawable.shape_circle_video_seek_bar;
        } else if (theme == Theme.Green) {
            progressDrawableResId = (R.drawable.alivc_info_seekbar_bg_green);
            thumbResId = (R.drawable.alivc_info_seekbar_thumb_green);
            largeThumbResId = (R.drawable.alivc_info_seekbar_thumb_green);
        } else if (theme == Theme.Orange) {
            progressDrawableResId = (R.drawable.alivc_info_seekbar_bg_orange);
            thumbResId = (R.drawable.alivc_info_seekbar_thumb_orange);
            largeThumbResId = (R.drawable.alivc_info_seekbar_thumb_orange);
        } else if (theme == Theme.Red) {
            progressDrawableResId = (R.drawable.alivc_info_seekbar_bg_red);
            thumbResId = (R.drawable.alivc_info_seekbar_thumb_red);
            largeThumbResId = (R.drawable.alivc_info_seekbar_thumb_red);
        }

        //这个很有意思。。哈哈。不同的seekbar不能用同一个drawable，不然会出问题。
        //That's interesting. Haha. You can't use the same drawable for different seekbars or something will go wrong.
        // https://stackoverflow.com/questions/12579910/seekbar-thumb-position-not-equals-progress

        //设置到对应控件中
        //Set to the corresponding control
        Resources resources = getResources();
        Drawable smallProgressDrawable = ContextCompat.getDrawable(getContext(), progressDrawableResId);
        Drawable smallThumb = ContextCompat.getDrawable(getContext(), thumbResId);
        mSmallSeekbar.setProgressDrawable(smallProgressDrawable);
        mSmallSeekbar.setThumb(smallThumb);
        Drawable smallMutiThumb = ContextCompat.getDrawable(getContext(), thumbResId);
        mSmallMutiSeekbar.setThumb(smallMutiThumb);

        Drawable largeProgressDrawable = ContextCompat.getDrawable(getContext(), progressDrawableResId);
        Drawable largeThumb = ContextCompat.getDrawable(getContext(), largeThumbResId);
        mLargeSeekbar.setProgressDrawable(largeProgressDrawable);
        mLargeSeekbar.setThumb(largeThumb);
//        mLargeMutiSeekbar.setThumb(largeThumb);
    }

    /**
     * 是否锁屏。锁住的话，其他的操作界面将不会显示。
     *
     * @param screenLocked true：锁屏
     */
    /****
     * Determine whether to lock the screen. If it is locked, other operations will not be displayed.
     *
     * @param screenLocked True: lock screen
     */
    public void setScreenLockStatus(boolean screenLocked) {
        mScreenLocked = screenLocked;
        updateScreenLockBtn();
        updateAllTitleBar();
        updateAllControlBar();
        updateShowMoreBtn();
        updateScreenShotBtn();
        updateScreenRecorderBtn();
    }

    /**
     * 更新视频进度
     *
     * @param position 位置，ms
     */
    /****
     * Update video progress
     *
     * @param position Position, ms
     */
    public void setVideoPosition(int position) {
        mVideoPosition = position;
        judgeCurrentPlayingVideo();
        updateSmallInfoBar();
        updateLargeInfoBar();
    }


    /**
     * 判断当前正在播放的视频
     */
    /****
     * Determine the current video being played
     */
    private void judgeCurrentPlayingVideo() {
        if (mAdvPosition == null || (!GlobalPlayerConfig.IS_VIDEO)) {
            return;
        }
        switch (mAdvPosition) {
            case ONLY_START:
                if (isVideoPositionInStart(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            case ONLY_MIDDLE:
                if (isVideoPositionInMiddle(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            case ONLY_END:
                if (isVideoPositionInEnd(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            case START_END:
                if (isVideoPositionInStart(mAdvVideoPosition) || isVideoPositionInEnd(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            case MIDDLE_END:
                if (isVideoPositionInMiddle(mAdvVideoPosition) || isVideoPositionInEnd(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            case START_MIDDLE:
                if (isVideoPositionInStart(mAdvVideoPosition) || isVideoPositionInMiddle(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            case ALL:
                if (isVideoPositionInStart(mAdvVideoPosition)
                        || isVideoPositionInMiddle(mAdvVideoPosition)
                        || isVideoPositionInEnd(mAdvVideoPosition)) {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_ADV;
                } else {
                    mCurrentVideoState = AdvVideoView.VideoState.VIDEO_SOURCE;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 针对于视频广告,seek后获取开始播放的位置
     */
    /****
     * For video ad, seek after getting the start playing position
     */
    public long afterSeekPlayStartPosition(long seekToPosition) {
        if (mAliyunScreenMode == AliyunScreenMode.Small && mSmallMutiSeekbar != null) {
            return mSmallMutiSeekbar.startPlayPosition(seekToPosition);
        }
//        else if (mAliyunScreenMode == AliyunScreenMode.Full && mLargeMutiSeekbar != null) {
//            return mLargeMutiSeekbar.startPlayPosition(seekToPosition);
//        }
        else {
            return seekToPosition;
        }
    }

    public AdvVideoView.IntentPlayVideo getIntentPlayVideo(int currentPosition, int seekToPosition) {
        if (mAliyunScreenMode == AliyunScreenMode.Small && mSmallMutiSeekbar != null) {
            return mSmallMutiSeekbar.getIntentPlayVideo(currentPosition, seekToPosition);
        }
//        else if (mAliyunScreenMode == AliyunScreenMode.Full && mLargeMutiSeekbar != null) {
//            return mLargeMutiSeekbar.getIntentPlayVideo(currentPosition, seekToPosition);
//        }
        else {
            return AdvVideoView.IntentPlayVideo.NORMAL;
        }
    }

    /**
     * 获取当前播放器的状态
     */
    /****
     * Get the current status of the player
     */
    public AdvVideoView.VideoState getCurrentVideoState() {
        return mCurrentVideoState;
    }

    /**
     * 判断当前播放进度是否在起始广告位置
     */
    /****
     * Determine whether the current playback progress is in the start ad position
     */
    private boolean isVideoPositionInStart(int mVideoPosition) {
        return mVideoPosition >= 0 && mVideoPosition <= mAdvDuration;
    }

    /**
     * 判断当前播放进度是否在中间广告位置
     */
    /****
     * Determine whether the current playback progress is in the middle ad position
     */
    private boolean isVideoPositionInMiddle(int mVideoPosition) {
        if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            return (mVideoPosition >= mSourceDuration / 2 + mAdvDuration) && (mVideoPosition <= mSourceDuration / 2 + mAdvDuration * 2);
        } else if (mAdvPosition == MutiSeekBarView.AdvPosition.START_END || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_START || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_END) {
            return false;
        } else {
            //ONLY_MIDDLE,MIDDLE_END
            return mVideoPosition >= mSourceDuration / 2 && mVideoPosition <= mSourceDuration / 2 + mAdvDuration;
        }
    }

    /**
     * 判断当前播放进度是否在结束广告位置
     */
    /****
     * Determine whether the current playback progress is in the end ad position
     */
    private boolean isVideoPositionInEnd(int mVideoPosition) {
        if (mAdvPosition == MutiSeekBarView.AdvPosition.ALL
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_MIDDLE) {
            return mVideoPosition >= mSourceDuration + mAdvDuration * 2;
        } else if (mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_START
                || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_MIDDLE
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_END
                || mAdvPosition == MutiSeekBarView.AdvPosition.MIDDLE_END) {
            return mVideoPosition >= mSourceDuration + mAdvDuration;
        } else {
            return mVideoPosition >= mSourceDuration;
        }
    }

    /**
     * 更新视频进度
     *
     * @param advTotalPosition 广告视频位置 ms
     * @param sourcePosition   原视频位置 ms
     */
    /****
     * Update video progress
     *
     * @param advTotalPosition Ad video position, ms
     * @param sourcePosition   Source video position, ms
     */
    public void setAdvVideoPosition(int advTotalPosition, int sourcePosition) {
        mAdvVideoPosition = advTotalPosition;
        mVideoPosition = sourcePosition;
        updateSmallInfoBar();
        updateLargeInfoBar();
    }

    /**
     * 获取视频进度
     *
     * @return 视频进度
     */
    /****
     * Get video progress
     *
     * @return Video progress
     */
    public int getVideoPosition() {
        return mVideoPosition;
    }

    private void updateAllViews() {
//        updateTitleView();//更新标题信息，文字 Update title information, text
        updateScreenLockBtn();//更新锁屏状态 Update screen lock status
        updatePlayStateBtn();//更新播放状态  Update play status
        updateLargeInfoBar();//更新大屏的显示信息 Update display information for large screen
        updateSmallInfoBar();//更新小屏的显示信息 Update display information for small screen
        updateScreenModeBtn();//更新大小屏信息 Update big and small screens information
        updateAllTitleBar(); //更新标题显示 Update title display
        updateAllControlBar();//更新控制栏显示 Update control bar display
        updateShowMoreBtn();
        updateScreenShotBtn();
        updateScreenRecorderBtn();
        updateInputDanmakuView();
        updatePIPIcon();
        updateDotLayout();
    }

    /**
     * 更新控制条的显示
     */
    /****
     * Update control bar display
     */
    private void updateAllControlBar() {
        //单独设置可以显示，并且没有锁屏的时候才可以显示
        //Individual settings can be displayed and only when there is no lock screen can be displayed
        boolean canShow = mControlBarCanShow && !mScreenLocked;
        if (mControlBar != null) {
            mControlBar.setVisibility(canShow ? VISIBLE : INVISIBLE);
        }
//        if (mInputDanmkuImageView != null) {
//            mInputDanmkuImageView.setVisibility(canShow ? VISIBLE : INVISIBLE);
//        }
    }

    private void updateDotLayout() {
        if (mAliyunScreenMode == AliyunScreenMode.Small
                || mShowType == ShowType.AudioMode || mShowType == ShowType.ScreenCast) {
            videoDotLayout.setVisibility(GONE);
        } else {
            videoDotLayout.setVisibility(VISIBLE);
            videoDotLayout.onShow();
        }
    }

    /**
     * 更新标题栏的显示
     */
    /****
     * Update title bar display
     */
    private void updateAllTitleBar() {
        //单独设置可以显示，并且没有锁屏的时候才可以显示
        boolean canShow = mTitleBarCanShow && !mScreenLocked;
        if (mTitleBar != null) {
            mTitleBar.setVisibility(canShow ? VISIBLE : INVISIBLE);
        }
    }

    /**
     * 更新标题栏的标题文字
     */
    /****
     * Update title bar title text
     */
    private void updateTitleView() {
//        if (mAliyunMediaInfo != null && mAliyunMediaInfo.getTitle() != null && !("null".equals(mAliyunMediaInfo.getTitle()))) {
//            mTitlebarText.setText(mAliyunMediaInfo.getTitle());
//        } else {
//            mTitlebarText.setText("");
//        }
    }

    /**
     * 更新小屏下的控制条信息
     */
    /****
     * Update control bar information for small screen
     */
    private void updateSmallInfoBar() {
        if (mAliyunScreenMode == AliyunScreenMode.Full) {
            mSmallInfoBar.setVisibility(INVISIBLE);
        } else if (mAliyunScreenMode == AliyunScreenMode.Small) {
            mTrackLinearLayout.setVisibility(View.GONE);
            mScreenModeBtn.setVisibility(View.VISIBLE);
            //先设置小屏的info数据
            //First set the info data for the small screen
            if (GlobalPlayerConfig.IS_VIDEO && !mInScreenCosting) {
                setAdvVideoTotalDuration();
                mSmallMutiSeekbar.calculateWidth();
                if (isSeekbarTouching) {
                    //用户拖动的时候，不去更新进度值，防止跳动。
                    //When the user drags, it doesn't go to update the progress value to prevent jumping.
                } else {
                    mSmallMutiSeekbar.setProgress(mAdvVideoPosition);
                    mSmallPositionText.setText(TimeFormater.formatMs(mVideoPosition));
                }
            } else {
                if (mAliyunMediaInfo != null) {
                    mSmallDurationText.setText(TimeFormater.formatMs(mMediaDuration));
                    mSmallSeekbar.setMax((int) mMediaDuration);
                } else {
                    mSmallDurationText.setText(TimeFormater.formatMs(0));
                    mSmallSeekbar.setMax(0);
                }

                if (isSeekbarTouching) {
                    //用户拖动的时候，不去更新进度值，防止跳动。
                    //When the user drags, it doesn't go to update the progress value to prevent jumping.
                } else {
                    mSmallSeekbar.setSecondaryProgress(mVideoBufferPosition);
                    mSmallSeekbar.setProgress(mVideoPosition);
                    mSmallPositionText.setText(TimeFormater.formatMs(mVideoPosition));
                }
            }
            //然后再显示出来。
            //Finally display it.
            mSmallInfoBar.setVisibility(VISIBLE);
        }
    }

    private void updateTitleBackBtn(){
        if (mAliyunScreenMode == AliyunScreenMode.Small || mScreenLocked || mInScreenCosting) {
            mTitlebarBackBtn.setVisibility(INVISIBLE);
        } else {
            if (mAliyunScreenMode == AliyunScreenMode.Full) {
                mTitlebarBackBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 更新大屏下的控制条信息
     */
    /****
     * Update control bar information for large screen
     */
    private void updateLargeInfoBar() {
        if (mAliyunScreenMode == AliyunScreenMode.Small) {
            //里面包含了很多按钮，比如切换清晰度的按钮之类的
            //It contains many buttons, such as buttons for switching the resolution
            mFullScreenBottomLayout.setVisibility(INVISIBLE);
            mLargeInfoBar.setVisibility(INVISIBLE);
        } else if (mAliyunScreenMode == AliyunScreenMode.Full) {
//            mTrackLinearLayout.setVisibility(View.VISIBLE);
            mScreenModeBtn.setVisibility(View.GONE);
            if (GlobalPlayerConfig.IS_VIDEO && !mInScreenCosting) {
                setAdvVideoTotalDuration();
//                mLargeMutiSeekbar.calculateWidth();
                if (isSeekbarTouching) {
                    //用户拖动的时候，不去更新进度值，防止跳动。
                    //When the user drags, it doesn't go to update the progress value to prevent jumping.
                } else {
//                    mLargeMutiSeekbar.setProgress(mAdvVideoPosition);
                    mLargePositionText.setText(TimeFormater.formatMs(mVideoPosition));
                }
            } else {
                //先更新大屏的info数据
                //First update the info data for the large screen
                if (mAliyunMediaInfo != null) {
                    mLargeDurationText.setText(TimeFormater.formatMs(mMediaDuration));
                    mLargeSeekbar.setMax((int) mMediaDuration);
                } else {
                    mLargeDurationText.setText(TimeFormater.formatMs(0));
                    mLargeSeekbar.setMax(0);
                }

                if (isSeekbarTouching) {
                    //用户拖动的时候，不去更新进度值，防止跳动。
                    //When the user drags, it doesn't go to update the progress value to prevent jumping.
                } else {
                    mLargeSeekbar.setSecondaryProgress(mVideoBufferPosition);
                    mLargeSeekbar.setProgress(mVideoPosition);
                    mLargePositionText.setText(TimeFormater.formatMs(mVideoPosition));
                }
                mLargeChangeQualityBtn.setText(QualityItem.getItem(getContext(), mCurrentQuality, isMtsSource).getName());
            }
            //然后再显示出来。
            //Finally display it.
            mFullScreenBottomLayout.setVisibility(VISIBLE);
            mLargeInfoBar.setVisibility(VISIBLE);
        }
    }

    private void updateTrackInfoView() {
        mAudioTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_AUDIO);
        mBitrateTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_VIDEO);
        mDefinitionTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_VOD);
        mSubtitleTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_SUBTITLE);

        if (mAudioTrackInfoList == null || mAudioTrackInfoList.size() <= 0) {
            mAudioTextView.setVisibility(View.GONE);
        } else {
            mAudioTextView.setVisibility(View.VISIBLE);
        }

        if (mBitrateTrackInfoList == null || mBitrateTrackInfoList.size() <= 0) {
            mBitrateTextView.setVisibility(View.GONE);
        } else {
            mBitrateTextView.setVisibility(View.VISIBLE);
        }

        if (mSubtitleTrackInfoList == null || mSubtitleTrackInfoList.size() <= 0) {
            mSubtitleTextView.setVisibility(View.GONE);
        } else {
            mSubtitleTextView.setVisibility(View.VISIBLE);
        }

        if (mDefinitionTrackInfoList == null || mDefinitionTrackInfoList.size() <= 0) {
            mDefinitionTextView.setVisibility(View.GONE);
        } else {
            mDefinitionTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新视频广告的视频总时长
     */
    /****
     * Update video ad total duration
     */
    private void setAdvVideoTotalDuration() {
        if (mAdvPosition == null) {
            return;
        }
        switch (mAdvPosition) {
            case ONLY_START:
            case ONLY_END:
            case ONLY_MIDDLE:
                mSmallDurationText.setText(TimeFormater.formatMs(mAdvDuration + mSourceDuration));
                mLargeDurationText.setText(TimeFormater.formatMs(mAdvDuration + mSourceDuration));
                break;
            case START_END:
            case MIDDLE_END:
            case START_MIDDLE:
                mSmallDurationText.setText(TimeFormater.formatMs(mAdvDuration * 2 + mSourceDuration));
                mLargeDurationText.setText(TimeFormater.formatMs(mAdvDuration * 2 + mSourceDuration));
                break;
            default:
                mSmallDurationText.setText(TimeFormater.formatMs(mAdvDuration * 3 + mSourceDuration));
                mLargeDurationText.setText(TimeFormater.formatMs(mAdvDuration * 3 + mSourceDuration));
                break;
        }
    }

    /**
     * 更新切换大小屏按钮的信息
     */
    /****
     * Update big and small screens information
     */
    private void updateScreenModeBtn() {
        if (mAliyunScreenMode == AliyunScreenMode.Full) {
            mScreenModeBtn.setImageResource(R.drawable.alivc_screen_mode_small);
        } else {
            mScreenModeBtn.setImageResource(R.drawable.alivc_screen_mode_large);
        }
    }

    /**
     * 更新锁屏按钮的信息
     */
    /****
     * Update lock screen button information
     */
    private void updateScreenLockBtn() {
        if (mScreenLocked) {
            mScreenLockBtn.setImageResource(R.drawable.alivc_screen_lock);
        } else {
            mScreenLockBtn.setImageResource(R.drawable.alivc_screen_unlock);
        }

        if (mAliyunScreenMode == AliyunScreenMode.Full && !mInScreenCosting) {
            mScreenLockBtn.setVisibility(VISIBLE);
        } else {
            mScreenLockBtn.setVisibility(GONE);
        }
        updateShowMoreBtn();
    }

    /**
     * 更新播放按钮的状态
     */
    /****
     * Update play button status
     */
    private void updatePlayStateBtn() {
        if (mShowType == ShowType.AudioMode || mShowType == ShowType.ScreenCast) {
            mPlayStateBtn.setVisibility(View.GONE);
            mPlayStateFullBtn.setVisibility(View.GONE);
        } else {
            mPlayStateFullBtn.setVisibility(View.VISIBLE);
            if(mAliyunScreenMode == AliyunScreenMode.Full || isSeekbarTouching){
                mPlayStateBtn.setVisibility(View.GONE);
            }else{
                mPlayStateBtn.setVisibility(View.VISIBLE);
            }
            if (mPlayState == PlayState.NotPlaying) {
                mPlayStateBtn.setImageResource(R.drawable.alivc_playstate_play);
                mPlayStateFullBtn.setImageResource(R.drawable.alivc_playstate_play);
            } else if (mPlayState == PlayState.Playing) {
                mPlayStateBtn.setImageResource(R.drawable.alivc_playstate_pause);
                mPlayStateFullBtn.setImageResource(R.drawable.alivc_playstate_pause);
            }
        }
    }

    /**
     * 投屏状态下,修改播放器状态
     */
    /****
     * Listen to the status of the player when it is being broadcasted
     */
    public void updateScreenCostPlayStateBtn(boolean showPlay) {
        if (showPlay) {
            mPlayStateBtn.setImageResource(R.drawable.alivc_playstate_play);
            mPlayStateFullBtn.setImageResource(R.drawable.alivc_playstate_play);
        } else {
            mPlayStateBtn.setImageResource(R.drawable.alivc_playstate_pause);
            mPlayStateFullBtn.setImageResource(R.drawable.alivc_playstate_pause);
        }
    }

    /**
     * 监听view是否可见。从而实现5秒隐藏的功能
     */
    /****
     * Listen to whether the view is visible. From there, you can implement the 5-second hiding function
     */
    @Override
    protected void onVisibilityChanged(@Nullable View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && !mAudioMode) {
            //如果变为可见了。启动五秒隐藏。
            hideDelayed();
        }
    }

    public void setHideType(HideType hideType) {
        this.mHideType = hideType;
    }

    /**
     * 判断是否需要暂停
     */
    /****
     * Determine whether to pause
     */
    public boolean isNeedToPause(int currentPosition, int advVideoCount) {
        if (mSourceDuration <= 0) {
            return false;
        }
        boolean needPause;
        switch (mAdvPosition) {
            case ONLY_START:
                needPause = false;
                break;
            case ONLY_END:
            case START_END:
                needPause = currentPosition >= mSourceDuration;
                break;
            case ONLY_MIDDLE:
                needPause = (currentPosition >= mSourceDuration / 2) && advVideoCount == 0;
                break;
            case START_MIDDLE:
                needPause = currentPosition >= mSourceDuration / 2;
                break;
            case MIDDLE_END:
            case ALL:
                needPause = ((currentPosition >= mSourceDuration / 2) && advVideoCount == 1)
                        || (currentPosition >= mSourceDuration);
                break;
            default:
                needPause = false;
        }
        return needPause;
    }

    /**
     * 广告视频总时长
     */
    /****
     * Ad video total duration
     */
    public void setTotalPosition(long mAdvTotalPosition) {
        this.mAdvTotalPosition = mAdvTotalPosition;
    }

    /**
     * 开始投屏
     */
    /****
     * Start screen cost
     */
    public void startScreenCost() {
        if (mScreenModeBtn != null) {
            mScreenModeBtn.setVisibility(INVISIBLE);
        }
        if (mScreenCostFrameLayout != null) {
            mScreenCostFrameLayout.setVisibility(VISIBLE);
        }
        updateScreenCostPlayStateBtn(false);
        showNativeSeekBar();
        updateShowMoreBtn();
        updateScreenLockBtn();
        updateInputDanmakuView();
    }

    /**
     * 投屏播放完成
     */
    /****
     * Screen cost playback completion
     */
    public void onScreenCostingVideoCompletion() {
        updateCostReplayView(true);
    }

    /**
     * 退出投屏
     */
    /****
     * Exit screen cost
     */
    public void exitScreenCost() {
        updateShowMoreBtn();
        updateScreenLockBtn();
        updateInputDanmakuView();
        if (mScreenModeBtn != null) {
            mScreenModeBtn.setVisibility(View.VISIBLE);
        }
        if (mScreenCostFrameLayout != null) {
            mScreenCostFrameLayout.setVisibility(View.GONE);
        }
        //开启自动隐藏
        //Enable Auto Hide
        hideDelayed();
    }

    /**
     * 隐藏类
     */
    /****
     * Hide class
     */
    private static class HideHandler extends Handler {
        private WeakReference<ControlView> controlViewWeakReference;

        public HideHandler(ControlView controlView) {
            controlViewWeakReference = new WeakReference<ControlView>(controlView);
        }

        @Override
        public void handleMessage(Message msg) {

            ControlView controlView = controlViewWeakReference.get();
            if (controlView != null && !controlView.mAudioMode) {
                if (!controlView.isSeekbarTouching && !controlView.mInScreenCosting) {
                    controlView.hide(HideType.Normal);
                }
            }

            super.handleMessage(msg);
        }
    }

    private HideHandler mHideHandler = new HideHandler(this);

    private void hideDelayed() {
        mHideHandler.removeMessages(WHAT_HIDE);
        mHideHandler.sendEmptyMessageDelayed(WHAT_HIDE, DELAY_TIME);
    }

    /**
     * 重置状态
     */
    /****
     * Reset status
     */
    @Override
    public void reset() {
        mHideType = null;
        mAliyunMediaInfo = null;
        mVideoPosition = 0;
        mPlayState = PlayState.NotPlaying;
        isSeekbarTouching = false;
        setSpeedViewText(1.0f);
        showNativeSeekBar();
        updateAllViews();
    }

    /**
     * 关闭控制栏自动隐藏，并且展示控制栏
     */
    /****
     * Close control bar auto hide, and show control bar
     */
    public void closeAutoHide() {
        if (mHideHandler != null) {
            mHideHandler.removeMessages(WHAT_HIDE);
        }
        show();
    }

    /**
     * 开启控制栏自动隐藏
     */
    /****
     * Enable control bar auto hide
     */
    public void openAutoHide() {
        if (mHideHandler != null) {
            hideDelayed();
        }
    }

    /**
     * 显示画面
     */
    /****
     * Show picture
     */
    @Override
    public void show() {
        mShowType = ShowType.Normal;
        if (mHideType == HideType.End) {
            //如果是由于错误引起的隐藏，那就不能再展现了
            //If it is caused by hiding due to an error, it can no longer be displayed
            setVisibility(GONE);
            hideQualityDialog();
        } else {
            updateAllViews();
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void show(ShowType showType) {
        mShowType = showType;
        if (mHideType == HideType.End && mShowType != ShowType.AudioMode) {
            //如果是由于错误引起的隐藏，那就不能再展现了
            //If it is caused by hiding due to an error, it can no longer be displayed
            setVisibility(GONE);
            hideQualityDialog();
        } else {
            updateAllViews();
            setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏画面
     */
    /****
     * Hide picture
     */
    @Override
    public void hide(HideType hideType) {
        if (mHideType != HideType.End) {
            mHideType = hideType;
        }
        if (mOnControlViewHideListener != null) {
            mOnControlViewHideListener.onControlViewHide();
        }
        setVisibility(GONE);
        hideQualityDialog();
    }

    /**
     * 隐藏清晰度对话框
     */
    /****
     * Hide quality dialog
     */
    private void hideQualityDialog() {
        if (mOnQualityBtnClickListener != null) {
            mOnQualityBtnClickListener.onHideQualityView();
        }
    }

    /**
     * 设置当前缓存的进度，给seekbar显示
     *
     * @param mVideoBufferPosition 进度，ms
     */
    /****
     * Set the current progress of the cache, and show it to the seekbar
     *
     * @param mVideoBufferPosition progress, ms
     */
    public void setVideoBufferPosition(int mVideoBufferPosition) {
        this.mVideoBufferPosition = mVideoBufferPosition;
        updateSmallInfoBar();
        updateLargeInfoBar();
    }

    public void setOnQualityBtnClickListener(OnQualityBtnClickListener l) {
        mOnQualityBtnClickListener = l;
    }

    public void setSpeedViewText(float speed) {
        if (speed <= 1f && speed > 0.75f) {
            mVideoSpeedTextView.setText(getContext().getResources().getString(R.string.video_speed_title));
        } else {
            mVideoSpeedTextView.setText(speed + "x");
        }
    }

    public interface OnQualityBtnClickListener {
        /**
         * 清晰度按钮被点击
         *
         * @param v              被点击的view
         * @param qualities      支持的清晰度
         * @param currentQuality 当前清晰度
         */
        /****
         * Clarity button clicked
         *
         * @param v The view that was clicked.
         * @param qualities Supported sharpness
         * @param currentQuality currentCrispness
         */
        void onQualityBtnClick(View v, List<TrackInfo> qualities, String currentQuality);

        /**
         * 隐藏
         */
        /****
         * Hide
         */
        void onHideQualityView();
    }


    public void setOnScreenLockClickListener(OnScreenLockClickListener l) {
        mOnScreenLockClickListener = l;
    }

    public interface OnScreenLockClickListener {
        /**
         * 锁屏按钮点击事件
         */
        /****
         * Lock screen button click event
         */
        void onClick();
    }

    public void setOnScreenModeClickListener(OnScreenModeClickListener l) {
        mOnScreenModeClickListener = l;
    }

    public interface OnScreenModeClickListener {
        /**
         * 大小屏按钮点击事件
         */
        /****
         * Size screen button click event
         */
        void onClick();
    }


    public void setOnBackClickListener(OnBackClickListener l) {
        mOnBackClickListener = l;
    }

    public interface OnBackClickListener {
        /**
         * 返回按钮点击事件
         */
        /****
         * Return button click event
         */
        void onClick();
    }

    public interface OnSeekListener {
        /**
         * seek结束事件
         */
        /****
         * seek end event
         */
        void onSeekEnd(int position);

        /**
         * seek开始事件
         */
        /****
         * seek start event
         */
        void onSeekStart(int position);

        /**
         * seek进度改变
         */
        /****
         * seek progress changed
         */
        void onProgressChanged(int progress);
    }


    public void setOnSeekListener(OnSeekListener onSeekListener) {
        mOnSeekListener = onSeekListener;
    }

    /**
     * 播放状态
     */
    /****
     * Play state
     */
    public static enum PlayState {
        /**
         * Playing:正在播放
         * NotPlaying: 停止播放
         */
        /****
         * Playing:Playing
         * NotPlaying:NotPlaying
         */
        Playing, NotPlaying
    }

    public interface OnPlayStateClickListener {
        /**
         * 播放按钮点击事件
         */
        /****
         * Play button click event
         */
        void onPlayStateClick();

        /**
         * 投屏重播点击事件
         */
        /****
         * Screen cost replay click event
         */
        void onRePlayClick();
    }


    public void setOnPlayStateClickListener(OnPlayStateClickListener onPlayStateClickListener) {
        mOnPlayStateClickListener = onPlayStateClickListener;
    }

    /**
     * 横屏下显示更多
     */
    /****
     * Landscape screen display more
     */
    public interface OnShowMoreClickListener {
        void showMore();
    }

    public void setOnShowMoreClickListener(
            OnShowMoreClickListener listener) {
        this.mOnShowMoreClickListener = listener;
    }

    /**
     * 屏幕截图
     */
    /****
     * Screenshot
     */
    public interface OnScreenShotClickListener {
        void onScreenShotClick();
    }

    public void setOnScreenShotClickListener(OnScreenShotClickListener listener) {
        this.mOnScreenShotClickListener = listener;
    }

    public void setOnVideoSpeedClickListener(OnVideoSpeedClickListener onVideoSpeedClickListener) {
        this.mOnVideoSpeedClickListener = onVideoSpeedClickListener;
    }

    public interface OnVideoSpeedClickListener {
        void onVideoSpeedClick();
    }

    /**
     * 录制
     */
    /****
     * Record
     */
    public interface OnScreenRecoderClickListener {
        void onScreenRecoderClick();
    }

    public void setOnScreenRecoderClickListener(OnScreenRecoderClickListener listener) {
        this.mOnScreenRecoderClickListener = listener;
    }

    /**
     * ContentView隐藏监听
     */
    /****
     * ContentView hide listener
     */
    public interface OnControlViewHideListener {
        void onControlViewHide();
    }

    public void setOnControlViewHideListener(OnControlViewHideListener listener) {
        this.mOnControlViewHideListener = listener;
    }

    /**
     * 输入弹幕
     */
    /****
     * Input danmaku
     */
    public interface OnInputDanmakuClickListener {
        void onInputDanmakuClick();
    }

    public void setOnInputDanmakuClickListener(OnInputDanmakuClickListener listener) {
        this.mOnInputDanmakuClickListener = listener;
    }

    public interface OnDLNAControlListener {
        void onExit();

        void onChangeQuality();

        void onChangeSeries(int series);
    }

    public void setOnDLNAControlListener(OnDLNAControlListener listener) {
        this.mOnDLNAControlListener = listener;
    }

    /**
     * trackInfo点击事件
     */
    /****
     * TrackInfo click event
     */
    public interface OnTrackInfoClickListener {
        void onSubtitleClick(List<TrackInfo> trackInfoList);

        void onAudioClick(List<TrackInfo> trackInfoList);

        void onBitrateClick(List<TrackInfo> trackInfoList);

        void onDefinitionClick(List<TrackInfo> trackInfoList);
    }

    public void setOnTrackInfoClickListener(OnTrackInfoClickListener listener) {
        this.mOnTrackInfoClickListener = listener;
    }

    /**
     * 获取视频广告下，当前总进度条的进度(包含广告时长)
     */
    /****
     * Get the progress of the total progress bar (including the ad duration)
     */
    public int getMutiSeekBarCurrentProgress() {
        return mMutiSeekBarCurrentProgress;
    }

    public void setMutiSeekBarInfo(long advTime, long sourceTime, MutiSeekBarView.AdvPosition advPosition) {
        this.mAdvDuration = advTime;
        this.mSourceDuration = sourceTime;
        this.mAdvPosition = advPosition;
    }

    /**
     * 隐藏原生seekBar,展示自定义seekBar
     */
    /****
     * Hide the original seekBar, show the custom seekBar
     */
    public void hideNativeSeekBar() {
        if (mSmallMutiSeekbar != null) {
            mSmallMutiSeekbar.setVisibility(View.VISIBLE);
            mSmallMutiSeekbar.post(new Runnable() {
                @Override
                public void run() {
                    mSmallMutiSeekbar.setTime(mAdvDuration, mSourceDuration, mAdvPosition);
                }
            });
        }
//        if (mLargeMutiSeekbar != null) {
//            mLargeMutiSeekbar.setVisibility(View.VISIBLE);
//            mLargeMutiSeekbar.post(new Runnable() {
//                @Override
//                public void run() {
//                    mLargeMutiSeekbar.setTime(mAdvDuration, mSourceDuration, mAdvPosition);
//                }
//            });
//        }
        if (mInScreenCosting) {
            if (mSmallMutiSeekbar != null) {
                mSmallMutiSeekbar.setVisibility(View.GONE);
            }
//            if (mLargeMutiSeekbar != null) {
//                mLargeMutiSeekbar.setVisibility(View.GONE);
//            }
        } else {
            if (mSmallSeekbar != null) {
                mSmallSeekbar.setVisibility(View.GONE);
            }
            if (mLargeSeekbar != null) {
                mLargeSeekbar.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 隐藏自定义seekBar,展示原生seekBar
     */
    /****
     * Hide the custom seekBar, show the original seekBar
     */
    public void showNativeSeekBar() {
        if (mSmallSeekbar != null) {
            mSmallSeekbar.setVisibility(View.VISIBLE);
        }
        if (mLargeSeekbar != null) {
            mLargeSeekbar.setVisibility(View.VISIBLE);
        }
        if (mSmallMutiSeekbar != null) {
            mSmallMutiSeekbar.setVisibility(View.GONE);
        }
//        if (mLargeMutiSeekbar != null) {
//            mLargeMutiSeekbar.setVisibility(View.GONE);
//        }
    }

    public void setDotInfo(List<DotBean> dotBean) {
        this.mDotBean = dotBean;
    }

    /**
     * 初始化打点View
     */
    /****
     * Initialize the dot view
     */
    public void initDotView() {
        if (mDotBean == null || videoDotLayout == null || mAliyunMediaInfo == null) {
            return;
        }
        videoDotLayout.setData(mDotBean, mAliyunMediaInfo.getDuration() / 1000);
    }

    public interface OnFloatPlayViewClickListener {
        void onFloatViewPlayClick();
    }

    private OnFloatPlayViewClickListener mOnFloatPlayViewClickListener;

    public void setOnFloatPlayViewClickListener(OnFloatPlayViewClickListener onFloatPlayViewClickListener) {
        this.mOnFloatPlayViewClickListener = onFloatPlayViewClickListener;
    }


    private OnDamkuOpenListener onDamkuOpenListener;

    public void setOnDamkuOpenListener(OnDamkuOpenListener onDamkuOpenListener) {
        this.onDamkuOpenListener = onDamkuOpenListener;
    }

    public interface OnDamkuOpenListener {
        void onDamkuOpen(boolean open);
    }

    public interface OnAudioModeChangeListener {
        void onAudioMode(boolean audioMode);
    }

    public interface OnCastScreenListener {
        void onCastScreen(boolean openCastScreen);
    }

    public interface OnSelectSeriesListener {
        void onSelectVideo();
    }

    private OnAudioModeChangeListener onAudioModeChangeListener;
    private OnCastScreenListener onCastScreenListener;

    public void setOnAudioModeChangeListener(OnAudioModeChangeListener onAudioModeChangeListener) {
        this.onAudioModeChangeListener = onAudioModeChangeListener;
    }

    public void setOnCastScreenListener(OnCastScreenListener onCastScreenListener) {
        this.onCastScreenListener = onCastScreenListener;
    }


    private OnSelectSeriesListener mOnSelectSeriesClickListener;

    public void setOnSelectSeriesClickListener(OnSelectSeriesListener onSelectSeriesClickListener) {
        mOnSelectSeriesClickListener = onSelectSeriesClickListener;
    }

    public interface OnNextSeriesClick {
        void onNextSeries();
    }

    private OnNextSeriesClick onNextSeriesClick;

    public void setOnNextSeriesClickListener(OnNextSeriesClick onNextSeriesClick) {
        this.onNextSeriesClick = onNextSeriesClick;
    }

    public void showVideoSpeedTipLayout(boolean show) {
        if (mVideoSpeedTipView == null)
            return;
        if (show) {
            mVideoSpeedTipView.setVisibility(VISIBLE);
        } else {
            mVideoSpeedTipView.setVisibility(GONE);
        }
    }

}
