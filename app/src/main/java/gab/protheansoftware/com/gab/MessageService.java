package gab.protheansoftware.com.gab;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.sinch.android.rtc.*;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;
import gab.protheansoftware.com.gab.model.IDatabaseHandler;
import gab.protheansoftware.com.gab.model.JdbcDatabaseHandler;

import java.sql.SQLException;

/**
 * Created by oskar on 2015-10-06.
 */
public class MessageService extends Service implements SinchClientListener {
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
            this.setDbh(new JdbcDatabaseHandler());
        }
        try {
            currentUserId = String.valueOf(dbh.getMyId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(currentUserId != null && !isSinchClientStarted()){
            startSinchClient(currentUserId);
        }
        return super.onStartCommand(intent, flags, startID);
    }
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
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.checkManifest();
        sinchClient.start();

    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    private void setDbh(IDatabaseHandler dbh) {
        this.dbh = dbh;
    }

    @Override
    public void onClientStarted(SinchClient client) {
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

    public void sendMessage(String recipientUserId, String textBody){
        if(messageClient != null){
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener){
        if(messageClient != null);{
            messageClient.addMessageClientListener(listener);
        }
    }
    public void removeMessageClientListener(MessageClientListener listener){
        if(messageClient != null){
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }


    //public interface for ListUsersActivity & MessagingActivity
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
