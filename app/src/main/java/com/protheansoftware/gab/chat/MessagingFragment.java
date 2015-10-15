package com.protheansoftware.gab.chat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.protheansoftware.gab.Main2Activity;
import com.protheansoftware.gab.adapter.TabsPagerAdapter;
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

import com.protheansoftware.gab.R;
import com.protheansoftware.gab.adapter.MessageAdapter;
import com.protheansoftware.gab.model.IDatabaseHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;

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
    private boolean viewCreated;

    private Main2Activity main;

    /**
     * Sets the recipientId for chat, later used inorder to send and retreive messages
     * @param recipientId Recipient Id
     */
    public static void setRecipientId(String recipientId) {
        MessagingFragment.recipientId = recipientId;
    }

    /**
     * Gets the recipient id
     * @return recipient id
     */
    public static String getRecipientId(){
        return recipientId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewCreated = false;

        Log.d(TAG, "Initializing messagefragment");

        //Initialize variables
        dbh = JdbcDatabaseHandler.getInstance();
        messageClientListener = new SinchMessageClientListener();

        currentUserId = String.valueOf(dbh.getMyId());
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
        viewCreated = true;
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
        if(isVisibleToUser){
            Log.d(TAG, "Switched to chat");
            fillChatIfNeeded();
        }else if(viewCreated){
            main.closeChat();
        }
    }

    /**
     * Empties and refills the chat if the recipient has been switched
     *
     */
    private void fillChatIfNeeded() {
        ListView list = (ListView)getActivity().findViewById(R.id.listMessages);

        //check if should empty messages(i.e if recipient id is same of conversation)
        Pair<WritableMessage, Integer> latestSentMessage  = ((MessageAdapter)list.getAdapter()).getLatestSentMessage();
        if(latestSentMessage != null && latestSentMessage.first.getRecipientIds().contains(recipientId)){
            Log.d(TAG, "chat is same as before");
        }else {
            //Empty chat
            list.setAdapter(null);
            messageAdapter = new MessageAdapter(getActivity());
            list.setAdapter(messageAdapter);
        }
        //Read messages from database, fill out chat.
        if(messageAdapter.isEmpty()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<com.protheansoftware.gab.model.Message> messages = dbh.getConversation(Integer.parseInt(recipientId));
                    messages = sortDescID(messages);
                    for (com.protheansoftware.gab.model.Message m : messages) {
                        WritableMessage writableMessage = new WritableMessage(String.valueOf(m.getId()), m.getMessage());
                        if (Integer.parseInt(currentUserId) == m.getRecieverId()) {
                            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
                        } else {
                            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
                        }
                    }
                    return;
                }
            });
            thread.run();
        }
    }

    /**
     * Helper method for sorting messages so latest messages come at the bottom of the list
     * @param messages entry list
     * @return sorted list
     */
    private ArrayList<com.protheansoftware.gab.model.Message> sortDescID(ArrayList<com.protheansoftware.gab.model.Message> messages) {
        if(messages == null) return null;
        //If not null sort by bubblesort
        com.protheansoftware.gab.model.Message temp;
        for (int x=0; x<messages.size(); x++)
        {
            for (int i=0; i < messages.size()-x-1; i++) {
                if (messages.get(i).getId() > (messages.get(i+1).getId()))
                {
                    temp = messages.get(i);
                    messages.set(i,messages.get(i+1));
                    messages.set(i+1, temp);
                }
            }
        }
        return messages;
    }

    public void setMain(Main2Activity main) {
        this.main = main;
    }

    /**
     * Needed to listen to when the service has been connected so we can start listening
     * for messages.
     */
    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Service Connection", "Service connected");
            messageService = (MessageService.MessageServiceInterface) service;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messageService = null;
        }
    }

    /**
     * Does all the "frontend" for chat aswell as calls the backend to add messags
     */
    private class SinchMessageClientListener implements MessageClientListener {
        @Override
        public void onIncomingMessage(MessageClient messageClient, final Message message) {
            Log.d(TAG, "MESSAGE RECIEVING: " + message.getMessageId());
            /**
             * Display message if the list doesn't already have the message displayed
             * This is required for sinch, sinch will try to redeliver all undelivered messages
             * Since we also save in a mysqldatabase for message history these two will both get
             * the same messages
             */
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!conversationContainsMessage(message)){
                        Log.d(TAG, "ASDF");
                        WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
                        messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
                    }
                }
            });
            thread.run();
        }

        @Override
        public void onMessageSent(MessageClient messageClient, Message message, String s) {
            Log.d(TAG, "Displaying message");
            //Display the message just sent
            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
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

    /**
     * Checks if current conversation contains message using the sinch id
     * @param message message in question
     * @return True; Database contains message, False; Database doesn't contain message
     */
    private boolean conversationContainsMessage(Message message) {
        return messageAdapter.contains(message);
        //return JdbcDatabaseHandler.getInstance().messagesContains(message.getMessageId());
    }
}