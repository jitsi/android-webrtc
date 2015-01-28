package org.jitsi.androidwebrtc;

import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.webrtc.*;

/**
 * Created by boris on 27/01/15.
 */
public class JingleUtils
{
    private static final String NL = "\n";
    public static SessionDescription toSdp(JingleIQ iq, String type)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("o=- 1923518516 2 IN IP4 0.0.0.0").append(NL);
        sb.append("s=-").append(NL);
        sb.append("t=0 0").append(NL);
        sb.append("a=group:BUNDLE audio video data").append(NL);

        for (ContentPacketExtension cpe : iq.getContentList())
            appendMLine(cpe, sb);

        return new SessionDescription(SessionDescription.Type.fromCanonicalForm(type),
                                      sb.toString());
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
        }

        for (RTPHdrExtPacketExtension ext : description.getExtmapList())
        {
            sb.append("extmap:").append(ext.getID()).append(' ').append(ext.getURI()).append(NL);
        }

        for (CandidatePacketExtension candidate : transport.getCandidateList())
        {
            sb.append("a=candidate:").append(candidate.getFoundation()).append(' ').append(candidate.getComponent());
            sb.append(' ').append(candidate.getProtocol()).append(' ').append(candidate.getPriority());
            sb.append(' ').append(candidate.getIP()).append(' ').append(candidate.getPort()).append(" typ");
            sb.append(candidate.getType().toString()).append(" generation ").append(candidate.getGeneration());
            sb.append(NL);
        }

    }
}
