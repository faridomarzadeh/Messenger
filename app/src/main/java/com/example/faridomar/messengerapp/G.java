package com.example.faridomar.messengerapp;

/**
 * Created by Farid omar on 1/29/2017.
 */
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;


public class G {
    public static String TAG="G";
    public static XMPPTCPConnection XMPPCONNECT;
    public static Context appContext;


    public static void sendFriendRequest()
    {
        final Dialog dialog =new Dialog(appContext);
        dialog.setContentView(R.layout.add_friend);
      //  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Button btnsendrequest=(Button)dialog.findViewById(R.id.btnsendrequest);
        Button btncancelrequest=(Button)dialog.findViewById(R.id.btncancelrequest);
        final EditText edtid=(EditText)dialog.findViewById(R.id.edtid);
        final EditText edtnickname=(EditText)dialog.findViewById(R.id.edtnickname);
        btnsendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jid=edtid.getText().toString();
                String nickname=edtnickname.getText().toString();

                Log.i(TAG, "trying add" + jid + " to contacts");
                Presence response=new Presence(Presence.Type.subscribed);
                response.setTo(jid);

                Roster roster=Roster.getInstanceFor(XMPPCONNECT);
                try {
                    roster.createEntry(jid,nickname, new String[]{"Friends"});
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

            }
        });
        dialog.show();

        btncancelrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        // dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


    }
}