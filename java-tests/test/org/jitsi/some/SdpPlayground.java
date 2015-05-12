package org.jitsi.some;

import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jitsi.androidwebrtc.meet.*;
import org.jitsi.androidwebrtc.meet.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.webrtc.*;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Pawel Domas
 */
@RunWith(JUnit4.class)
public class SdpPlayground
{
    @Test
    public void testSourceAdd()
    {
        MediaSSRCMap addedSSRCs = new MediaSSRCMap();

        List<SourcePacketExtension> audioSSRCs
                = addedSSRCs.getSSRCsForMedia("audio");

        long ssrc1aL = 1685798484L;
        String cname1aStr = "zjetKk5FW4QjQN2t";
        String msid1aStr = "b5db8282-1649-4216-99bb-6ec7e2d08fbf " +
                "69868593-1eff-469e-8f57-ccddb3c86c97";
        String mslabel1aStr = "b5db8282-1649-4216-99bb-6ec7e2d08fbf";
        String label1aStr = "69868593-1eff-469e-8f57-ccddb3c86c97";

        SourcePacketExtension ssrc1a = createSSRC(
                ssrc1aL, cname1aStr, msid1aStr, mslabel1aStr, label1aStr);

        audioSSRCs.add(ssrc1a);

        List<SourcePacketExtension> videoSSRCs
                = addedSSRCs.getSSRCsForMedia("video");

        long ssrc1vL = 3821067763L;
        String cname1vStr = "Vfq5fexGjrvzNRLU";
        String msid1vStr = "3cdb8846-5581-4a1f-8cbf-4ca2d7da8d5f 61d7f812-db44-447d-adc9-fb363c6f2d70";
        String mslabel1vStr = "3cdb8846-5581-4a1f-8cbf-4ca2d7da8d5f";
        String label1vStr = "61d7f812-db44-447d-adc9-fb363c6f2d70";

        SourcePacketExtension ssrc1v = createSSRC(
                ssrc1vL, cname1vStr, msid1vStr, mslabel1vStr, label1vStr);

        videoSSRCs.add(ssrc1v);

        String initialSdp = "v=0\n" +
        "o=- 1923518516 2 IN IP4 0.0.0.0\n" +
        "s=-\n" +
        "t=0 0\n" +
        "a=group:BUNDLE audio video\n" +
        "m=audio 1 RTP/SAVPF 111 103 104 0 8 106 105 13 126\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=rtcp:1 IN IP4 0.0.0.0\n" +
        "a=ice-ufrag:bf40l19j0h2ebm\n" +
        "a=ice-pwd:28rk3qqg5683ihopea3t0p92hl\n" +
        "a=fingerprint:sha-1 A0:D0:01:C6:1B:88:87:A8:DC:3B:35:3C:FC:99:D2:0B:A2:1E:4B:D0\n" +
        "a=sendrecv\n" +
        "a=mid:audio\n" +
        "a=rtcp-mux\n" +
        "a=rtpmap:111 opus/48000/2\n" +
        "a=fmtp:111 minptime=10\n" +
        "a=rtpmap:103 ISAC/16000\n" +
        "a=rtpmap:104 ISAC/32000\n" +
        "a=rtpmap:0 PCMU/8000\n" +
        "a=rtpmap:8 PCMA/8000\n" +
        "a=rtpmap:106 CN/32000\n" +
        "a=rtpmap:105 CN/16000\n" +
        "a=rtpmap:13 CN/8000\n" +
        "a=rtpmap:126 telephone-event/8000\n" +
        "a=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\n" +
        "a=ssrc:1360129237 cname:nSxK15r3d7WTB3eV\n" +
        "a=ssrc:1360129237 msid:dd7dc1b1-b4ec-47cb-8a32-487e9f566b3c dd1c4152-47d7-44de-8355-e7562cc9259b\n" +
        "a=ssrc:1360129237 mslabel:dd7dc1b1-b4ec-47cb-8a32-487e9f566b3c\n" +
        "a=ssrc:1360129237 label:dd1c4152-47d7-44de-8355-e7562cc9259b\n" +
        "a=ssrc:2210343375 cname:mixed\n" +
        "a=ssrc:2210343375 label:mixedlabelaudio0\n" +
        "a=ssrc:2210343375 msid:mixedmslabel mixedlabelaudio0\n" +
        "a=ssrc:2210343375 mslabel:mixedmslabel\n" +
        "a=candidate:1 1 udp 2130706431 192.168.1.101 10014 typ host generation 0\n" +
        "a=candidate:2 1 udp 2130706431 192.168.56.1 10014 typ host generation 0\n" +
        "a=candidate:3 1 udp 2113932031 172.24.133.4 10014 typ host generation 0\n" +
        "a=candidate:4 1 ssltcp 2113932031 192.168.1.101 443 typ host generation 0\n" +
        "a=candidate:5 1 ssltcp 2113932031 192.168.56.1 443 typ host generation 0\n" +
        "a=candidate:6 1 ssltcp 2113932031 172.24.133.4 443 typ host generation 0\n" +
        "m=video 1 RTP/SAVPF 100 116 117\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=rtcp:1 IN IP4 0.0.0.0\n" +
        "a=ice-ufrag:bf40l19j0h2ebm\n" +
        "a=ice-pwd:28rk3qqg5683ihopea3t0p92hl\n" +
        "a=fingerprint:sha-1 A0:D0:01:C6:1B:88:87:A8:DC:3B:35:3C:FC:99:D2:0B:A2:1E:4B:D0\n" +
        "a=sendrecv\n" +
        "a=mid:video\n" +
        "a=rtcp-mux\n" +
        "a=rtpmap:100 VP8/90000\n" +
        "a=rtpmap:116 red/90000\n" +
        "a=rtpmap:117 ulpfec/90000\n" +
        "a=extmap:2 urn:ietf:params:rtp-hdrext:toffset\n" +
        "a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\n" +
        "a=ssrc:4193882417 cname:11s32vjpwXYQ90eX\n" +
        "a=ssrc:4193882417 msid:386cad01-e090-47d8-93d5-3f800f0f7541 ed08cdd3-f8a6-4b06-86c5-e32026af318e\n" +
        "a=ssrc:4193882417 mslabel:386cad01-e090-47d8-93d5-3f800f0f7541\n" +
        "a=ssrc:4193882417 label:ed08cdd3-f8a6-4b06-86c5-e32026af318e\n" +
        "a=ssrc:428538435 cname:mixed\n" +
        "a=ssrc:428538435 label:mixedlabelvideo0\n" +
        "a=ssrc:428538435 msid:mixedmslabel mixedlabelvideo0\n" +
        "a=ssrc:428538435 mslabel:mixedmslabel\n" +
        "a=candidate:1 1 udp 2130706431 192.168.1.101 10014 typ host generation 0\n" +
        "a=candidate:2 1 udp 2130706431 192.168.56.1 10014 typ host generation 0\n" +
        "a=candidate:3 1 udp 2113932031 172.24.133.4 10014 typ host generation 0\n" +
        "a=candidate:4 1 ssltcp 2113932031 192.168.1.101 443 typ host generation 0\n" +
        "a=candidate:5 1 ssltcp 2113932031 192.168.56.1 443 typ host generation 0\n" +
        "a=candidate:6 1 ssltcp 2113932031 172.24.133.4 443 typ host generation 0\n";

        SessionDescription remoteSdp
            = new SessionDescription(
                SessionDescription.Type.OFFER, initialSdp);

        SessionDescription resultSdp
            = JingleToSdp.addSSRCs(remoteSdp, addedSSRCs);

        String expectedSdp = "v=0\n" +
                "o=- 1923518516 2 IN IP4 0.0.0.0\n" +
                "s=-\n" +
                "t=0 0\n" +
                "a=group:BUNDLE audio video\n" +
                "m=audio 1 RTP/SAVPF 111 103 104 0 8 106 105 13 126\n" +
                "c=IN IP4 0.0.0.0\n" +
                "a=rtcp:1 IN IP4 0.0.0.0\n" +
                "a=ice-ufrag:bf40l19j0h2ebm\n" +
                "a=ice-pwd:28rk3qqg5683ihopea3t0p92hl\n" +
                "a=fingerprint:sha-1 A0:D0:01:C6:1B:88:87:A8:DC:3B:35:3C:FC:99:D2:0B:A2:1E:4B:D0\n" +
                "a=sendrecv\n" +
                "a=mid:audio\n" +
                "a=rtcp-mux\n" +
                "a=rtpmap:111 opus/48000/2\n" +
                "a=fmtp:111 minptime=10\n" +
                "a=rtpmap:103 ISAC/16000\n" +
                "a=rtpmap:104 ISAC/32000\n" +
                "a=rtpmap:0 PCMU/8000\n" +
                "a=rtpmap:8 PCMA/8000\n" +
                "a=rtpmap:106 CN/32000\n" +
                "a=rtpmap:105 CN/16000\n" +
                "a=rtpmap:13 CN/8000\n" +
                "a=rtpmap:126 telephone-event/8000\n" +
                "a=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\n" +
                "a=ssrc:1360129237 cname:nSxK15r3d7WTB3eV\n" +
                "a=ssrc:1360129237 msid:dd7dc1b1-b4ec-47cb-8a32-487e9f566b3c dd1c4152-47d7-44de-8355-e7562cc9259b\n" +
                "a=ssrc:1360129237 mslabel:dd7dc1b1-b4ec-47cb-8a32-487e9f566b3c\n" +
                "a=ssrc:1360129237 label:dd1c4152-47d7-44de-8355-e7562cc9259b\n" +
                "a=ssrc:2210343375 cname:mixed\n" +
                "a=ssrc:2210343375 label:mixedlabelaudio0\n" +
                "a=ssrc:2210343375 msid:mixedmslabel mixedlabelaudio0\n" +
                "a=ssrc:2210343375 mslabel:mixedmslabel\n" +
                "a=candidate:1 1 udp 2130706431 192.168.1.101 10014 typ host generation 0\n" +
                "a=candidate:2 1 udp 2130706431 192.168.56.1 10014 typ host generation 0\n" +
                "a=candidate:3 1 udp 2113932031 172.24.133.4 10014 typ host generation 0\n" +
                "a=candidate:4 1 ssltcp 2113932031 192.168.1.101 443 typ host generation 0\n" +
                "a=candidate:5 1 ssltcp 2113932031 192.168.56.1 443 typ host generation 0\n" +
                "a=candidate:6 1 ssltcp 2113932031 172.24.133.4 443 typ host generation 0\n" +
                "a=ssrc:1685798484 cname:zjetKk5FW4QjQN2t\n" +
                "a=ssrc:1685798484 msid:b5db8282-1649-4216-99bb-6ec7e2d08fbf 69868593-1eff-469e-8f57-ccddb3c86c97\n" +
                "a=ssrc:1685798484 mslabel:b5db8282-1649-4216-99bb-6ec7e2d08fbf\n" +
                "a=ssrc:1685798484 label:69868593-1eff-469e-8f57-ccddb3c86c97\n" +
                "m=video 1 RTP/SAVPF 100 116 117\n" +
                "c=IN IP4 0.0.0.0\n" +
                "a=rtcp:1 IN IP4 0.0.0.0\n" +
                "a=ice-ufrag:bf40l19j0h2ebm\n" +
                "a=ice-pwd:28rk3qqg5683ihopea3t0p92hl\n" +
                "a=fingerprint:sha-1 A0:D0:01:C6:1B:88:87:A8:DC:3B:35:3C:FC:99:D2:0B:A2:1E:4B:D0\n" +
                "a=sendrecv\n" +
                "a=mid:video\n" +
                "a=rtcp-mux\n" +
                "a=rtpmap:100 VP8/90000\n" +
                "a=rtpmap:116 red/90000\n" +
                "a=rtpmap:117 ulpfec/90000\n" +
                "a=extmap:2 urn:ietf:params:rtp-hdrext:toffset\n" +
                "a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\n" +
                "a=ssrc:4193882417 cname:11s32vjpwXYQ90eX\n" +
                "a=ssrc:4193882417 msid:386cad01-e090-47d8-93d5-3f800f0f7541 ed08cdd3-f8a6-4b06-86c5-e32026af318e\n" +
                "a=ssrc:4193882417 mslabel:386cad01-e090-47d8-93d5-3f800f0f7541\n" +
                "a=ssrc:4193882417 label:ed08cdd3-f8a6-4b06-86c5-e32026af318e\n" +
                "a=ssrc:428538435 cname:mixed\n" +
                "a=ssrc:428538435 label:mixedlabelvideo0\n" +
                "a=ssrc:428538435 msid:mixedmslabel mixedlabelvideo0\n" +
                "a=ssrc:428538435 mslabel:mixedmslabel\n" +
                "a=candidate:1 1 udp 2130706431 192.168.1.101 10014 typ host generation 0\n" +
                "a=candidate:2 1 udp 2130706431 192.168.56.1 10014 typ host generation 0\n" +
                "a=candidate:3 1 udp 2113932031 172.24.133.4 10014 typ host generation 0\n" +
                "a=candidate:4 1 ssltcp 2113932031 192.168.1.101 443 typ host generation 0\n" +
                "a=candidate:5 1 ssltcp 2113932031 192.168.56.1 443 typ host generation 0\n" +
                "a=candidate:6 1 ssltcp 2113932031 172.24.133.4 443 typ host generation 0\n" +
                "a=ssrc:3821067763 cname:Vfq5fexGjrvzNRLU\n" +
                "a=ssrc:3821067763 msid:3cdb8846-5581-4a1f-8cbf-4ca2d7da8d5f 61d7f812-db44-447d-adc9-fb363c6f2d70\n" +
                "a=ssrc:3821067763 mslabel:3cdb8846-5581-4a1f-8cbf-4ca2d7da8d5f\n" +
                "a=ssrc:3821067763 label:61d7f812-db44-447d-adc9-fb363c6f2d70\n";

        assertEquals(expectedSdp, resultSdp.description);

        // SOURCE-REMOVE
        MediaSSRCMap removedSSRCs = new MediaSSRCMap();

        audioSSRCs = removedSSRCs.getSSRCsForMedia("audio");

        SourcePacketExtension toRemoveASSRC = new SourcePacketExtension();
        toRemoveASSRC.setSSRC(1360129237L);
        toRemoveASSRC.addParameter(
            new ParameterPacketExtension("cname", "nSxK15r3d7WTB3eV"));
        toRemoveASSRC.addParameter(
            new ParameterPacketExtension("msid",
                "dd7dc1b1-b4ec-47cb-8a32-487e9f566b3c " +
                    "dd1c4152-47d7-44de-8355-e7562cc9259b"));
        toRemoveASSRC.addParameter(
            new ParameterPacketExtension(
                "mslabel", "dd7dc1b1-b4ec-47cb-8a32-487e9f566b3c"));
        toRemoveASSRC.addParameter(
            new ParameterPacketExtension(
                "label", "dd1c4152-47d7-44de-8355-e7562cc9259b"));

        audioSSRCs.add(toRemoveASSRC);

        videoSSRCs = removedSSRCs.getSSRCsForMedia("video");
        SourcePacketExtension toRemoveVSSRC = new SourcePacketExtension();
        toRemoveVSSRC.setSSRC(4193882417L);
        toRemoveVSSRC.addParameter(
            new ParameterPacketExtension("cname", "11s32vjpwXYQ90eX"));
        toRemoveVSSRC.addParameter(
            new ParameterPacketExtension(
                "msid", "386cad01-e090-47d8-93d5-3f800f0f7541 " +
                "ed08cdd3-f8a6-4b06-86c5-e32026af318e"));
        toRemoveVSSRC.addParameter(
            new ParameterPacketExtension(
                "mslabel", "386cad01-e090-47d8-93d5-3f800f0f7541"));
        toRemoveVSSRC.addParameter(
            new ParameterPacketExtension(
                "label", "ed08cdd3-f8a6-4b06-86c5-e32026af318e"));

        videoSSRCs.add(toRemoveVSSRC);

        SessionDescription afterRemoveSdp
            = JingleToSdp.removeSSRCs(resultSdp, removedSSRCs);

        expectedSdp = "v=0\n" +
            "o=- 1923518516 2 IN IP4 0.0.0.0\n" +
            "s=-\n" +
            "t=0 0\n" +
            "a=group:BUNDLE audio video\n" +
            "m=audio 1 RTP/SAVPF 111 103 104 0 8 106 105 13 126\n" +
            "c=IN IP4 0.0.0.0\n" +
            "a=rtcp:1 IN IP4 0.0.0.0\n" +
            "a=ice-ufrag:bf40l19j0h2ebm\n" +
            "a=ice-pwd:28rk3qqg5683ihopea3t0p92hl\n" +
            "a=fingerprint:sha-1 A0:D0:01:C6:1B:88:87:A8:DC:3B:35:3C:FC:99:D2:0B:A2:1E:4B:D0\n" +
            "a=sendrecv\n" +
            "a=mid:audio\n" +
            "a=rtcp-mux\n" +
            "a=rtpmap:111 opus/48000/2\n" +
            "a=fmtp:111 minptime=10\n" +
            "a=rtpmap:103 ISAC/16000\n" +
            "a=rtpmap:104 ISAC/32000\n" +
            "a=rtpmap:0 PCMU/8000\n" +
            "a=rtpmap:8 PCMA/8000\n" +
            "a=rtpmap:106 CN/32000\n" +
            "a=rtpmap:105 CN/16000\n" +
            "a=rtpmap:13 CN/8000\n" +
            "a=rtpmap:126 telephone-event/8000\n" +
            "a=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\n" +
            "a=ssrc:2210343375 cname:mixed\n" +
            "a=ssrc:2210343375 label:mixedlabelaudio0\n" +
            "a=ssrc:2210343375 msid:mixedmslabel mixedlabelaudio0\n" +
            "a=ssrc:2210343375 mslabel:mixedmslabel\n" +
            "a=candidate:1 1 udp 2130706431 192.168.1.101 10014 typ host generation 0\n" +
            "a=candidate:2 1 udp 2130706431 192.168.56.1 10014 typ host generation 0\n" +
            "a=candidate:3 1 udp 2113932031 172.24.133.4 10014 typ host generation 0\n" +
            "a=candidate:4 1 ssltcp 2113932031 192.168.1.101 443 typ host generation 0\n" +
            "a=candidate:5 1 ssltcp 2113932031 192.168.56.1 443 typ host generation 0\n" +
            "a=candidate:6 1 ssltcp 2113932031 172.24.133.4 443 typ host generation 0\n" +
            "a=ssrc:1685798484 cname:zjetKk5FW4QjQN2t\n" +
            "a=ssrc:1685798484 msid:b5db8282-1649-4216-99bb-6ec7e2d08fbf 69868593-1eff-469e-8f57-ccddb3c86c97\n" +
            "a=ssrc:1685798484 mslabel:b5db8282-1649-4216-99bb-6ec7e2d08fbf\n" +
            "a=ssrc:1685798484 label:69868593-1eff-469e-8f57-ccddb3c86c97\n" +
            "m=video 1 RTP/SAVPF 100 116 117\n" +
            "c=IN IP4 0.0.0.0\n" +
            "a=rtcp:1 IN IP4 0.0.0.0\n" +
            "a=ice-ufrag:bf40l19j0h2ebm\n" +
            "a=ice-pwd:28rk3qqg5683ihopea3t0p92hl\n" +
            "a=fingerprint:sha-1 A0:D0:01:C6:1B:88:87:A8:DC:3B:35:3C:FC:99:D2:0B:A2:1E:4B:D0\n" +
            "a=sendrecv\n" +
            "a=mid:video\n" +
            "a=rtcp-mux\n" +
            "a=rtpmap:100 VP8/90000\n" +
            "a=rtpmap:116 red/90000\n" +
            "a=rtpmap:117 ulpfec/90000\n" +
            "a=extmap:2 urn:ietf:params:rtp-hdrext:toffset\n" +
            "a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\n" +
            "a=ssrc:428538435 cname:mixed\n" +
            "a=ssrc:428538435 label:mixedlabelvideo0\n" +
            "a=ssrc:428538435 msid:mixedmslabel mixedlabelvideo0\n" +
            "a=ssrc:428538435 mslabel:mixedmslabel\n" +
            "a=candidate:1 1 udp 2130706431 192.168.1.101 10014 typ host generation 0\n" +
            "a=candidate:2 1 udp 2130706431 192.168.56.1 10014 typ host generation 0\n" +
            "a=candidate:3 1 udp 2113932031 172.24.133.4 10014 typ host generation 0\n" +
            "a=candidate:4 1 ssltcp 2113932031 192.168.1.101 443 typ host generation 0\n" +
            "a=candidate:5 1 ssltcp 2113932031 192.168.56.1 443 typ host generation 0\n" +
            "a=candidate:6 1 ssltcp 2113932031 172.24.133.4 443 typ host generation 0\n" +
            "a=ssrc:3821067763 cname:Vfq5fexGjrvzNRLU\n" +
            "a=ssrc:3821067763 msid:3cdb8846-5581-4a1f-8cbf-4ca2d7da8d5f 61d7f812-db44-447d-adc9-fb363c6f2d70\n" +
            "a=ssrc:3821067763 mslabel:3cdb8846-5581-4a1f-8cbf-4ca2d7da8d5f\n" +
            "a=ssrc:3821067763 label:61d7f812-db44-447d-adc9-fb363c6f2d70\n";

        assertEquals(expectedSdp, afterRemoveSdp.description);
    }

