package com.aliyun.apsaravideo.videocommon.list;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 监听recycleView滑动状态，
 * 自动播放可见区域内的第一个视频
 */
/****
 * Listening to recycleView sliding state.
 * Automatically play the first video in the visible area
 */
public class AutoPlayScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "AutoPlayScrollListener";

    private int firstVisibleItem = 0;
    private int lastVisibleItem = 0;
    private int visibleCount = 0;
    private int mLastVisiblePosition = 0;
    /**
     * 构造函数相关
     */
    /****
     * Constructor related
     */
    private int mVideoViewId;
    private ListPlayCallback mListPlayCallback;

    public AutoPlayScrollListener(int mVideoViewId, ListPlayCallback mListPlayCallback) {
        this.mVideoViewId = mVideoViewId;
        this.mListPlayCallback = mListPlayCallback;
    }

    /**
     * 被处理的视频状态标签
     */
    /****
     * Processed video status tag
     */
    private enum VideoTagEnum {

        /**
         * 自动播放视频
         */
        /****
         * Auto play video
         */
        TAG_AUTO_PLAY_VIDEO,

        /**
         * 暂停视频
         */
        /****
         * Pause video
         */
        TAG_PAUSE_VIDEO
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                autoPlayVideo(recyclerView, VideoTagEnum.TAG_AUTO_PLAY_VIDEO);
            default:
                // 滑动时暂停视频 autoPlayVideo(recyclerView, VideoTagEnum.TAG_PAUSE_VIDEO);
                // Pause video while sliding
                break;
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            firstVisibleItem = linearManager.findFirstVisibleItemPosition();
            lastVisibleItem = linearManager.findLastVisibleItemPosition();
            visibleCount = lastVisibleItem - firstVisibleItem;
        }

    }

    /**
     * 循环遍历 可见区域的播放器
     * 然后通过 getLocalVisibleRect(rect)方法计算出哪个播放器完全显示出来
     *
     * @param recyclerView
     * @param handleVideoTag 视频需要进行状态
     */
    /****
     * Loop through the visible player in the visible area
     * Then calculate which player completely displayed by using the getLocalVisibleRect(rect) method
     *
     * @param recyclerView
     * @param handleVideoTag Video needs to be in a certain state
     */
    private void autoPlayVideo(RecyclerView recyclerView, VideoTagEnum handleVideoTag) {
        for (int i = 0; i < visibleCount; i++) {
            if (recyclerView != null && recyclerView.getChildAt(i) != null && recyclerView.getChildAt(i).findViewById(mVideoViewId) != null) {
                View homeGSYVideoPlayer = recyclerView.getChildAt(i).findViewById(mVideoViewId);

                Rect rect = new Rect();
                homeGSYVideoPlayer.getLocalVisibleRect(rect);
                int videoheight = homeGSYVideoPlayer.getHeight();
                if (rect.top == 0 && rect.bottom == videoheight) {
                    handleVideo(handleVideoTag, i + firstVisibleItem);
                    // 跳出循环，只处理可见区域内的第一个播放器
                    // Exit the loop and only handle the first player in the visible area
                    break;
                }
            }
        }

    }

    /**
     * 视频状态处理
     *
     * @param handleVideoTag 视频需要进行状态
     */
    /****
     * Video status processing
     *
     * @param handleVideoTag Video needs to be in a certain state
     */
    private void handleVideo(VideoTagEnum handleVideoTag, int position) {
        if (mListPlayCallback == null) {
            Log.i(TAG, "mListPlayCallback is null");
            return;
        }
        switch (handleVideoTag) {
            case TAG_AUTO_PLAY_VIDEO:
                if ((mListPlayCallback.getPlayState() != ListPlayCallback.STATE_PLAYING)) {
                    // 进行播放
                    // Play video
                    mListPlayCallback.play(position);
                }
                break;
            case TAG_PAUSE_VIDEO:
                if (mListPlayCallback.getPlayState() != ListPlayCallback.STATE_PAUSED) {
                    // 暂停视频
                    // Pause video
                    mListPlayCallback.pause();
                }
                break;
            default:
                break;
        }
    }


    public void setListPlayCallback(ListPlayCallback mListPlayCallback) {
        this.mListPlayCallback = mListPlayCallback;
    }
}