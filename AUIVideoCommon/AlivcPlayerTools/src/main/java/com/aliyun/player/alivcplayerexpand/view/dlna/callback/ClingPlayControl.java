package com.aliyun.player.alivcplayerexpand.view.dlna.callback;

import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.aliyun.player.alivcplayerexpand.util.ClingUtils;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingPositionResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingTransportInfoResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingVolumeResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.DLANPlayState;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.manager.ClingManager;
import com.aliyun.player.alivcplayerexpand.util.Formatter;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.item.VideoItem;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;
import org.seamless.util.MimeType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cling 实现的控制方法
 */
/****
 * Control methods implemented by Cling
 */
public class ClingPlayControl implements IPlayControl {

    private static final String TAG = ClingPlayControl.class.getSimpleName();
    /** 每次接收 500ms 延迟 */
    /** 500ms delay per reception */
    private static final int RECEIVE_DELAY = 500;
    /** 上次设置音量时间戳, 防抖动 */
    /** Last time of setting volume, to prevent shake */
    private long mVolumeLastTime;
    /**
     * 当前状态
     */
    /****
     * Current state
     */
    private @DLANPlayState.DLANPlayStates int mCurrentState = DLANPlayState.STOP;
    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" + "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";

    @Override
    public void playNew(final String url, final ControlCallback callback) {

        stop(new ControlCallback() { // 1、 停止当前播放视频 1. Stop the current playing video
            @Override
            public void success(IResponse response) {

                setAVTransportURI(url, new ControlCallback() {   // 2、设置 url  2. Set the URL
                    @Override
                    public void success(IResponse response) {
                        play(callback);                        // 3、播放视频 3. Play the video
                    }

                    @Override
                    public void fail(IResponse response) {
                        if (callback != null) {
                            callback.fail(response);
                        }
                    }
                });
            }

            @Override
            public void fail(IResponse response) {
                if (callback != null) {
                    callback.fail(response);
                }
            }
        });
    }

    @Override
    public void play(final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(new Play(avtService) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (callback != null) {
                    callback.success(new ClingResponse(invocation));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                }
            }
        });
    }

    @Override
    public void pause(final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(new Pause(avtService) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (callback != null) {
                    callback.success(new ClingResponse(invocation));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                }
            }
        });
    }

    @Override
    public void stop(final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(new Stop(avtService) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (callback != null) {
                    callback.success(new ClingResponse(invocation));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                }
            }
        });
    }

    @Override
    public void seek(int pos, final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        String time = Formatter.getStringTime(pos);
        Log.e(TAG, "seek->pos: " + pos + ", time: " + time);
        controlPointImpl.execute(new Seek(avtService, time) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (callback != null) {
                    callback.success(new ClingResponse(invocation));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                }
            }
        });
    }

    @Override
    public void setVolume(int pos, @Nullable final ControlCallback callback) {
        final Service rcService = ClingUtils.findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE);
        if (rcService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > mVolumeLastTime + RECEIVE_DELAY){
            controlPointImpl.execute(new SetVolume(rcService, pos) {

                @Override
                public void success(ActionInvocation invocation) {
                    if (callback != null) {
                        callback.success(new ClingResponse(invocation));
                    }
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    if (callback != null) {
                        callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                    }
                }
            });
        }
        mVolumeLastTime = currentTimeMillis;
    }

    @Override
    public void setMute(boolean desiredMute, @Nullable final ControlCallback callback) {
        final Service rcService = ClingUtils.findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE);
        if (rcService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(new SetMute(rcService, desiredMute) {

            @Override
            public void success(ActionInvocation invocation) {
                if (callback != null) {
                    callback.success(new ClingResponse(invocation));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                }
            }
        });
    }

    /**
     * 设置片源，用于首次播放
     *
     * @param url   片源地址
     * @param callback  回调
     */
    /****
     * Setting up the source for the first playback
     *
     * @param url source address
     * @param callback callback
     */
    private void setAVTransportURI(String url, final ControlCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        String metadata = pushMediaToRender(url, "id", "name", "0");

        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(new SetAVTransportURI(avtService, url, metadata) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (callback != null) {
                    callback.success(new ClingResponse(invocation));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingResponse(invocation, operation, defaultMsg));
                }
            }
        });
    }

    /**
     * 获取当前播放状态
     */
    /****
     * Get the current playback status
     */
    @Override
    public void getTransportInfo(final ControlReceiveCallback callback){
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        GetTransportInfo getTransportInfo = new GetTransportInfo(avtService) {
            @Override
            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                if(callback != null){
                    callback.receive(new ClingTransportInfoResponse(invocation,transportInfo));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if(callback != null){
                    callback.fail(new ClingTransportInfoResponse(invocation,operation,defaultMsg));
                }
            }
        };

        controlPointImpl.execute(getTransportInfo);
    }

    @Override
    public void getPositionInfo(final ControlReceiveCallback callback) {

        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (avtService == null) {
            return;
        }

        Log.d(TAG, "Found media render service in device, sending get position");

        GetPositionInfo getPositionInfo = new GetPositionInfo(avtService) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingPositionResponse(invocation, operation, defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (callback != null) {
                    callback.success(new ClingPositionResponse(invocation));
                }
            }

            @Override
            public void received(ActionInvocation invocation, PositionInfo info) {
                if (callback != null) {
                    callback.receive(new ClingPositionResponse(invocation, info));
                }
            }
        };

        ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(getPositionInfo);
    }

    @Override
    public void getVolume(final ControlReceiveCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE);
        if (avtService == null) {
            return;
        }
        GetVolume getVolume = new GetVolume(avtService) {
            @Override
            public void received(ActionInvocation actionInvocation, int currentVolume) {
                if (callback != null) {
                    callback.receive(new ClingVolumeResponse(actionInvocation, currentVolume));
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (callback != null) {
                    callback.fail(new ClingVolumeResponse(invocation, operation, defaultMsg));
                }
            }
        };

        ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        controlPointImpl.execute(getVolume);
    }

    public @DLANPlayState.DLANPlayStates
    int getCurrentState() {
        return mCurrentState;
    }

    public void setCurrentState(@DLANPlayState.DLANPlayStates int currentState) {
        if (this.mCurrentState != currentState) {
            this.mCurrentState = currentState;
        }
    }

    private String pushMediaToRender(String url, String id, String name, String duration) {
        long size = 0;
        long bitrate = 0;
        Res res = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url);

        String creator = "unknow";
        String resolution = "unknow";
        VideoItem videoItem = new VideoItem(id, "0", name, creator, res);

        String metadata = createItemMetadata(videoItem);
        Log.e(TAG, "metadata: " + metadata);
        return metadata;
    }

    private String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);

        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));

        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));

        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        metadata.append(String.format("<dc:date>%s</dc:date>", time));

        // metadata.append(String.format("<upnp:album>%s</upnp:album>",
        // item.get);

        // <res protocolInfo="http-get:*:audio/mpeg:*"
        // resolution="640x478">http://192.168.1.104:8088/Music/07.我醒著做夢.mp3</res>

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolinfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolinfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi
                        .getAdditionalInfo());
            }
            Log.e(TAG, "protocolinfo: " + protocolinfo);

            // resolution, extra info, not adding yet
            String resolution = "";
            if (res.getResolution() != null && res.getResolution().length() > 0) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (res.getDuration() != null && res.getDuration().length() > 0) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            //            metadata.append(String.format("<res %s>", protocolinfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolinfo, resolution, duration));

            // url
            String url = res.getValue();
            metadata.append(url);

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }
}
