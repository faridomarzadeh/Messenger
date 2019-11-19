package com.example.faridomar.messengerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import co.devcenter.androiduilibrary.ChatView;
import co.devcenter.androiduilibrary.ChatViewEventListener;
import co.devcenter.androiduilibrary.SendButton;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private String contactJid;
    private ChatView ctView;
    private SendButton sendButton;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        ctView = (ChatView) findViewById(R.id.messenger_chat_view);
        ctView.setEventListener(new ChatViewEventListener() {
            @Override
            public void userIsTyping() {
                //Here you know that the user is typing
            }

            @Override
            public void userHasStoppedTyping() {
                //Here you know that the user has stopped typing.
            }
        });

        sendButton = ctView.getSendButton();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (ConnectionService.getState().equals(Connection.States.CONNECTED)) {


                    Intent intent = new Intent(ConnectionService.SEND_MESSAGE);
                    intent.putExtra(ConnectionService.BODY,
                            ctView.getTypedString());
                    intent.putExtra(ConnectionService.TO, contactJid);

                    sendBroadcast(intent);

                    //Update the chat view.
                    ctView.sendMessage();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "not connected to server ,not sent!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        contactJid = intent.getStringExtra("EXTRA_CONTACT_JID");
        setTitle(contactJid);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case ConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(ConnectionService.FROM);
                        String body = intent.getStringExtra(ConnectionService.BODY);

                        if (from.equals(contactJid)) {
                            ctView.receiveMessage(body);

                        } else {
                            Log.d(TAG, "Got a message from :" + from+"Body:"+body);
                        }

                        return;
                }

            }
        };

        IntentFilter filter = new IntentFilter(ConnectionService.NEW_MESSAGE);
        registerReceiver(broadcastReceiver, filter);

    }
}

