/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.androidwebrtc.meet;

import android.util.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jitsi.androidwebrtc.meet.util.*;
import org.jitsi.util.*;
import org.webrtc.*;

/**
 * Class contains utility method for converting from Jingle IQ to WebRTC SDP
 * equivalent.
 *
 * @author Boris Grozev
 * @author Pawel Domas
 */
public class JingleToSdp
{
    /**
     * New line constant character.
     */
    private static final String NL = "\r\n";

    /**
     * Tag used for Android logging
     */
    private static final String TAG = "JingleToSdp";

    public static SessionDescription toSdp(JingleIQ iq,
                                           SessionDescription.Type type)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("v=0").append(NL);
        sb.append("o=- 1923518516 2 IN IP4 0.0.0.0").append(NL);
        sb.append("s=-").append(NL);
        sb.append("t=0 0").append(NL);
        sb.append("a=group:BUNDLE audio video data").append(NL);

        for (ContentPacketExtension cpe : iq.getContentList())
        {
            if (cpe.getAttributeNames().size() == 1)
            {
                // FIXME: duplicate empty ContentPacketExtensions
                continue;
            }

            if(!"data".equals(cpe.getName()))
            {
                appendMLine(cpe, sb);
            }
            else
            {
                appendSCTPLines(cpe, sb);
            }
        }