    private SourcePacketExtension createSSRC(long ssrc,   String cname,
                                             String msid, String mslabel,
                                             String label)
    {
        SourcePacketExtension ssrcPe = new SourcePacketExtension();

        ssrcPe.setSSRC(ssrc);
        ssrcPe.addParameter(
                new ParameterPacketExtension("cname", cname));
        ssrcPe.addParameter(
                new ParameterPacketExtension("msid", msid));
        ssrcPe.addParameter(
                new ParameterPacketExtension("mslabel", mslabel));
        ssrcPe.addParameter(
                new ParameterPacketExtension("label", label));

        return ssrcPe;
    }

    @Test
    public void testSdpToJingle()
    {
        String sdp = "v=0\n" +
        "o=- 1923518516 2 IN IP4 0.0.0.0\n" +
        "s=-\n" +
        "t=0 0\n" +
        "a=group:BUNDLE audio video data\n" +
        "m=audio 1 RTP/SAVPF 111 103 104 0 8 106 105 13 126\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=rtcp:1 IN IP4 0.0.0.0\n" +
        "a=ice-ufrag:4uq1t19kpid5gn\n" +
        "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
        "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" +
        "a=sendrecv\n" +
        "a=mid:audio\n" +
        "a=rtcp-mux\n" +
        "a=rtpmap:111 opus/48000/2\n" +
        "a=fmtp:111 minptime=10\n" +
        "a=rtpmap:103 ISAC/16000\n" +
        "a=rtpmap:104 ISAC/32000\n" +
        "a=rtpmap:0 PCMU/8000\n" +
        "a=rtpmap:8 PCMA/8000\n" +
        "a=rtpmap:106 CN/32000\n" +
        "a=rtpmap:105 CN/16000\n" +
        "a=rtpmap:13 CN/8000\n" +
        "a=rtpmap:126 telephone-event/8000\n" +
        "a=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\n" +
        "a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
        "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
        "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
        "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n" +
        "a=ssrc:1541584428 cname:mixed\n" +
        "a=ssrc:1541584428 label:mixedlabelaudio0\n" +
        "a=ssrc:1541584428 msid:mixedmslabel mixedlabelaudio0\n" +
        "a=ssrc:1541584428 mslabel:mixedmslabel\n" +
        "m=video 1 RTP/SAVPF 100 116 117\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=rtcp:1 IN IP4 0.0.0.0\n" +
        "a=ice-ufrag:4uq1t19kpid5gn\n" +
        "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
        "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" +
        "a=sendrecv\n" +
        "a=mid:video\n" +
        "a=rtcp-mux\n" +
        "a=rtpmap:100 VP8/90000\n" +
        "a=rtcp-fb:100 ccm fir\n" +
        "a=rtcp-fb:100 nack\n" +
        "a=rtcp-fb:100 nack pli\n" +
        "a=rtcp-fb:100 goog-remb\n" +
        "a=rtpmap:116 red/90000\n" +
        "a=rtpmap:117 ulpfec/90000\n" +
        "a=extmap:2 urn:ietf:params:rtp-hdrext:toffset\n" +
        "a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\n" +
        "a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
        "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
        "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
        "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n" +
        "a=ssrc:3200987178 cname:mixed\n" +
        "a=ssrc:3200987178 label:mixedlabelvideo0\n" +
        "a=ssrc:3200987178 msid:mixedmslabel mixedlabelvideo0\n" +
        "a=ssrc:3200987178 mslabel:mixedmslabel\n" +
        "m=application 1 DTLS/SCTP 5000\n" +
        "a=sctpmap:5000 webrtc-datachannel 1024\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=ice-ufrag:4uq1t19kpid5gn\n" +
        "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
        "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" +
        "a=sendrecv\n" +
        "a=mid:data\n" +
        "a=rtcp-mux\n" +
        "a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
        "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
        "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
        "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n";

        JingleIQ jingleIq = SdpToJingle.toJingle(
            new SessionDescription(SessionDescription.Type.OFFER, sdp));

        List<ContentPacketExtension> contents = jingleIq.getContentList();

        ContentPacketExtension audio = contents.get(0);
        assertEquals("audio", audio.getName());

        verifyAudioContents(audio);

        ContentPacketExtension video = contents.get(1);
        assertEquals("video", video.getName());

        verifyVideoContents(video);

        ContentPacketExtension data = contents.get(2);
        assertEquals("data", data.getName());

        verifyDataContents(data);
    }

