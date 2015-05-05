/*
 * libjingle
 * Copyright 2013, Google Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jitsi.androidwebrtc;

import android.util.Log;

import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jitsi.androidwebrtc.meet.*;
import org.jitsi.androidwebrtc.meet.util.*;
import org.jivesoftware.smack.provider.*;
import org.webrtc.*;

import java.util.*;

/**
 * Negotiates signaling for chatting with apprtc.appspot.com "rooms".
 * Uses the client<->server specifics of the apprtc AppEngine webapp.
 * <p/>
 * To use: create an instance of this object (registering a message handler) and
 * call connectToRoom().  Once that's done call sendMessage() and wait for the
 * registered handler to be called with received messages.
 */
public class MeetClient
{
    private static final String TAG = "AppRTCClient";

    private final MeetDemoActivity activity;

    private final IceServersObserver iceServersObserver;

    private AppRTCSignalingParameters appRTCSignalingParameters;

    Participant participant;

    public void acceptSessionInit(SessionDescription bridgeOfferSdp)
    {
        activity.setRemoteDescription(bridgeOfferSdp);
    }

    public void sendSessionAccept(SessionDescription sdp)
    {
        participant.sendSessionAccept(sdp);
    }

    public void onSourceAdd(MediaSSRCMap addedSSRCs)
    {
        SessionDescription rsd = activity.getRemoteDescription();

        SessionDescription modifiedOffer
            = JingleUtils.addSSRCs(rsd, addedSSRCs);

        activity.setRemoteDescription(modifiedOffer);
    }

    public void onSourceRemove(MediaSSRCMap removedSSRCs)
    {
        SessionDescription rsd = activity.getRemoteDescription();

        SessionDescription modifiedOffer
                = JingleUtils.removeSSRCs(rsd, removedSSRCs);

        activity.setRemoteDescription(modifiedOffer);
    }

    /**
     * Callback fired once the room's signaling parameters specify the set of
     * ICE servers to use.
     */
    public static interface IceServersObserver
    {
        public void onIceServers(List<PeerConnection.IceServer> iceServers);
    }

    public MeetClient(
        MeetDemoActivity activity, IceServersObserver iceServersObserver)
    {
        this.activity = activity;
        this.iceServersObserver = iceServersObserver;
    }

    /**
     * Asynchronously connect to an AppRTC room URL, e.g.
     * https://apprtc.appspot.com/?r=NNN and register message-handling callbacks
     * on its GAE Channel.
     */
    public void connectToRoom(String url)
    {

        final String domain = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/"));
        String room = url.substring(url.lastIndexOf("/") + 1);
        final String fullMuc = room + "@conference." + domain;
        Log.i(TAG, "Domain: '" + domain + "' room: '" + room + "' muc: '" + fullMuc + "'");

        final List<PeerConnection.IceServer> iceServers
                = new ArrayList<PeerConnection.IceServer>();

        MediaConstraints pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(
                new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        MediaConstraints videoConstraints = new MediaConstraints();

        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googNoiseSupression", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googNoisesuppression2", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googEchoCancellation2", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googAutoGainControl2", "true"));

        appRTCSignalingParameters = new AppRTCSignalingParameters(
                iceServers,
                "gaeBaseHref",
                "channelToken",
                "postMessageUrl",
                false,
                pcConstraints,
                videoConstraints,
                audioConstraints);

        iceServersObserver.onIceServers(iceServers);

        ProviderManager.getInstance().addIQProvider(
                ColibriConferenceIQ.ELEMENT_NAME,
                ColibriConferenceIQ.NAMESPACE,
                new ColibriIQProvider());

        ProviderManager.getInstance().addIQProvider(
                JingleIQ.ELEMENT_NAME,
                JingleIQ.NAMESPACE,
                new JingleIQProvider());

        this.participant = new Participant();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                participant.join(
                        MeetClient.this, domain, domain, fullMuc, "androidtester");
            }
        }).start();
    }

    /**
     * Disconnect.
     */
    public void disconnect()
    {
        if (participant != null)
        {
            participant.disconnect();
            participant = null;
        }
    }

    public boolean isInitiator()
    {
        return appRTCSignalingParameters.initiator;
    }

    public MediaConstraints pcConstraints()
    {
        return appRTCSignalingParameters.pcConstraints;
    }

    public MediaConstraints videoConstraints()
    {
        return appRTCSignalingParameters.videoConstraints;
    }

    public MediaConstraints audioConstraints()
    {
        return appRTCSignalingParameters.audioConstraints;
    }

    // Struct holding the signaling parameters of an AppRTC room.
    private class AppRTCSignalingParameters
    {
        public final List<PeerConnection.IceServer> iceServers;
        public final String gaeBaseHref;
        public final String channelToken;
        public final String postMessageUrl;
        public final boolean initiator;
        public final MediaConstraints pcConstraints;
        public final MediaConstraints videoConstraints;
        public final MediaConstraints audioConstraints;

        public AppRTCSignalingParameters(
                List<PeerConnection.IceServer> iceServers,
                String gaeBaseHref, String channelToken, String postMessageUrl,
                boolean initiator, MediaConstraints pcConstraints,
                MediaConstraints videoConstraints, MediaConstraints audioConstraints)
        {
            this.iceServers = iceServers;
            this.gaeBaseHref = gaeBaseHref;
            this.channelToken = channelToken;
            this.postMessageUrl = postMessageUrl;
            this.initiator = initiator;
            this.pcConstraints = pcConstraints;
            this.videoConstraints = videoConstraints;
            this.audioConstraints = audioConstraints;
        }
    }
}
