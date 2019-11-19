package com.example.faridomar.messengerapp;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Farid omar on 1/29/2017.
 */
public class ConnectionService extends Service {
    private static final String TAG = "MessengerService";

    public static final String UI_AUTHENTICATED = "com.example.faridomar.messengerapp.uiauthenticated";
    public static final String SEND_MESSAGE = "com.example.faridomar.messengerapp.sendmessage";
    public static final String BODY = "b_body";
    public static final String TO = "b_to";

    public static final String NEW_MESSAGE = "com.example.faridomar.messengerapp.newmessage";
    public static final String FROM = "b_from";

    public static Connection.States sConnectionState;
    public static Connection.LogState sLoggedInState;
    private boolean isActive;//thread active or not
    private Thread thread;
    private Handler handler;
    //the background thread.
    private Connection connection;

    private BroadcastReceiver Receiver;

    public ConnectionService() {

      //  setupMessageReceiver();

    }
 /*   private void setupMessageReceiver() {
        Receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case ConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(ConnectionService.FROM);
                        String body = intent.getStringExtra(ConnectionService.BODY);

                            Log.i(TAG, "Got a message from jid :" + body);


                        return;
                }

            }
        };

    }*/

    public static Connection.States getState() {
        if (sConnectionState == null) {
            return Connection.States.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static Connection.LogState getLoggedInState() {
        if (sLoggedInState == null) {
            return Connection.LogState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    private void startConnection() throws KeyManagementException, NoSuchAlgorithmException {
        Log.d(TAG, "startConnection()");
        if (connection == null) {
            connection = new Connection(this);
        }
        try {
            connection.connect();

        } catch (IOException | SmackException | XMPPException e) {
            Log.d(TAG, "error ocurred try again");
            e.printStackTrace();
            //Stop the service
            stopSelf();
        }

    }


    public void start() {
        Log.d(TAG, " Service Start() function called.");
        if (!isActive) {
            isActive = true;
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        handler = new Handler();
                        try {
                            startConnection();
                        } catch (KeyManagementException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        Looper.loop();

                    }
                });
                thread.start();
            }


        }

    }


    public void stop() {
        Log.d(TAG, "stop()");
        isActive = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        start();
        return Service.START_STICKY;
        //make service alive
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stop();
    }
}
