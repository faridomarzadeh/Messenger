package com.example.faridomar.messengerapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.sasl.SASLMechanism.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
//import com.google.gson.Gson;


/**
 * Created by Farid omar on 1/29/2017.
 */
public class Connection implements ConnectionListener, ChatMessageListener,PacketListener {

    private static final String TAG = "Connection";

    private final Context appContext;
    private final String username;
    private final String pass;
    private final String name_Servive;
    private XMPPTCPConnection conn;
    private ChatManager chatManager;

    private BroadcastReceiver ReceiverThread;


/*
    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        processMessage(chat,new Message());
    }*/


    public static enum States {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED;
    }

    public static enum LogState {
        LOGGED_IN, LOGGED_OUT;
    }


    public Connection(Context context) {
        appContext = context.getApplicationContext();
        String id = PreferenceManager.getDefaultSharedPreferences(appContext)
                .getString("xmpp_id", null);
        pass = PreferenceManager.getDefaultSharedPreferences(appContext)
                .getString("xmpp_password", null);

        if (id != null) {
            username = id.split("@")[0];
            name_Servive = id.split("@")[1];
        } else {
            username = "";
            name_Servive = "";
        }
    }

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(
                        X509Certificate[] certs,
                        String authType) {
                }

                public void checkServerTrusted(
                        X509Certificate[] certs,
                        String authType) {
                }
            }
    };
    HostnameVerifier verifier = new HostnameVerifier() {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public void connect() throws IOException, XMPPException, SmackException, NoSuchAlgorithmException, KeyManagementException {
        Log.d(TAG, "Connecting to server " + name_Servive);
        XMPPTCPConnectionConfiguration.Builder builder =
                XMPPTCPConnectionConfiguration.builder();
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustAllCerts, new java.security.SecureRandom());
        builder.setCustomSSLContext(context);
        builder.setHostnameVerifier(verifier);
        builder.setServiceName(name_Servive);
        builder.setPort(5222);
        builder.setUsernameAndPassword(username, pass);
        // builder.setRosterLoadedAtLogin(true);
        builder.setDebuggerEnabled(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        builder.setResource("Messenger");
        try {
            TLSUtils.acceptAllCertificates(builder);
            Log.i(TAG, TLSUtils.TLS.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        Presence presence = new Presence(Presence.Type.available);
        presence.setPriority(1);
        //  presence.setStatus();

        setupMessageReceiver();

        conn = new XMPPTCPConnection(builder.build());
        conn.setUseStreamManagement(true);
        conn.setUseStreamManagementResumption(true);
        conn.addConnectionListener(this);

        /*********************/
     /*   conn.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException
            {
                Presence presence = (Presence) packet;
                Roster roster=Roster.getInstanceFor(conn);
                if(presence.getType()== Presence.Type.subscribed)
                {
                    Log.i("Friend Request",presence.getFrom());
                    Log.i("Friend Request",presence.getStatus());
                    Log.i("Friend Request",presence.getTo());
                    Log.i("Friend Request",presence.getMode().toString());

                }
            }
        },new PacketTypeFilter(Presence.class));*/

        /***************************/


      /*  SASLMechanism mechanism = new SASLDigestMD5Mechanism();
        SASLAuthentication.registerSASLMechanism(mechanism);
        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");

      /*  SASLAuthentication.unBlacklistSASLMechanism("PLAIN");*/
       /* SASLAuthentication.registerSASLMechanism("DIGEST-MD5",SASLDigestMD5Mechanism.class);*/
        //SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        conn.connect();
        conn.login();

       // chatManager=conn.get

      /*  StanzaTypeFilter message_filter = new StanzaTypeFilter(Message.class);
        conn.addSyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                Log.i(TAG, �Processing Packet�);
                Message message = (Message) packet;
                if (message.getType() == Message.Type.chat) {
                    String msg_xml = packet.toString();
                    Log.i(TAG, �Message � + msg_xml);
                    if (msg_xml.contains(ChatState.gone.toString())) {
//handle has closed chat
                        Log.i(TAG, �Gone�);
                    } else if (msg_xml.contains(ChatState.paused.toString())) {
// handle �stopped typing�
                        Log.i(TAG, �Paused�);
                    } else {
//single chat message
                        Log.i(TAG, �Chat � + message.getBody());
                    }
                } else if (message.getType() == Message.Type.groupchat) {
//group chat message
                    Log.i(TAG, �GroupChat � � + message.getBody());
                } else if (message.getType() == Message.Type.error) {
//error message
                    Log.i(TAG, �Error � � + message.toXML());
                }

            }
        }, message_filter);*/
        conn.sendPacket(presence);
        G.XMPPCONNECT = conn;


        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(conn);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }
    private void getFriendRequestProcess()
    {
       // RosterExchangeManager rem = new RosterExchangeManager(connection);
    }

    private void setupMessageReceiver() {
        ReceiverThread = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action.equals(ConnectionService.SEND_MESSAGE)) {
                    //Send the message.
                    sendMessage(intent.getStringExtra(ConnectionService.BODY),
                            intent.getStringExtra(ConnectionService.TO));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionService.SEND_MESSAGE);
        appContext.registerReceiver(ReceiverThread, filter);

    }
  /*  public Collection<RosterEntry> getRousetr() throws SmackException.NotLoggedInException, SmackException.NotConnectedException {
        Roster roster = Roster.getInstanceFor(conn);
        if (!roster.isLoaded())
            roster.reloadAndWait();

        Collection<RosterEntry> entries = roster.getEntries();
        return entries;
    }*/



    private void sendMessage(String body, String toJid) {
        Log.i(TAG, "Sending message to :" + toJid);
        Chat chat = ChatManager.getInstanceFor(conn)
                .createChat(toJid, this);
        Log.i(TAG, conn.toString());
        Gson gson = new Gson();
        String _body = gson.toJson(body);
        Random r = new Random();
        int id = r.nextInt(10000);
        final Message message = new Message();
        message.setBody(_body);
       // message.setStanzaId("" + id);
        message.setType(Message.Type.chat);
        //*  message.setType(Message.Type.chat);*/
        try {
            chat.sendMessage(message);
            Log.i(TAG, body);
        } catch (SmackException.NotConnectedException e)//( | XMPPException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void processMessage(Chat chat, Message message) {

       // Log.i("I got an Message",message.);

        Log.i(TAG, "message.getBody() :" + message.getBody());
        Log.i(TAG, "message.getFrom() :" + message.getFrom());

        String from = message.getFrom();
        String contactJid = "";
        if (from.contains("/")) {
            contactJid = from.split("/")[0];
            Log.i(TAG, "The real jid is :" + contactJid);
        } else {
            contactJid = from;
        }

        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(ConnectionService.NEW_MESSAGE);
        intent.setPackage(appContext.getPackageName());
        intent.putExtra(ConnectionService.FROM, contactJid);
        intent.putExtra(ConnectionService.BODY, message.getBody());
        appContext.sendBroadcast(intent);
        Log.i(TAG, "Received message from :" + contactJid + " broadcast sent.");

    }

 /* public void processMessage(Chat chat, Message message) {
      // Print out any messages we get back to standard out.
      System.out.println("Received message: " + message);
  }*/

    public void disconnect() {
        Log.d(TAG, "Disconnecting from serser " + name_Servive);
        if (conn != null) {
            conn.disconnect();
        }

        conn = null;
        if (ReceiverThread != null) {
            appContext.unregisterReceiver(ReceiverThread);
            ReceiverThread = null;
        }

    }


    @Override
    public void connected(XMPPConnection connection) {
        ConnectionService.sConnectionState = States.CONNECTED;
        Log.d(TAG, "Connected Successfully");
    }

    @Override
    public void authenticated(XMPPConnection connection,boolean resumed) {
        ConnectionService.sConnectionState = States.CONNECTED;
        Log.d(TAG, "Authenticated Successfully");
        showContactList();
       // chatManager = ChatManager.getInstanceFor(conn);
        //chatManager.addChatListener(processMessage(););


    }

    @Override
    public void connectionClosed() {
        ConnectionService.sConnectionState = States.DISCONNECTED;
        Log.d(TAG, "Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        ConnectionService.sConnectionState = States.DISCONNECTED;
        Log.d(TAG, "ConnectionClosedOnError, error " + e.toString());

    }

    @Override
    public void reconnectingIn(int seconds) {
        ConnectionService.sConnectionState = States.CONNECTING;
        Log.d(TAG, "ReconnectingIn() ");

    }

    @Override
    public void reconnectionSuccessful() {
        ConnectionService.sConnectionState = States.CONNECTED;
        Log.d(TAG, "ReconnectionSuccessful()");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        ConnectionService.sConnectionState = States.DISCONNECTED;
        Log.d(TAG, "ReconnectionFailed()");

    }


    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException
    {
        Presence presence = (Presence) packet;
        Roster roster=Roster.getInstanceFor(conn);
        if(presence.getType()== Presence.Type.subscribed)
        {
            Log.i("sFriend Request",presence.getFrom());
            Log.i("sFriend Request",presence.getStatus());
            Log.i("sFriend Request",presence.getTo());
            Log.i("sFriend Request",presence.getMode().toString());

        }
        Log.i("sFriend Request",presence.getFrom());
        Log.i("sFriend Request",presence.getStatus());
        Log.i("sFriend Request",presence.getTo());
        Log.i("sFriend Request", presence.getMode().toString());

    }

    private void showContactList() {
        Intent i = new Intent(ConnectionService.UI_AUTHENTICATED);
        i.setPackage(appContext.getPackageName());
        appContext.sendBroadcast(i);
        Log.i(TAG, "Broadcast : we authenticated");
    }
}

