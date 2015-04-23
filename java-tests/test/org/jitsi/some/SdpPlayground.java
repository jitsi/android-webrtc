package org.jitsi.some;

import net.java.sip.communicator.impl.protocol.jabber.extensions.colibri.*;
import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jitsi.androidwebrtc.*;
import org.jitsi.androidwebrtc.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.webrtc.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
            = JingleUtils.addSSRCs(remoteSdp, addedSSRCs);

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
            = JingleUtils.removeSSRCs(resultSdp, removedSSRCs);

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

}
