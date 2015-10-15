package com.protheansoftware.gab.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sinch.android.rtc.*;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.protheansoftware.gab.model.IDatabaseHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;

/**
 * @author oskar 
 * Created by oskar on 2015-10-06.
 */
public class MessageService extends Service implements SinchClientListener {
    private static final String TAG = "MESSAGE SERVICE";
    private IDatabaseHandler dbh = null;
    private static final String APP_KEY = "ca2d3532-28fa-47a0-9b86-770b33c28c63";
    private static final String APP_SECRET = "ZnbCNoNYAkGV4nKVvvivmQ==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private final MessageServiceInterface serviceInterface = new MessageServiceInterface();
    private SinchClient sinchClient = null;
    private MessageClient messageClient = null;
    private String currentUserId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        if(dbh == null){
            this.setDbh(JdbcDatabaseHandler.getInstance());
        }
        currentUserId = String.valueOf(dbh.getMyId());

        if(!isSinchClientStarted()){
            startSinchClient(currentUserId);
        }
        return super.onStartCommand(intent, flags, startID);
    }

    /**
     * Starts the sinch client with the app secrets and the correct environment for the current user
     * @param username User id
     */
    public void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(username)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();
        sinchClient.addSinchClientListener(this);
        //Allow messaging
        sinchClient.setSupportMessaging(true);


        sinchClient.checkManifest();
        sinchClient.start();
        Log.d(TAG, "Starting sinch client");

    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    private void setDbh(IDatabaseHandler dbh) {
        this.dbh = dbh;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        Log.d(TAG, "Client starting..");
        client.startListeningOnActiveConnection();
        messageClient = client.getMessageClient();
    }

    @Override
    public void onClientStopped(SinchClient client) {
        sinchClient = null;
    }

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        sinchClient = null;
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {}

    @Override
    public void onLogMessage(int i, String s, String s1) {}

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    /**
     * Send messages to client and save to database with the unique sinch_id
     * @param recipientUserId recipient
     * @param textBody message content
     */
    public void sendMessage(String recipientUserId, String textBody){
        if(messageClient != null){
            Log.d("MessageService", "sending message: " + textBody);
            final WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Save in database
                    JdbcDatabaseHandler.getInstance()
                            .saveMessage(
                                    Integer.parseInt(message.getRecipientIds().get(0)),
                                    message.getTextBody(),
                                    message.getMessageId());
                }
            });
            thread.run();
        }
    }

    /**
     * Adds messagelistener to the client which can later call the methods OnMessageSent and
     * onIncomingMessage in MessagingFragment
     * @param listener target listener
     */
    public void addMessageClientListener(MessageClientListener listener){
        if(messageClient != null){
            Log.d(TAG, "Adding messageclientlistener");
            messageClient.addMessageClientListener(listener);
        }
    }
    public void removeMessageClientListener(MessageClientListener listener){
        if(messageClient != null){
            Log.d(TAG, "Removing messageclientlistener");
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.terminate();
    }


    /**
     * Used to call the message service from MessagingFragment
     */
    public class MessageServiceInterface extends Binder {
        public void sendMessage(String recipientUserId, String textBody) {
            MessageService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            MessageService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            MessageService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return MessageService.this.isSinchClientStarted();
        }
    }
}
