package com.aliyun.auiplayerapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aliyun.auiplayerapp.R;

/**
 * 检查权限/权限数组
 * request权限
 */
/****
 * Check permissions/privilege array
 * request permissions
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getName();

    public static final String[] PERMISSION_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static final String[] PERMISSION_STORAGE33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };

    public static final String[] PERMISSION_CAMERA = {
        Manifest.permission.CAMERA,
    };

    /**
     * 无权限时对应的提示内容
     */
    /****
     * The content of the alert for no permission
     */
    public static final int[] NO_PERMISSION_TIP = {
        R.string.alivc_common_no_camera_permission,
        R.string.alivc_common_no_record_bluetooth_permission,
        R.string.alivc_common_no_record_audio_permission,
        R.string.alivc_common_no_read_phone_state_permission,
        R.string.alivc_common_no_write_external_storage_permission,
        R.string.alivc_common_no_read_external_storage_permission,
    };


    /**
     * 检查多个权限
     *
     * 检查权限
     * @param permissions 权限数组
     * @param context Context
     * @return true 已经拥有所有check的权限 false存在一个或多个未获得的权限
     */
    /****
     * Check multiple permissions
     *
     * Check permissions
     * @param permissions Array of permissions
     * @param context Context
     * @return true Already have all permissions checked false One or more unobtained permissions exist
     */
    public static boolean checkPermissionsGroup(Context context, String[] permissions) {

        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查单个权限
     * @param context Context
     * @param permission 权限
     * @return boolean
     */
    /****
     * Check single permissions
     * @param context context
     * @param permission Permission
     * @return boolean
     */
    private static boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 申请权限
     * @param activity Activity
     * @param permissions 权限数组
     * @param requestCode 请求码
     */
    /****
     * Request permission
     * @param activity Activity
     * @param permissions Array of permissions
     * @param requestCode request code
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        // 先检查是否已经授权
        // Check if authorization has been granted first
        if (!checkPermissionsGroup(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    /**
     * 通过AppOpsManager判断小米手机授权情况
     *
     * @return boolean
     */
    /****
     * Determining Xiaomi phone authorization through AppOpsManager
     *
     * @return boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean checkXiaomi(Context context, String[] opstrArrays) {
        AppOpsManager appOpsManager = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
        String packageName = context.getPackageName();
        for (String opstr : opstrArrays) {
            int locationOp = appOpsManager.checkOp(opstr, Binder.getCallingUid(), packageName);
            if (locationOp == AppOpsManager.MODE_IGNORED) {
                return false;
            }
        }

        return true;
    }
}
