package gab.protheansoftware.com.gab.chat;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.sql.SQLException;
import java.util.List;

import gab.protheansoftware.com.gab.R;
import gab.protheansoftware.com.gab.adapter.MessageAdapter;
import gab.protheansoftware.com.gab.model.IDatabaseHandler;
import gab.protheansoftware.com.gab.model.JdbcDatabaseHandler;

/**
 * Created by boking on 2015-10-07.
 */
public class MessagingFragment extends Fragment {
    private final static String TAG = "MESSAGING_ACTIVITY";

    private MessageClientListener messageClientListener;
    private IDatabaseHandler dbh;

    private String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    private ServiceConnection serviceConnection = new MyServiceConnection();

    private ListView messagesList;
    private MessageAdapter messageAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "sending message");

        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.fragment_chat);

        //Initialize variables
        dbh = new JdbcDatabaseHandler();
        messageClientListener = new MySQLMessageClientListener();

        getActivity().bindService(new Intent(getActivity(), MessagingFragment.class), serviceConnection, getActivity().BIND_AUTO_CREATE);

        //get recipientId from intent extra
        Intent intent = getActivity().getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
        try {
            currentUserId = String.valueOf(dbh.getMyId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Add listener for sending messages
        messageBodyField = (EditText) getActivity().findViewById(R.id.messageBodyField);
        getActivity().findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sending message");
                messageBody = messageBodyField.getText().toString();
                if (!messageBody.isEmpty()) {
                    messageService.sendMessage(recipientId, messageBody);
                    messageBodyField.setText("");
                }
            }
        });

        messagesList = (ListView) getActivity().findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(getActivity());
        messagesList.setAdapter(messageAdapter);
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(serviceConnection);
        messageService.removeMessageClientListener(messageClientListener);
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messageService = (MessageService.MessageServiceInterface) service;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messageService = null;
        }
    }




    private class MySQLMessageClientListener implements MessageClientListener {
        @Override
        public void onIncomingMessage(MessageClient messageClient, Message message) {
            //Display
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
        }

        @Override
        public void onMessageSent(MessageClient messageClient, Message message, String s) {
            //Display the message just sent
            //Save in database
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
            Toast.makeText(getActivity(), "Message failed to send.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

        }
    }
}