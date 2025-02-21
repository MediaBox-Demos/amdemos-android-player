package com.aliyun.player.alivcplayerexpand.view.dlna.manager;

import android.content.Context;


import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IControlPoint;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;

import java.util.Collection;

/**
 * DLNA管理类
 *
 * @author hanyu
 */
/****
 * DLNA management class
 *
 * @author hanyu
 */
public interface IDLNAManager {

    /**
     * 搜索设备
     */
    /****
     * Search devices
     */
    void searchDevices();

    /**
     * 获取支持 Media 类型的设备
     */
    /****
     * Get devices that support Media
     */
    Collection<? extends IDevice> getDmrDevices();

    /**
     * 获取控制点
     */
    /****
     * Get control point
     */
    IControlPoint getControlPoint();

    /**
     * 获取选中的设备
     *
     * @return 选中的设备
     */
    /****
     * Get selected device
     *
     * @return selected device
     */
    IDevice getSelectedDevice();

    /**
     * 取消选中的设备
     */
    /****
     * Cancel selected device
     */
    void cleanSelectedDevice();

    /**
     * 连接成功后，设置device为连接状态
     */
    /****
     * After connection is successful, set device to connected status
     */
    void setSelectedDeviceConnected();

    /**
     * 设置选中的设备
     *
     * @param device 已选中设备
     */
    /****
     * Set selected device
     *
     * @param device selected device
     */
    void setSelectedDevice(IDevice device);

    /**
     * 监听投屏端 AVTransport 回调
     */
    /****
     * Listen to the AVTransport callback of the screen casting side
     */
    void registerAVTransport(Context context);

    /**
     * 监听投屏端 RenderingControl 回调
     */
    /****
     * Listen to the RenderingControl callback of the screen casting side
     */
    void registerRenderingControl(Context context);

    /**
     * 销毁
     */
    /****
     * Destroy
     */
    void destroy();

}
