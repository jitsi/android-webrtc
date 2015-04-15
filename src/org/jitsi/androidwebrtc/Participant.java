package org.jitsi.androidwebrtc;

import android.util.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jitsimeet.*;
import org.jitsi.androidwebrtc.util.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.muc.*;
import org.jivesoftware.smackx.packet.*;
import org.webrtc.*;

import java.util.*;

/**
 * Created by boris on 27/01/15.
 */
public class Participant
        implements PacketListener
{
    private final String TAG = "Participant";

    private String xmppHostname;
    private String xmppDomain;
    private int port = 5222;
    private XMPPConnection connection;
    MultiUserChat muc;
    private SessionDescription bridgeOfferSdp;
    private AppRTCClient rtcClient;
    private String offererJid = null;
    private String sid;

    public void join(AppRTCClient rtcClient, String xmppHostname, String xmppDomain, String mucJid, String nickname)
    {
        this.rtcClient = rtcClient;
        this.xmppHostname = xmppHostname;
        this.xmppDomain = xmppDomain;
        connect();
        joinMuc(mucJid, nickname);
    }

    private void connect()
    {
        System.err.println("connect");
        ConnectionConfiguration config = new ConnectionConfiguration(
                xmppHostname,
                port,
                xmppDomain);
        //config.setDebuggerEnabled(true);

        connection = new XMPPConnection(config);

        connection.addPacketListener(new PacketListener()
        {
            @Override
            public void processPacket(Packet packet)
            {
                logLongString("XMPP", "<- " + packet.toXML());
            }
        }, new PacketFilter()
        {
            @Override
            public boolean accept(Packet packet)
            {
                return true;
            }
        });

        connection.addPacketSendingListener(new PacketListener()
        {
            @Override
            public void processPacket(Packet packet)
            {
                logLongString("XMPP", "-> " + packet.toXML());
            }
        }, new PacketFilter()
        {
            @Override
            public boolean accept(Packet packet)
            {
                return true;
            }
        });

        connection.addPacketListener(this, new PacketFilter()
        {
            public boolean accept(Packet packet)
            {
                return (packet instanceof JingleIQ);
            }
        });

        try
        {
            System.err.println("connect2");
            connection.connect();
            connection.loginAnonymously();
            System.err.println("logged in");

            /*ServiceDiscoveryManager discoManager
                = ServiceDiscoveryManager.getInstanceFor(connection);
            discoManager.addFeature("urn:xmpp:jingle:apps:rtp:audio");
            discoManager.addFeature("urn:xmpp:jingle:apps:rtp:video");
            discoManager.addFeature("urn:xmpp:jingle:transports:ice-udp:1");
            discoManager.addFeature("urn:xmpp:jingle:transports:dtls-sctp:1");
            discoManager.addFeature("urn:ietf:rfc:5761");
            discoManager.addFeature("urn:ietf:rfc:5888");*/
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error connecting XMPP", e);
        }
    }

    private void logLongString(String tag, String message)
    {
        int partLen = 1024;
        if (message.length() < partLen)
        {
            Log.d(tag, message);
        }
        else
        {
            int parts = message.length() / partLen;
            int mod = message.length() % partLen;
            for (int i = 0; i < parts; i++)
            {
                Log.d(tag,
                        "PART " + i + ": "
                                + message.substring(i * partLen, (i + 1) * partLen));
            }
            if (mod > 0)
            {
                Log.d(tag,
                        "PART " + parts + ": "
                                + message.substring(parts * partLen, message.length()));
            }
        }
    }

    private void joinMuc(String roomURL, String nickname)
    {
        muc = new MultiUserChat(connection, roomURL);

        muc.addPresenceInterceptor(new PresenceInterceptor());

        while (true)
        {
            try
            {
                muc.join(nickname);

                muc.sendMessage("Hello World! " + nickname);
                System.err.println("Hello World! " + nickname);

                /*
                 * Send a Presence packet containing a Nick extension so that the
                 * nickname is correctly displayed in jitmeet
                 */
                Packet presencePacket = new Presence(Presence.Type.available);
                presencePacket.setTo(roomURL + "/" + nickname);
                presencePacket.addExtension(new Nick(nickname));
                connection.sendPacket(presencePacket);
            }
            catch (XMPPException e)
            {
                /*
                 * IF the nickname is already taken in the MUC (code 409)
                 * then we append '_' to the nickname, and retry
                 */
                if ((e.getXMPPError() != null) && (e.getXMPPError().getCode() == 409))
                {
                    Log.e(TAG, nickname + " nickname already used, "
                            + "changing to " + nickname + '_');
                    nickname = nickname + '_';
                    continue;
                }
                else
                {
                    Log.e(TAG, nickname + " : could not enter MUC " + e);
                    muc = null;
                }
            }
            break;
        }
    }

    @Override
    public void processPacket(Packet packet)
    {
        try
        {
            JingleIQ jiq = (JingleIQ) packet;
            ackJingleIQ(jiq);
            switch (jiq.getAction())
            {
                case SESSION_INITIATE:
                    System.err.println(" : Jingle session-initiate " +
                            "received");
                    this.bridgeOfferSdp = JingleUtils.toSdp(jiq, "offer");
                    offererJid = jiq.getFrom();
                    sid = jiq.getSID();
                    Log.d(TAG, bridgeOfferSdp.description);

                    rtcClient.acceptSessionInit(bridgeOfferSdp);

                    break;
                case SOURCEADD:
                case ADDSOURCE:
                    Log.i(TAG, "SOURCE ADD: " + jiq.toXML());
                    break;
                case REMOVESOURCE:
                case SOURCEREMOVE:
                    Log.i(TAG, "REMOVE SOURCE: " + jiq.toXML());
                    break;
                default:
                    System.err.println(" : Unknown Jingle IQ received : "
                            + jiq.toString());
                    break;
            }
        }
        catch (Exception e)
        {

            Log.e(TAG, "Error", e);
        }
    }

    /**
     * This function simply create an ACK packet to acknowledge the Jingle IQ
     * packet <tt>packetToAck</tt>.
     *
     * @param packetToAck the <tt>JingleIQ</tt> that need to be acknowledge.
     */
    private void ackJingleIQ(JingleIQ packetToAck)
    {
        IQ ackPacket = IQ.createResultIQ(packetToAck);
        connection.sendPacket(ackPacket);
    }

    public void disconnect()
    {
        if (muc != null)
        {
            muc.leave();
            muc = null;
        }
        if (connection != null)
        {
            connection.disconnect();
            connection = null;
        }
    }

    public SessionDescription getBridgeOfferSdp()
    {
        return bridgeOfferSdp;
    }

    public void sendSessionAccept(SessionDescription sdp)
    {
        JingleIQ sessionAccept = JingleUtils.toJingle(sdp);
        sessionAccept.setTo(offererJid);
        sessionAccept.setSID(sid);
        sessionAccept.setType(IQ.Type.SET);
        Log.i(TAG, sessionAccept.toXML());

        addStreamsToPresence(sessionAccept.getContentList());

        connection.sendPacket(sessionAccept);
    }

    private void addStreamsToPresence(List<ContentPacketExtension> contentList)
    {
        MediaSSRCMap mediaSSRCs = MediaSSRCMap.getSSRCsFromContent(contentList);

        MediaPresenceExtension mediaPresence
                = new MediaPresenceExtension();

        for (SourcePacketExtension audioSSRC
                : mediaSSRCs.getSSRCsForMedia("audio"))
        {
            MediaPresenceExtension.Source ssrc
                    = new MediaPresenceExtension.Source();
            ssrc.setMediaType("audio");
            ssrc.setSSRC(String.valueOf(audioSSRC.getSSRC()));

            mediaPresence.addChildExtension(ssrc);
        }

        for (SourcePacketExtension videoSSRC
                : mediaSSRCs.getSSRCsForMedia("video"))
        {
            MediaPresenceExtension.Source ssrc
                    = new MediaPresenceExtension.Source();
            ssrc.setMediaType("video");
            ssrc.setSSRC(String.valueOf(videoSSRC.getSSRC()));

            mediaPresence.addChildExtension(ssrc);
        }

        sendPresenceExtension(mediaPresence);
    }

    private Presence lastPresenceSent = null;

    private class PresenceInterceptor
            implements PacketInterceptor
    {
        /**
         * {@inheritDoc}
         * <p/>
         * Adds <tt>this.publishedConferenceExt</tt> as the only
         * <tt>ConferenceAnnouncementPacketExtension</tt> of <tt>packet</tt>.
         */
        @Override
        public void interceptPacket(Packet packet)
        {
            if (packet instanceof Presence)
            {
                lastPresenceSent = (Presence) packet;
            }
        }
    }

    /**
     * Adds given <tt>PacketExtension</tt> to the MUC presence and publishes it
     * immediately.
     *
     * @param extension the <tt>PacketExtension</tt> to be included in MUC
     *                  presence.
     */
    public void sendPresenceExtension(PacketExtension extension)
    {
        if (lastPresenceSent != null)
        {
            setPacketExtension(
                    lastPresenceSent, extension, extension.getNamespace());

            connection.sendPacket(lastPresenceSent);
        }
    }

    private static void setPacketExtension(
            Packet packet,
            PacketExtension extension,
            String namespace)
    {
        if (namespace == null || namespace.isEmpty())
        {
            return;
        }

        //clear previous announcements
        PacketExtension pe;
        while (null != (pe = packet.getExtension(namespace)))
        {
            packet.removeExtension(pe);
        }

        if (extension != null)
        {
            packet.addExtension(extension);
        }
    }

    private static class Observer
            implements PeerConnection.Observer
    {

        @Override
        public void onSignalingChange(PeerConnection.SignalingState newState)
        {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState newState)
        {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState
                                                 newState)
        {

        }

        @Override
        public void onIceCandidate(IceCandidate candidate)
        {

        }

        @Override
        public void onError()
        {

        }

        @Override
        public void onAddStream(MediaStream stream)
        {

        }

        @Override
        public void onRemoveStream(MediaStream stream)
        {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel)
        {

        }

        @Override
        public void onRenegotiationNeeded()
        {

        }
    }

    public void sendTransportInfo(IceCandidate candidate)
    {
        JingleIQ iq = JingleUtils.createTransportInfo(offererJid, candidate);
        if (iq != null)
        {
            iq.setSID(sid);
            connection.sendPacket(iq);
            Log.i(TAG, "transport-info: " + iq.toXML());
        }
    }
}

