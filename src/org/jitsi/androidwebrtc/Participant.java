package org.jitsi.androidwebrtc;

import android.util.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.muc.*;
import org.jivesoftware.smackx.packet.*;
import org.webrtc.*;

/**
 * Created by boris on 27/01/15.
 */
public class Participant implements PacketListener
{
    private final String TAG = "Participant";

    private String xmppHostname;
    private String xmppDomain;
    private int port = 5222;
    private XMPPConnection connection;
    MultiUserChat muc;

    public void join(String xmppHostname, String xmppDomain, String mucJid, String nickname)
    {
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
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error connecting XMPP", e);
        }
    }

    private void joinMuc(String roomURL, String nickname)
    {
        muc = new MultiUserChat(connection, roomURL);
        while(true)
        {
            try
            {
                muc.join(nickname);

                muc.sendMessage("Hello World! "+nickname);
                System.err.println("Hello World! "+nickname);

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
                if((e.getXMPPError() != null) && (e.getXMPPError().getCode() == 409))
                {
                    Log.e(TAG, nickname + " nickname already used, "
                                        + "changing to " + nickname + '_');
                    nickname=nickname+'_';
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
        JingleIQ jiq = (JingleIQ)packet;
        ackJingleIQ(jiq);
        switch(jiq.getAction())
        {
            case SESSION_INITIATE:
                System.err.println(" : Jingle session-initiate " +
                                           "received");
                    acceptJingleSession(jiq);
            default:
                System.err.println(" : Unknown Jingle IQ received : "
                                    + jiq.toString());
                break;
        }
    }

    private void acceptJingleSession(JingleIQ jiq)
    {
        System.err.println("accept jingle " + jiq.toXML());


    }

    /**
     * This function simply create an ACK packet to acknowledge the Jingle IQ
     * packet <tt>packetToAck</tt>.
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

    private static class Observer implements PeerConnection.Observer
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
}
