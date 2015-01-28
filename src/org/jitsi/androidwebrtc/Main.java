package org.jitsi.androidwebrtc;

import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import org.jivesoftware.smack.provider.*;

/**
 * Created by boris on 27/01/15.
 */
public class Main
{
    public static void main(String[] args)
    {
        ProviderManager.getInstance().addIQProvider(
                JingleIQ.ELEMENT_NAME,
                JingleIQ.NAMESPACE,
                new JingleIQProvider());
        System.err.println("Main.main");
        Participant participant = new Participant();
        String domain = "test.hipchat.me";
        participant.join(
            domain, domain, "test@conference.test.hipchat.me", "testnickname");
    }
}
