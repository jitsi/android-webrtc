package org.jitsi.androidwebrtc;

import android.util.*;
import org.webrtc.*;

/**
 *
 */
public class VideoStreamHandler
{
    private final static String TAG = "VideoStreamHandler";

    private VideoRenderer.Callbacks rendererCallbacks;

    private VideoRenderer videoRenderer;

    private MediaStream mediaStream;

    public VideoStreamHandler(VideoRenderer.Callbacks rendererCallbacks)
    {
        this.rendererCallbacks = rendererCallbacks;
    }

    public synchronized void start(MediaStream mediaStream)
    {
        if (this.mediaStream != null)
            return;

        this.mediaStream = mediaStream;

        Log.d(TAG, "Starting renderer for " + mediaStream);

        this.videoRenderer = new VideoRenderer(rendererCallbacks);

        this.mediaStream.videoTracks.get(0).addRenderer(videoRenderer);
    }

    public synchronized void stop()
    {
        if (mediaStream == null)
        {
            return;
        }

        //Log.d(TAG, "Stopping renderer for " + mediaStream);

        //videoRenderer.dispose();

        //mediaStream.videoTracks.get(0).removeRenderer(videoRenderer);

        //mediaStream.dispose();

        //mediaStream.videoTracks.get(0).dispose();

        mediaStream = null;
        videoRenderer = null;
    }

    public synchronized boolean isRunning()
    {
        return this.mediaStream != null;
    }

    public synchronized boolean isStreamRenderer(MediaStream mediaStream)
    {
        return isRunning() && this.mediaStream == mediaStream;
    }
}
