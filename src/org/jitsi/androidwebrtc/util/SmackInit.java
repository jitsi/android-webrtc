package org.jitsi.androidwebrtc.util;

import android.util.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smackx.*;
import org.jivesoftware.smackx.bytestreams.ibb.*;
import org.jivesoftware.smackx.bytestreams.socks5.*;
import org.jivesoftware.smackx.bytestreams.socks5.provider.*;
import org.jivesoftware.smackx.muc.*;
import org.jivesoftware.smackx.packet.*;
import org.jivesoftware.smackx.provider.*;
import org.jivesoftware.smackx.search.*;

/**
 * @author Pawel Domas
 */
public class SmackInit
{
    private final static String TAG = "SmackInit";

    public static void init()
    {
        loadJabberServiceClasses();
    }

    private static void loadJabberServiceClasses()
    {
        try
        {
            // pre-configure smack in android
            // just to load class to init their static blocks
            SmackConfiguration.getVersion();

            Class.forName(ServiceDiscoveryManager.class.getName());

            Class.forName(PrivacyListManager.class.getName());
            Class.forName(MultiUserChat.class.getName());

            Class.forName(DelayInformation.class.getName());
            Class.forName(DelayInformationProvider.class.getName());

            Class.forName(Socks5BytestreamManager.class.getName());
            Class.forName(XHTMLManager.class.getName());
            Class.forName(InBandBytestreamManager.class.getName());
            Class.forName(ReconnectionManager.class.getName());

            configure(ProviderManager.getInstance());
        }
        catch(ClassNotFoundException e)
        {
            Log.e(TAG, "Error initializing Smack", e);
        }
    }

    private static void configure(ProviderManager pm) {

        //  Private Data Storage
        pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        //  Time
        try {
            pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        //  Roster Exchange
        pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());

        //  Message Events
        pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

        //  Chat State
        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        //  XHTML
        pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        //  Group Chat Invitations
        pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());

        //  Service Discovery # Items
        pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        //  Service Discovery # Info
        pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        //  Data Forms
        pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());

        //  MUC User
        pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());

        //  MUC Admin
        pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        //  MUC Owner
        pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        //  Delayed Delivery
        pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());

        //  Version
        try {
            pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }

        //  VCard
        pm.addIQProvider("vCard","vcard-temp", new VCardProvider());

        //  Offline Message Requests
        pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        //  Offline Message Indicator
        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        //  Last Activity
        pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());

        //  User Search
        pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());

        //  SharedGroupsInfo
        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());

        pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

        //  Privacy
        pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }
}
