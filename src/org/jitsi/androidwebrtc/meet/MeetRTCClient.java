package org.jitsi.androidwebrtc.meet;

import android.util.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jitsi.androidwebrtc.*;
import org.jitsi.androidwebrtc.meet.util.*;
import org.jivesoftware.smack.provider.*;
import org.webrtc.*;

import java.util.*;

/**
 *
 */
public class MeetRTCClient
    implements AppRTCClient
{
    private final static String TAG = "MeetRTCClient";

    private final CallActivity callActivity;
    private Participant participant;
    private boolean answerSent;

    public MeetRTCClient(CallActivity callActivity)
    {
        this.callActivity = callActivity;
    }

    @Override
    public void connectToRoom(RoomConnectionParameters connectionParameters)
    {
        String url = connectionParameters.roomId;
        Log.i(TAG, "URL: '" + url);
        final String domain = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/"));
        String room = url.substring(url.lastIndexOf("/") + 1);
        final String fullMuc = room + "@conference." + domain;
        Log.i(TAG, "Domain: '" + domain + "' room: '" + room + "' muc: '" + fullMuc + "'");

        // FIXME: figure out constraints
        /*MediaConstraints pcConstraints = new MediaConstraints();
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
            new MediaConstraints.KeyValuePair("googAutoGainControl2", "true"));*/

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
                    MeetRTCClient.this, domain, domain, fullMuc, "androidtester");
            }
        }).start();
    }

    private SignalingParameters prepareSignalingParams(SessionDescription bridgeOfferSdp)
    {
        boolean initiator = false;

        // We provide empty ice servers and candidates lists, they should be
        // extracted from the remote description
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        LinkedList<IceCandidate> iceCandidates = new LinkedList<>();

        SignalingParameters params = new SignalingParameters(
            iceServers, initiator,
            //clientId,// wssUrl, wssPostUrl,
            bridgeOfferSdp, iceCandidates);

        return params;
    }

    public void acceptSessionInit(SessionDescription bridgeOfferSdp)
    {
        SignalingParameters signalingParameters
            = prepareSignalingParams(bridgeOfferSdp);

        callActivity.onConnectedToRoom(signalingParameters);
        //pcClient.setRemoteDescription(bridgeOfferSdp);
    }

    public void onSourceAdd(MediaSSRCMap addedSSRCs)
    {
        SessionDescription rsd = callActivity.getRemoteDescription();

        SessionDescription modifiedOffer
            = JingleUtils.addSSRCs(rsd, addedSSRCs);

        Log.i(TAG, "SOURCE ADD OFFER: " + modifiedOffer.description);

        callActivity.setRemoteDescription(modifiedOffer);
    }

    public void onSourceRemove(MediaSSRCMap removedSSRCs)
    {
        SessionDescription rsd = callActivity.getRemoteDescription();

        SessionDescription modifiedOffer
            = JingleUtils.removeSSRCs(rsd, removedSSRCs);

        Log.i(TAG, "SOURCE REMOVE OFFER: " + modifiedOffer.description);

        callActivity.setRemoteDescription(modifiedOffer);
    }

    @Override
    public void sendOfferSdp(SessionDescription sdp)
    {
        Log.e(TAG, "We do not send offer ! - ever!!");
    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp)
    {
        if (!answerSent)
        {
            participant.sendSessionAccept(sdp);
        }
        answerSent = true;
    }

    @Override
    public void sendLocalIceCandidate(IceCandidate candidate)
    {
        participant.sendTransportInfo(candidate);
    }

    /**
     * Disconnect.
     */
    private void disconnect()
    {
        if (participant != null)
        {
            participant.disconnect();
            participant = null;
        }
    }

    @Override
    public void disconnectFromRoom()
    {
        disconnect();
    }
}