    private void verifyAudioContents(ContentPacketExtension audio)
    {
        /*  "m=audio 1 RTP/SAVPF 111 103 104 0 8 106 105 13 126\n" +
            "c=IN IP4 0.0.0.0\n" +
            "a=rtcp:1 IN IP4 0.0.0.0\n" +
            "a=sendrecv\n" +
            "a=mid:audio\n" +
            "a=rtcp-mux\n" +
            "a=rtpmap:111 opus/48000/2\n" +
            "a=fmtp:111 minptime=10\n" +
            "a=rtpmap:103 ISAC/16000\n" +
            "a=rtpmap:104 ISAC/32000\n" +
            "a=rtpmap:0 PCMU/8000\n" +
            "a=rtpmap:8 PCMA/8000\n" +
            "a=rtpmap:106 CN/32000\n" +
            "a=rtpmap:105 CN/16000\n" +
            "a=rtpmap:13 CN/8000\n" +
            "a=rtpmap:126 telephone-event/8000\n" */

        RtpDescriptionPacketExtension audioRtp
            = audio.getFirstChildOfType(RtpDescriptionPacketExtension.class);

        assertEquals("audio", audioRtp.getMedia());

        List<PayloadTypePacketExtension> payloads = audioRtp.getPayloadTypes();

        PayloadTypePacketExtension opus = payloads.get(0);
        verifyPayloadType(opus, 111, "opus", 48000);
        assertEquals(2, opus.getChannels());

        List<ParameterPacketExtension> opusParams = opus.getParameters();
        assertEquals(1, opusParams.size());

        ParameterPacketExtension minPTime = opusParams.get(0);
        assertEquals("minptime", minPTime.getName());
        assertEquals("10", minPTime.getValue());

        PayloadTypePacketExtension isac16 = payloads.get(1);
        verifyPayloadType(isac16, 103, "ISAC", 16000);

        PayloadTypePacketExtension isac32 = payloads.get(2);
        verifyPayloadType(isac32, 104, "ISAC", 32000);

        PayloadTypePacketExtension pcmu = payloads.get(3);
        verifyPayloadType(pcmu, 0, "PCMU", 8000);

        PayloadTypePacketExtension pcma = payloads.get(4);
        verifyPayloadType(pcma, 8, "PCMA", 8000);

        PayloadTypePacketExtension cn32 = payloads.get(5);
        verifyPayloadType(cn32, 106, "CN", 32000);

        PayloadTypePacketExtension cn16 = payloads.get(6);
        verifyPayloadType(cn16, 105, "CN", 16000);

        PayloadTypePacketExtension cn8 = payloads.get(7);
        verifyPayloadType(cn8, 13, "CN", 8000);

        PayloadTypePacketExtension tele = payloads.get(8);
        verifyPayloadType(tele, 126, "telephone-event", 8000);

        //"a=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\n" +
        List<RTPHdrExtPacketExtension> rtpExts = audioRtp.getExtmapList();
        assertEquals(1, rtpExts.size());

        RTPHdrExtPacketExtension audioLevels = rtpExts.get(0);
        assertEquals("1", audioLevels.getID());
        assertEquals(
            "urn:ietf:params:rtp-hdrext:ssrc-audio-level",
            audioLevels.getURI().toString());

        /* "a=ice-ufrag:4uq1t19kpid5gn\n" +
           "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
           "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" */
        IceUdpTransportPacketExtension transport
            = audio.getFirstChildOfType(IceUdpTransportPacketExtension.class);

        assertEquals("4uq1t19kpid5gn", transport.getUfrag());
        assertEquals("3f8tujt9474ics249gvq1m7q3i", transport.getPassword());

        DtlsFingerprintPacketExtension dtls
            = transport.getFirstChildOfType(
                    DtlsFingerprintPacketExtension.class);

        assertEquals("sha-1", dtls.getHash());
        assertEquals(
            "2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E",
            dtls.getFingerprint());

        /*"a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
          "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
          "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
          "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n" */
        List<CandidatePacketExtension> candidates
            = transport.getCandidateList();

        assertEquals(4, candidates.size());

        verifyCandidate(
            "1", 1, "udp", 2130706431,
            "2001:41d0:d:750:0:0:0:9", 10031,
            CandidateType.host, 0,
            candidates.get(0));

        verifyCandidate(
            "2", 1, "udp", 2130706431,
            "176.31.40.85", 10031,
            CandidateType.host, 0,
            candidates.get(1));

        verifyCandidate(
            "3", 1, "ssltcp", 2113939711,
            "2001:41d0:d:750:0:0:0:9", 4443,
            CandidateType.host, 0,
            candidates.get(2));

        verifyCandidate(
            "4", 1, "ssltcp", 2113932031,
            "176.31.40.85", 4443,
            CandidateType.host, 0,
            candidates.get(3));

        /* "a=ssrc:1541584428 cname:mixed\n" +
           "a=ssrc:1541584428 label:mixedlabelaudio0\n" +
           "a=ssrc:1541584428 msid:mixedmslabel mixedlabelaudio0\n" +
           "a=ssrc:1541584428 mslabel:mixedmslabel\n" */
        SourcePacketExtension ssrcPe
            = audioRtp.getFirstChildOfType(SourcePacketExtension.class);

        verifySSRC(1541584428L, "mixed", "mixedlabelaudio0",
            "mixedmslabel mixedlabelaudio0", "mixedmslabel", ssrcPe);
    }