        return new SessionDescription(type, sb.toString());
    }

    private static void appendSCTPLines(ContentPacketExtension cpe,
                                        StringBuilder sb)
    {
        String contentName = cpe.getName();
        RtpDescriptionPacketExtension rdpe
            = cpe.getFirstChildOfType(RtpDescriptionPacketExtension.class);
        if (rdpe == null)
        {
            Log.d(TAG, "No RtpDescPacketExtension");
            return;
        }

        IceUdpTransportPacketExtension transport
            = cpe.getFirstChildOfType(IceUdpTransportPacketExtension.class);
        if (transport == null)
        {
            Log.d(TAG, "No ICE packet extension");
            return;
        }

        SctpMapExtension sctpMapExtension
            = transport.getFirstChildOfType(SctpMapExtension.class);
        if (sctpMapExtension == null)
        {
            Log.d(TAG, "No SctpMap packet extension");
            return;
        }
        int sctpPort = sctpMapExtension.getPort();
        // m=application 1 DTLS/SCTP 5000
        sb.append("m=").append(rdpe.getMedia()).append(" 1 DTLS/SCTP ")
            .append(sctpPort).append(NL);
        // a=sctpmap:5000 webrtc-datachannel 1024
        sb.append("a=sctpmap:").append(sctpPort)
            .append(" ").append(sctpMapExtension.getProtocol())
            .append(" ").append(sctpMapExtension.getStreams()).append(NL);

        // c=IN IP4 0.0.0.0 //FIXME: what is it for ?
        //sb.append("c=IN IP4 0.0.0.0");

        DtlsFingerprintPacketExtension dtls
            = transport.getFirstChildOfType(DtlsFingerprintPacketExtension.class);

        sb.append("a=ice-ufrag:").append(transport.getUfrag()).append(NL);
        sb.append("a=ice-pwd:").append(transport.getPassword()).append(NL);

        sb.append("a=fingerprint:").append(dtls.getHash()).append(' ').append(dtls.getFingerprint()).append(NL);
        sb.append("a=sendrecv").append(NL);
        sb.append("a=mid:").append(contentName).append(NL); // XXX cpe.getName or description.getMedia()?
        sb.append("a=rtcp-mux").append(NL);

        for (CandidatePacketExtension candidate : transport.getCandidateList())
        {
            sb.append("a=candidate:").append(candidate.getFoundation()).append(' ').append(candidate.getComponent());
            sb.append(' ').append(candidate.getProtocol()).append(' ').append(candidate.getPriority());
            sb.append(' ').append(candidate.getIP()).append(' ').append(candidate.getPort()).append(" typ ");
            sb.append(candidate.getType().toString()).append(" generation ").append(candidate.getGeneration());
            sb.append(NL);
        }
    }

    private static void appendMLine(ContentPacketExtension cpe, StringBuilder sb)
    {
        RtpDescriptionPacketExtension description
                = cpe.getFirstChildOfType(RtpDescriptionPacketExtension.class);

        IceUdpTransportPacketExtension transport
                = cpe.getFirstChildOfType(IceUdpTransportPacketExtension.class);

        DtlsFingerprintPacketExtension dtls
                = transport.getFirstChildOfType(DtlsFingerprintPacketExtension.class);

        sb.append("m=").append(cpe.getName()).append(" 1 RTP/SAVPF");
        for (PayloadTypePacketExtension pt : description.getPayloadTypes())
        {
            sb.append(" ").append(pt.getID());
        }
        sb.append(NL);

        sb.append("c=IN IP4 0.0.0.0").append(NL);
        sb.append("a=rtcp:1 IN IP4 0.0.0.0").append(NL);

        sb.append("a=ice-ufrag:").append(transport.getUfrag()).append(NL);
        sb.append("a=ice-pwd:").append(transport.getPassword()).append(NL);

        sb.append("a=fingerprint:").append(dtls.getHash()).append(' ').append(dtls.getFingerprint()).append(NL);
        sb.append("a=sendrecv").append(NL);
        sb.append("a=mid:").append(cpe.getName()).append(NL); // XXX cpe.getName or description.getMedia()?
        sb.append("a=rtcp-mux").append(NL);

        for (PayloadTypePacketExtension pt : description.getPayloadTypes())
        {
            sb.append("a=rtpmap:").append(pt.getID()).append(' ').append(pt.getName()).append('/').append(pt.getClockrate());
            if (pt.getChannels() != 1)
                sb.append('/').append(pt.getChannels());
            sb.append(NL);

            for (ParameterPacketExtension ppe : pt.getParameters())
                sb.append("a=fmtp:").append(pt.getID()).append(' ').append(ppe.getName()).append('=').append(ppe.getValue()).append(NL);

            for (RtcpFbPacketExtension rtcpFb
                : pt.getRtcpFeedbackTypeList())
            {
                sb.append("a=rtcp-fb:").append(pt.getID())
                    .append(" ").append(rtcpFb.getFeedbackType());
                if (!StringUtils.isNullOrEmpty(rtcpFb.getFeedbackSubtype()))
                {
                    sb.append(" ").append(rtcpFb.getFeedbackSubtype());
                }
                sb.append(NL);
            }
        }

        for (RTPHdrExtPacketExtension ext : description.getExtmapList())
        {
            sb.append("a=extmap:").append(ext.getID()).append(' ').append(ext.getURI()).append(NL);
        }

        for (SourcePacketExtension ssrc
                : description.getChildExtensionsOfType(
                        SourcePacketExtension.class))
        {
            long ssrcL = ssrc.getSSRC();
            for (ParameterPacketExtension param : ssrc.getParameters())
            {
                sb.append("a=ssrc:").append(ssrcL).append(" ")
                        .append(param.getName())
                        .append(":").append(param.getValue()).append(NL);
            }
        }

        for (CandidatePacketExtension candidate : transport.getCandidateList())
        {
            sb.append("a=candidate:").append(candidate.getFoundation()).append(' ').append(candidate.getComponent());
            sb.append(' ').append(candidate.getProtocol()).append(' ').append(candidate.getPriority());
            sb.append(' ').append(candidate.getIP()).append(' ').append(candidate.getPort()).append(" typ ");
            sb.append(candidate.getType().toString()).append(" generation ").append(candidate.getGeneration());
            sb.append(NL);
        }

    }

    public static SessionDescription addSSRCs(SessionDescription sdp,
                                              MediaSSRCMap ssrcToAdd)
    {
        String[] parts = sdp.description.split("m=");

        StringBuilder outputSdp = new StringBuilder(parts[0]);

        for (int i=1; i < parts.length; i++)
        {
            String mediaPart = parts[i];
            String media = mediaPart.substring(0, mediaPart.indexOf(" "));

            // Process only audio and video media types
            if (!"audio".equals(media) && !"video".equals(media))
            {
                outputSdp.append("m=").append(mediaPart);
                continue;
            }

            StringBuilder builder = new StringBuilder("m=").append(mediaPart);

            for (SourcePacketExtension ssrcPe
                    : ssrcToAdd.getSSRCsForMedia(media))
            {
                if (mediaPart.contains("a=ssrc:" + ssrcPe.getSSRC()))
                {
                    // SSRC is included already
                    continue;
                }

                for (ParameterPacketExtension ppe : ssrcPe.getParameters())
                {
                    builder.append("a=ssrc:").append(ssrcPe.getSSRC())
                            .append(" ").append(ppe.getName())
                            .append(":").append(ppe.getValue()).append(NL);
                }
            }

            outputSdp.append(builder.toString());
        }

        return new SessionDescription(sdp.type, outputSdp.toString());
    }

    public static SessionDescription removeSSRCs(SessionDescription sdp,
                                                 MediaSSRCMap removedSSRCs)
    {
        String[] parts = sdp.description.split("m=");

        StringBuilder outputSdp = new StringBuilder(parts[0]);

        for (int i=1; i < parts.length; i++)
        {
            String mediaPart = parts[i];
            String media = mediaPart.substring(0, mediaPart.indexOf(" "));

            // Process only audio and video media types
            if (!"audio".equals(media) && !"video".equals(media))
            {
                outputSdp.append("m=").append(mediaPart);
                continue;
            }

            StringBuilder mediaPartOut = new StringBuilder();
            String[] mediaLines = mediaPart.split(NL);

            boolean first = true;
            for (String line : mediaLines)
            {
                if (first)
                {
                    mediaPartOut.append("m=").append(line).append(NL);
                    first = false;
                    continue;
                }
                if (!line.startsWith("a=ssrc:"))
                {
                    mediaPartOut.append(line).append(NL);
                    continue;
                }
                boolean skip = false;
                for (SourcePacketExtension ssrcPe
                    : removedSSRCs.getSSRCsForMedia(media))
                {
                    if (line.startsWith("a=ssrc:" + ssrcPe.getSSRC()))
                       skip = true;
                }
                if (!skip)
                    mediaPartOut.append(line).append(NL);
            }
            outputSdp.append(mediaPartOut.toString());
        }

        return new SessionDescription(sdp.type, outputSdp.toString());
    }
}
