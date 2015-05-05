package org.jitsi.androidwebrtc.meet;

import android.util.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jitsi.androidwebrtc.meet.util.*;
import org.jitsi.util.*;
import org.jivesoftware.smack.packet.*;
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
            if(!"data".equals(cpe.getName())
                    // FIXME: duplicate empty ContentPacketExtensions
                    && cpe.getAttributeNames().size() != 1
                    )
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
                            .append(":").append(ppe.getValue()).append("\n");
                }
            }

            outputSdp.append(builder.toString());
        }

        return new SessionDescription(sdp.type, outputSdp.toString());
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

            RtpDescriptionPacketExtension d;
            // FIXME: re-use Format and EncodingConfiguration
            // to construct the offer
            if (mediaType.equals("audio"))
            {
                RtpDescriptionPacketExtension rtpDesc
                        = new RtpDescriptionPacketExtension();
                d = rtpDesc;

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
                d = rtpDesc;

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

                /*
a=ssrc:2928659107 cname:mixed
a=ssrc:2928659107 label:mixedlabelaudio0
a=ssrc:2928659107 msid:mixedmslabel mixedlabelaudio0
a=ssrc:2928659107 mslabel:mixedmslabel
                 */
            String cname = null, label = null, msid = null, mslabel = null,ssrc = null;
            for (String line : getMediaSsrcLines(mediaType, sdp))
            {
                if (ssrc == null)
                    ssrc = line.split(" ")[0].split(":")[1];
                String k = line.split(" ")[1].split(":")[0];
                String v = line.split(" ")[1].split(":")[1];
                if ("cname".equals(k))
                    cname = v.trim();
                else if ("label".equals(k))
                    label = v.trim();
                else if ("msid".equals(k))
                    msid = v.trim();
                else if ("mslabel".equals(k))
                    mslabel = v.trim();
            }
            if (msid != null)
                msid += " " + label.trim();

            SourcePacketExtension spe = new SourcePacketExtension();
            spe.setSSRC(Long.valueOf(ssrc));

            ParameterPacketExtension p = new ParameterPacketExtension();

            p.setName("cname");
            p.setValue(cname);
            spe.addParameter(p);

            p = new ParameterPacketExtension();
            p.setName("label");
            p.setValue(label);
            spe.addParameter(p);

            p = new ParameterPacketExtension();
            p.setName("msid");
            p.setValue(msid);
            spe.addParameter(p);

            p = new ParameterPacketExtension();
            p.setName("mslabel");
            p.setValue(mslabel);
            spe.addParameter(p);

            if (d !=null)
                d.addChildExtension(spe);

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
            */
            String ufrag=null, icepwd=null, fp=null;
            for (String line : getMediaIceLines(mediaType, sdp))
            {
                if (line.contains("frag"))
                    ufrag=line.split(":")[1].trim();
                else if (line.contains("pwd"))
                    icepwd=line.split(":")[1].trim();
                else if (line.contains("nger"))
                    fp=line.split(" ")[1].trim();
            }
            transport.setPassword(icepwd);
            transport.setUfrag(ufrag);
            DtlsFingerprintPacketExtension f = new DtlsFingerprintPacketExtension();
            f.setFingerprint(fp);
            f.setHash("sha-1");
            transport.addChildExtension(f);
            content.addChildExtension(transport);

            return content;
        }

    public static JingleIQ createTransportInfo(String jid, IceCandidate candidate)
    {
        JingleIQ iq = new JingleIQ();
        iq.setAction(JingleAction.TRANSPORT_INFO);
        iq.setTo(jid);
        iq.setType(IQ.Type.SET);

        ContentPacketExtension content
                = new ContentPacketExtension(
                ContentPacketExtension.CreatorEnum.initiator,
                candidate.sdpMid);
        IceUdpTransportPacketExtension transport = new IceUdpTransportPacketExtension();
        CandidatePacketExtension cpe = new CandidatePacketExtension();


        String c = candidate.sdp;
        if(c.contains("tcp"))
            return null;
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


private static List<String> getMediaSsrcLines(String mediaType, SessionDescription sdp)
{
    String[] lines = sdp.description.split("\n");
    Log.i("some", "SDP LINES: " + lines.length);
    LinkedList<String> ret = new LinkedList<String>();

    boolean in = false;
    for (String s : lines)
    {
        if (s.startsWith("m="+mediaType))
        {
            in = true;
            continue;
        }
        if (!in) continue;
        if (s.startsWith("m="))
            return ret;
        if (s.startsWith("a=ssrc"))
            ret.add(s);
    }

    return ret;

}
    private static List<String> getMediaIceLines(String mediaType, SessionDescription sdp)
    {
        String[] lines = sdp.description.split("\n");
        Log.i("some", "SDP LINES: " + lines.length);
        LinkedList<String> ret = new LinkedList<String>();

        boolean in = false;
        for (String s : lines)
        {
            if (s.startsWith("m="+mediaType))
            {
                in = true;
                continue;
            }
            if (!in) continue;
            if (s.startsWith("m="))
                return ret;
            if (s.startsWith("a=ice") || s.startsWith("a=finge"))
                ret.add(s);
        }

        return ret;

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
            String[] mediaLines = mediaPart.split("\n");

            boolean first = true;
            for (String line : mediaLines)
            {
                if (first)
                {
                    mediaPartOut.append("m=").append(line).append("\n");
                    first = false;
                    continue;
                }
                if (!line.startsWith("a=ssrc:"))
                {
                    mediaPartOut.append(line).append("\n");
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
                    mediaPartOut.append(line).append("\n");
            }
            outputSdp.append(mediaPartOut.toString());
        }

        return new SessionDescription(sdp.type, outputSdp.toString());
    }
}