    private void verifyVideoContents(ContentPacketExtension video)
    {
        /*
        "m=video 1 RTP/SAVPF 100 116 117\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=rtcp:1 IN IP4 0.0.0.0\n" +
        ...
        "a=sendrecv\n" +
        "a=mid:video\n" +
        "a=rtcp-mux\n" +
        "a=rtpmap:100 VP8/90000\n" +
        "a=rtcp-fb:100 ccm fir\n" +
        "a=rtcp-fb:100 nack\n" +
        "a=rtcp-fb:100 nack pli\n" +
        "a=rtcp-fb:100 goog-remb\n" +
        "a=rtpmap:116 red/90000\n" +
        "a=rtpmap:117 ulpfec/90000\n" +

         */

        RtpDescriptionPacketExtension videoRtp
            = video.getFirstChildOfType(RtpDescriptionPacketExtension.class);

        assertEquals("video", videoRtp.getMedia());

        List<PayloadTypePacketExtension> payloads = videoRtp.getPayloadTypes();

        PayloadTypePacketExtension vp8 = payloads.get(0);
        verifyPayloadType(vp8, 100, "VP8", 90000);

        List<RtcpFbPacketExtension> vp8Fb = vp8.getRtcpFeedbackTypeList();
        assertEquals(4, vp8Fb.size());

        RtcpFbPacketExtension ccmFir = vp8Fb.get(0);
        assertEquals("ccm", ccmFir.getFeedbackType());
        assertEquals("fir", ccmFir.getFeedbackSubtype());

        RtcpFbPacketExtension nack = vp8Fb.get(1);
        assertEquals("nack", nack.getFeedbackType());

        RtcpFbPacketExtension nackPli = vp8Fb.get(2);
        assertEquals("nack", nackPli.getFeedbackType());
        assertEquals("pli", nackPli.getFeedbackSubtype());

        RtcpFbPacketExtension googRemb = vp8Fb.get(3);
        assertEquals("goog-remb", googRemb.getFeedbackType());

        PayloadTypePacketExtension red = payloads.get(1);
        verifyPayloadType(red, 116, "red", 90000);

        PayloadTypePacketExtension ulpfec = payloads.get(2);
        verifyPayloadType(ulpfec, 117, "ulpfec", 90000);

        //"a=extmap:2 urn:ietf:params:rtp-hdrext:toffset\n" +
        //"a=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\n" +
        List<RTPHdrExtPacketExtension> rtpExts = videoRtp.getExtmapList();
        assertEquals(2, rtpExts.size());

        RTPHdrExtPacketExtension toffset = rtpExts.get(0);
        assertEquals("2", toffset.getID());
        assertEquals(
            "urn:ietf:params:rtp-hdrext:toffset",
            toffset.getURI().toString());

        RTPHdrExtPacketExtension absSendTime = rtpExts.get(1);
        assertEquals("3", absSendTime.getID());
        assertEquals(
            "http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time",
            absSendTime.getURI().toString());

        /* "a=ice-ufrag:4uq1t19kpid5gn\n" +
           "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
           "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" + */
        IceUdpTransportPacketExtension transport
            = video.getFirstChildOfType(IceUdpTransportPacketExtension.class);

        assertEquals("4uq1t19kpid5gn", transport.getUfrag());
        assertEquals("3f8tujt9474ics249gvq1m7q3i", transport.getPassword());

        DtlsFingerprintPacketExtension dtls
            = transport.getFirstChildOfType(
            DtlsFingerprintPacketExtension.class);

        assertEquals("sha-1", dtls.getHash());
        assertEquals(
            "2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E",
            dtls.getFingerprint());

        /* "a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
           "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
           "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
           "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n" +
         */
        List<CandidatePacketExtension> candidates
            = transport.getCandidateList();

        assertEquals(4, candidates.size());

        verifyCandidate(
            "1", 1, "udp", 2130706431,
            "2001:41d0:d:750:0:0:0:9", 10031,
            CandidateType.host, 0,
            candidates.get(0));

        verifyCandidate(
            "2", 1, "udp", 2130706431,
            "176.31.40.85", 10031,
            CandidateType.host, 0,
            candidates.get(1));

        verifyCandidate(
            "3", 1, "ssltcp", 2113939711,
            "2001:41d0:d:750:0:0:0:9", 4443,
            CandidateType.host, 0,
            candidates.get(2));

        verifyCandidate(
            "4", 1, "ssltcp", 2113932031,
            "176.31.40.85", 4443,
            CandidateType.host, 0,
            candidates.get(3));

        /* "a=ssrc:3200987178 cname:mixed\n" +
           "a=ssrc:3200987178 label:mixedlabelvideo0\n" +
           "a=ssrc:3200987178 msid:mixedmslabel mixedlabelvideo0\n" +
           "a=ssrc:3200987178 mslabel:mixedmslabel\n" */
        SourcePacketExtension ssrcPe
            = videoRtp.getFirstChildOfType(SourcePacketExtension.class);

        verifySSRC(3200987178L, "mixed", "mixedlabelvideo0",
            "mixedmslabel mixedlabelvideo0", "mixedmslabel", ssrcPe);
    }

