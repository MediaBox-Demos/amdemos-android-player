package com.aliyun.player.alivcplayerexpand.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * 所有自定义对话框的基类
 * 用来实现复杂的弹框样式
 */
/****
 * Base class for all custom dialogs
 * Used to implement complex popup box styles
 */
public abstract class BaseDialogFragment extends DialogFragment {


    protected String tag = getClass().getSimpleName();

    //默认透明度
    // Default transparency
    private static final float DEFAULT_DIMAMOUNT = 0.2F;

    //填充视图
    //Fill view
    protected abstract int getLayoutRes();

    //设置视图内容
    //Set view content
    protected abstract void bindView(View view);

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            //设置窗体背景色透明
            //Set window background color transparent
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置宽高
            //Set width and height
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            if (getDialogWidth() > 0) {
                layoutParams.width = getDialogWidth();
            } else {
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            }
            if (getDialogHeight() > 0) {
                layoutParams.height = getDialogHeight();
            } else {
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            //透明度
            //Transparency
            layoutParams.dimAmount = getDimAmount();
            //位置
            //Position
            layoutParams.gravity = getGravity();
            window.setAttributes(layoutParams);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = null;
        if (getLayoutRes() > 0) {
            view = inflater.inflate(getLayoutRes(), container, false);
        }
        bindView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //去除Dialog默认头部
        //Remove default header of Dialog
        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(isCancelableOutside());
        if (dialog.getWindow() != null && getDialogAnimationRes() > 0) {
            dialog.getWindow().setWindowAnimations(getDialogAnimationRes());
        }
        if (getOnKeyListener() != null) {
            dialog.setOnKeyListener(getOnKeyListener());
        }
    }

    protected DialogInterface.OnKeyListener getOnKeyListener() {
        return null;
    }

    //默认弹窗位置为中心
    //Default popup location is center
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    //默认宽高为包裹内容
    //Default width and height to wrap content
    public int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public int getDialogWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    //默认透明度为0.2
    //Default transparency is 0.2
    public float getDimAmount() {
        return DEFAULT_DIMAMOUNT;
    }

    public String getFragmentTag() {
        return tag;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getFragmentTag());
    }

    protected boolean isCancelableOutside() {
        return true;
    }

    //获取弹窗显示动画,子类实现
    //Get popup box display animation, implemented in the derived class
    protected int getDialogAnimationRes() {
        return 0;
    }

    //获取设备屏幕宽度
    //Get device screen width
    public static final int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    //获取设备屏幕高度
    //Get device screen height
    public static final int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}