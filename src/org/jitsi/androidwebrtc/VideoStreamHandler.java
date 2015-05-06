package org.jitsi.androidwebrtc;

import android.util.*;
import org.webrtc.*;

/**
 *
 */
public class VideoStreamHandler
{
    private final static String TAG = "VideoStreamHandler";
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final VideoRendererGui.ScalingType scalingType;
    private final boolean mirror;

    private VideoRenderer.Callbacks rendererCallbacks;

    private VideoRenderer videoRenderer;

    private MediaStream mediaStream;
    private VideoTrack remoteVideoTrack;

    public VideoStreamHandler(int x, int y, int w, int h, VideoRendererGui.ScalingType scalingType, boolean mirror)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.scalingType = scalingType;
        this.mirror = mirror;
        this.rendererCallbacks
            = VideoRendererGui.create(x, y, w ,h, scalingType, mirror);
    }

    public synchronized void start(MediaStream mediaStream, boolean enabled)
    {
        if (this.mediaStream != null)
            return;

        this.mediaStream = mediaStream;

        Log.d(TAG, "Starting renderer for " + mediaStream);

        this.videoRenderer = new VideoRenderer(rendererCallbacks);


        remoteVideoTrack = mediaStream.videoTracks.get(0);
        remoteVideoTrack.setEnabled(enabled);
        remoteVideoTrack.addRenderer(videoRenderer);
    }

    public synchronized void setEnabled(boolean enabled)
    {
        if (remoteVideoTrack != null)
        {
            remoteVideoTrack.setEnabled(enabled);
        }
    }

    public synchronized void stop()
    {
        if (mediaStream == null)
        {
            return;
        }

        /*if (remoteVideoTrack != null)
        {
            remoteVideoTrack.dispose();
        }*/

        //Log.d(TAG, "Stopping renderer for " + mediaStream);

        //videoRenderer.dispose();

        //mediaStream.videoTracks.get(0).removeRenderer(videoRenderer);

        //mediaStream.dispose();

        //mediaStream.videoTracks.get(0).dispose();

        mediaStream = null;
        videoRenderer = null;
        remoteVideoTrack = null;

        //update();
    }

    public synchronized boolean isRunning()
    {
        return this.mediaStream != null;
    }

    public synchronized boolean isStreamRenderer(MediaStream mediaStream)
    {
        return isRunning() && this.mediaStream == mediaStream;
    }

    public void update()
    {
        if (remoteVideoTrack != null)
        {
            VideoRendererGui.update(
                rendererCallbacks, x, y, w, h, scalingType, false);
        }
        else
        {
            VideoRendererGui.update(
                rendererCallbacks, x, y, 0, 0, scalingType, false);
        }
    }
}