    private void verifyDataContents(ContentPacketExtension data)
    {
        /*
        "m=application 1 DTLS/SCTP 5000\n" +
        "a=sctpmap:5000 webrtc-datachannel 1024\n" +
        "c=IN IP4 0.0.0.0\n" +
        "a=ice-ufrag:4uq1t19kpid5gn\n" +
        "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
        "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" +
        "a=sendrecv\n" +
        "a=mid:data\n" +
        "a=rtcp-mux\n" +
        "a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
        "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
        "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
        "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n"
         */

        RtpDescriptionPacketExtension dataRtp
            = data.getFirstChildOfType(RtpDescriptionPacketExtension.class);

        assertEquals("application", dataRtp.getMedia());

        SctpMapExtension sctpMap
            = dataRtp.getFirstChildOfType(SctpMapExtension.class);
        assertEquals(
            SctpMapExtension.Protocol.WEBRTC_CHANNEL.toString(),
            sctpMap.getProtocol());
        assertEquals(5000, sctpMap.getPort());
        assertEquals(1024, sctpMap.getStreams());

        /* "a=ice-ufrag:4uq1t19kpid5gn\n" +
           "a=ice-pwd:3f8tujt9474ics249gvq1m7q3i\n" +
           "a=fingerprint:sha-1 2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E\n" + */
        IceUdpTransportPacketExtension transport
            = data.getFirstChildOfType(IceUdpTransportPacketExtension.class);

        assertEquals("4uq1t19kpid5gn", transport.getUfrag());
        assertEquals("3f8tujt9474ics249gvq1m7q3i", transport.getPassword());

        DtlsFingerprintPacketExtension dtls
            = transport.getFirstChildOfType(
            DtlsFingerprintPacketExtension.class);

        assertEquals("sha-1", dtls.getHash());
        assertEquals(
            "2A:96:FE:D7:07:2E:50:3D:94:87:58:6C:CE:CA:71:0F:99:BE:FF:8E",
            dtls.getFingerprint());

        /* "a=candidate:1 1 udp 2130706431 2001:41d0:d:750:0:0:0:9 10031 typ host generation 0\n" +
           "a=candidate:2 1 udp 2130706431 176.31.40.85 10031 typ host generation 0\n" +
           "a=candidate:3 1 ssltcp 2113939711 2001:41d0:d:750:0:0:0:9 4443 typ host generation 0\n" +
           "a=candidate:4 1 ssltcp 2113932031 176.31.40.85 4443 typ host generation 0\n" +
         */
        List<CandidatePacketExtension> candidates
            = transport.getCandidateList();

        assertEquals(4, candidates.size());

        verifyCandidate(
            "1", 1, "udp", 2130706431,
            "2001:41d0:d:750:0:0:0:9", 10031,
            CandidateType.host, 0,
            candidates.get(0));

        verifyCandidate(
            "2", 1, "udp", 2130706431,
            "176.31.40.85", 10031,
            CandidateType.host, 0,
            candidates.get(1));

        verifyCandidate(
            "3", 1, "ssltcp", 2113939711,
            "2001:41d0:d:750:0:0:0:9", 4443,
            CandidateType.host, 0,
            candidates.get(2));

        verifyCandidate(
            "4", 1, "ssltcp", 2113932031,
            "176.31.40.85", 4443,
            CandidateType.host, 0,
            candidates.get(3));
    }

