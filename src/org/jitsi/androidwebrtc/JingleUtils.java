package org.jitsi.androidwebrtc;

import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.webrtc.*;

import java.net.*;
import java.util.*;

/**
 * Created by boris on 27/01/15.
 */
public class JingleUtils
{
    private static final String NL = "\n";
    public static SessionDescription toSdp(JingleIQ iq, String type)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("v=0").append(NL);
        sb.append("o=- 1923518516 2 IN IP4 0.0.0.0").append(NL);
        sb.append("s=-").append(NL);
        sb.append("t=0 0").append(NL);
        sb.append("a=group:BUNDLE audio video").append(NL);

        for (ContentPacketExtension cpe : iq.getContentList())
        {
            if(!"data".equals(cpe.getName()))
                appendMLine(cpe, sb);
        }

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
            sb.append("a=extmap:").append(ext.getID()).append(' ').append(ext.getURI()).append(NL);
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

    public static JingleIQ toJingle(SessionDescription sdp)
    {
        JingleIQ iq = new JingleIQ();
        iq.setAction(JingleAction.SESSION_ACCEPT);

        ContentPacketExtension audioContent = createContentForMedia("audio", sdp);
        ContentPacketExtension videoContent = createContentForMedia("video", sdp);
        iq.addContent(audioContent);
        iq.addContent(videoContent);

        return iq;
    }

