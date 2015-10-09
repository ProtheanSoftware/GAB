package gab.protheansoftware.com.gab.chat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
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

    private static String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    public ServiceConnection serviceConnection = new MyServiceConnection();

    private ListView messagesList;
    private MessageAdapter messageAdapter;

    public static void setRecipientId(String recipientId) {
        MessagingFragment.recipientId = recipientId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize base items for messaging functionality

        Log.d(TAG, "Initializing messagefragment");

        //Initialize variables
        dbh = JdbcDatabaseHandler.getInstance();
        messageClientListener = new MySQLMessageClientListener();


        try {
            currentUserId = String.valueOf(dbh.getMyId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(serviceConnection);
        messageService.removeMessageClientListener(messageClientListener);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat,container,false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "Connecting service");
        getActivity().bindService(new Intent(getActivity(), MessageService.class), serviceConnection, FragmentActivity.BIND_AUTO_CREATE);

        //Add listener for sending messages
        messageBodyField = (EditText) getActivity().findViewById(R.id.messageBodyField);
        getActivity().findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageBody = messageBodyField.getText().toString();

                Log.d(TAG, "sending message: " + messageBody + ", To: " + recipientId);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) Log.d(TAG, "Switched to chat");
        //Put read messages from db here (dont forget to empty current chat)
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Service Connection", "Service connected");
            messageService = (MessageService.MessageServiceInterface) service;
            messageService.addMessageClientListener(messageClientListener);

            //For now the conversation between the current user and user 1 is retrieved and
            //displayed. This will later be integrated and the variable recipientId will be used
            //instead.
            for (gab.protheansoftware.com.gab.model.Message m : dbh.getConversation(1)) {
                WritableMessage writableMessage = new WritableMessage(m.getId() + "", m.getMessage());
                if (Integer.parseInt(currentUserId) == m.getRecieverId()) {
                    messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
                } else {
                    messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
                }
            }
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
            Log.d(TAG, "Displaying message");
            //Display the message just sent
            //Save in database
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);

            JdbcDatabaseHandler.getInstance()
                    .saveMessage(
                            Integer.parseInt(message.getRecipientIds().get(0)),
                            message.getTextBody(),
                            writableMessage.getMessageId());
        }

        @Override
        public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
            Toast.makeText(getActivity(), "Message failed to send.", Toast.LENGTH_LONG).show();
            Log.e(TAG, messageFailureInfo.getSinchError().toString());
        }

        @Override
        public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

        }
    }
}