    private void verifySSRC(long ssrc, String cname, String label,
                            String msid, String mslabel,
                            SourcePacketExtension ssrcPe)
    {
        assertEquals(ssrc, ssrcPe.getSSRC());
        assertEquals(cname, ssrcPe.getParameter("cname"));
        assertEquals(label, ssrcPe.getParameter("label"));
        assertEquals(msid, ssrcPe.getParameter("msid"));
        assertEquals(mslabel, ssrcPe.getParameter("mslabel"));
    }

    private void verifyCandidate(String foundation, int component,
                                 String protocol, int priority,
                                 String ip, int port, CandidateType type,
                                 int generation,
                                 CandidatePacketExtension c)
    {
        assertEquals(foundation, c.getFoundation());
        assertEquals(component, c.getComponent());
        assertEquals(protocol, c.getProtocol());
        assertEquals(priority, c.getPriority());
        assertEquals(ip, c.getIP());
        assertEquals(port, c.getPort());
        assertEquals(type, c.getType());
        assertEquals(generation, c.getGeneration());
    }

    private void verifyPayloadType(PayloadTypePacketExtension payload,
                                   int id, String name, int rate)
    {
        assertEquals(id, payload.getID());
        assertEquals(name, payload.getName());
        assertEquals(rate, payload.getClockrate());
    }

}