        private static ContentPacketExtension createContentForMedia(
                String mediaType, SessionDescription sdp)
        {
            ContentPacketExtension content
                    = new ContentPacketExtension(
                    ContentPacketExtension.CreatorEnum.initiator,
                    mediaType);

            content.setSenders(ContentPacketExtension.SendersEnum.both);

            // FIXME: re-use Format and EncodingConfiguration
            // to construct the offer
            if (mediaType.equals("audio"))
            {
                RtpDescriptionPacketExtension rtpDesc
                        = new RtpDescriptionPacketExtension();

                rtpDesc.setMedia("audio");

                RTPHdrExtPacketExtension ssrcAudioLevel
                        = new RTPHdrExtPacketExtension();
                ssrcAudioLevel.setID("1");
                ssrcAudioLevel.setURI(
                        URI.create("urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                rtpDesc.addExtmap(ssrcAudioLevel);

                // a=rtpmap:111 opus/48000/2
                PayloadTypePacketExtension opus
                        = new PayloadTypePacketExtension();
                opus.setId(111);
                opus.setName("opus");
                opus.setClockrate(48000);
                opus.setChannels(2);
                rtpDesc.addPayloadType(opus);
                // fmtp:111 minptime=10
                ParameterPacketExtension opusMinptime
                        = new ParameterPacketExtension();
                opusMinptime.setName("minptime");
                opusMinptime.setValue("10");
                opus.addParameter(opusMinptime);
                // a=rtpmap:103 ISAC/16000
                PayloadTypePacketExtension isac16
                        = new PayloadTypePacketExtension();
                isac16.setId(103);
                isac16.setName("ISAC");
                isac16.setClockrate(16000);
                rtpDesc.addPayloadType(isac16);
                // a=rtpmap:104 ISAC/32000
                PayloadTypePacketExtension isac32
                        = new PayloadTypePacketExtension();
                isac32.setId(104);
                isac32.setName("ISAC");
                isac32.setClockrate(32000);
                rtpDesc.addPayloadType(isac32);
                // a=rtpmap:0 PCMU/8000
                PayloadTypePacketExtension pcmu
                        = new PayloadTypePacketExtension();
                pcmu.setId(0);
                pcmu.setName("PCMU");
                pcmu.setClockrate(8000);
                rtpDesc.addPayloadType(pcmu);
                // a=rtpmap:8 PCMA/8000
                PayloadTypePacketExtension pcma
                        = new PayloadTypePacketExtension();
                pcma.setId(8);
                pcma.setName("PCMA");
                pcma.setClockrate(8000);
                rtpDesc.addPayloadType(pcma);
                // a=rtpmap:106 CN/32000
                PayloadTypePacketExtension cn
                        = new PayloadTypePacketExtension();
                cn.setId(106);
                cn.setName("CN");
                cn.setClockrate(32000);
                rtpDesc.addPayloadType(cn);
                // a=rtpmap:105 CN/16000
                PayloadTypePacketExtension cn16
                        = new PayloadTypePacketExtension();
                cn16.setId(105);
                cn16.setName("CN");
                cn16.setClockrate(16000);
                rtpDesc.addPayloadType(cn16);
                // a=rtpmap:13 CN/8000
                PayloadTypePacketExtension cn8
                        = new PayloadTypePacketExtension();
                cn8.setId(13);
                cn8.setName("CN");
                cn8.setClockrate(8000);
                rtpDesc.addPayloadType(cn8);
                // rtpmap:126 telephone-event/8000
                PayloadTypePacketExtension teleEvent
                        = new PayloadTypePacketExtension();
                teleEvent.setId(126);
                teleEvent.setName("telephone-event");
                teleEvent.setClockrate(8000);
                rtpDesc.addPayloadType(teleEvent);
                // a=maxptime:60
                rtpDesc.setAttribute("maxptime", "60");
                content.addChildExtension(rtpDesc);
            }
            else if (mediaType.equals("video"))
            {
                RtpDescriptionPacketExtension rtpDesc
                        = new RtpDescriptionPacketExtension();

                rtpDesc.setMedia("video");

                // a=extmap:2 urn:ietf:params:rtp-hdrext:toffset
                RTPHdrExtPacketExtension toOffset
                        = new RTPHdrExtPacketExtension();
                toOffset.setID("2");
                toOffset.setURI(
                        URI.create("urn:ietf:params:rtp-hdrext:toffset"));
                rtpDesc.addExtmap(toOffset);
                // a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time
                RTPHdrExtPacketExtension absSendTime
                        = new RTPHdrExtPacketExtension();
                absSendTime.setID("3");
                absSendTime.setURI(
                        URI.create(
                                "http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                rtpDesc.addExtmap(absSendTime);
                // a=rtpmap:100 VP8/90000
                PayloadTypePacketExtension vp8
                        = new PayloadTypePacketExtension();
                vp8.setId(100);
                vp8.setName("VP8");
                vp8.setClockrate(90000);
                rtpDesc.addPayloadType(vp8);
                // a=rtcp-fb:100 ccm fir
                /*
                RtcpFbPacketExtension ccmFir = new RtcpFbPacketExtension();
                ccmFir.setFeedbackType("ccm");
                ccmFir.setFeedbackSubtype("fir");
                vp8.addRtcpFeedbackType(ccmFir);
                // a=rtcp-fb:100 nack
                RtcpFbPacketExtension nack = new RtcpFbPacketExtension();
                nack.setFeedbackType("nack");
                vp8.addRtcpFeedbackType(nack);
                if (!enableFirefoxHacks)
                {
                    // a=rtcp-fb:100 goog-remb
                    RtcpFbPacketExtension remb = new RtcpFbPacketExtension();
                    remb.setFeedbackType("goog-remb");
                    vp8.addRtcpFeedbackType(remb);
                }
                 */
                // a=rtpmap:116 red/90000
                PayloadTypePacketExtension red
                        = new PayloadTypePacketExtension();
                red.setId(116);
                red.setName("red");
                red.setClockrate(90000);
                rtpDesc.addPayloadType(red);
                // a=rtpmap:117 ulpfec/90000
                PayloadTypePacketExtension ulpfec
                        = new PayloadTypePacketExtension();
                ulpfec.setId(117);
                ulpfec.setName("ulpfec");
                ulpfec.setClockrate(90000);
                rtpDesc.addPayloadType(ulpfec);

                content.addChildExtension(rtpDesc);
            }
            else
                return null;

            IceUdpTransportPacketExtension transport = new IceUdpTransportPacketExtension();
            /*
            for (String c : getCandidateLines(mediaType, sdp))
            {
                String foundation = (c.split(":")[1]).split(" ")[0];
                String component = c.split(" ")[1];
                String protocol = c.split(" ")[2];
                String priority = c.split(" ")[3];
                String addr = c.split(" ")[4];
                String port = c.split(" ")[5];
                String typ = c.split(" ")[7];
                String generation = c.split(" ")[9];

                CandidatePacketExtension cpe = new CandidatePacketExtension();
                cpe.setPort(Integer.valueOf(port));
                cpe.setFoundation(foundation);
                cpe.setProtocol(protocol);
                cpe.setPriority(Long.valueOf(priority));
                cpe.setComponent(Integer.valueOf(component));
                cpe.setIP(addr);
                if ("host".equals(typ))
                    cpe.setType(CandidateType.host);
                else
                    continue; //skip non-host...
                cpe.setGeneration(Integer.valueOf(generation));

                transport.addCandidate(cpe);
            }
            content.addChildExtension(transport);
            */

            return content;
        }

    public static JingleIQ createTransportInfo(String jid, IceCandidate candidate)
    {
        JingleIQ iq = new JingleIQ();
        iq.setAction(JingleAction.TRANSPORT_INFO);
        iq.setTo(jid);

        ContentPacketExtension content
                = new ContentPacketExtension(
                ContentPacketExtension.CreatorEnum.initiator,
                candidate.sdpMid);
        IceUdpTransportPacketExtension transport = new IceUdpTransportPacketExtension();
        CandidatePacketExtension cpe = new CandidatePacketExtension();


        String c = candidate.sdp;
        String foundation = (c.split(":")[1]).split(" ")[0];
        String component = c.split(" ")[1];
        String protocol = c.split(" ")[2];
        String priority = c.split(" ")[3];
        String addr = c.split(" ")[4];
        String port = c.split(" ")[5];
        String typ = c.split(" ")[7];
        String generation = c.split(" ")[9];

        cpe.setPort(Integer.valueOf(port));
        cpe.setFoundation(foundation);
        cpe.setProtocol(protocol);
        cpe.setPriority(Long.valueOf(priority));
        cpe.setComponent(Integer.valueOf(component));
        cpe.setIP(addr);
        if ("host".equals(typ))
            cpe.setType(CandidateType.host);
        cpe.setGeneration(Integer.valueOf(generation));

        transport.addCandidate(cpe);


        transport.addCandidate(cpe);
        content.addChildExtension(transport);
        iq.addContent(content);

        return iq;
    }


private static List<String> getCandidateLines(String mediaType, SessionDescription sdp)
{
    String[] lines = sdp.description.split("\n");
    LinkedList<String> ret = new LinkedList<String>();

    boolean in = false;
    for (String s : lines)
    {
        if ( ("m="+mediaType).equals(s))
        {
            in = true;
            continue;
        }
        if (!in) continue;
        if (s.startsWith("m="))
            return ret;
        if (s.startsWith("a=candida"))
            if (!s.contains("relay")) //skip relay cands
                ret.add(s);
    }

    return ret;

}
}
