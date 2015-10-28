package com.protheansoftware.gab.adapter;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import android.widget.Toast;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.protheansoftware.gab.R;

/**
 * Adapter for displaying messages, has different directions for incoming and outgoing,
 * automatically positions the messages to left or right depending on these.
 * @author boking
 * Created by boking on 2015-10-07.
 */
public class MessageAdapter extends BaseAdapter {
    public static final String TAG = "MESSAGE ADAPTER";

    //Used for positioning of messages in chat
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<WritableMessage, Integer>> messages;
    private LayoutInflater layoutInflater;

    /**
     * Initializes the message adapter using the Activity in order to inflate messages on the correct
     * side of the chat.
     * @param activity The activity of the message adapter
     */
    public MessageAdapter(Activity activity){
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<WritableMessage, Integer>>();
    }

    /**
     * Adds message to the message with a direction I.E if recieved or sent(left vs right) list,
     * notify frontend
     * @param message Target message
     * @param direction Direction, using DIRECTION_INCOMING or DIRECTION_OUTGOING
     */
    public void addMessage(WritableMessage message, int direction){
        Log.d(TAG, "Adding message to adapater");
        messages.add(new Pair(message,direction));
        Log.d(TAG, message.getMessageId());
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int i){
        return messages.get(i).second;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        int direction = getItemViewType(i);

        //Show message on left or right
        if(convertView == null){
            int res = 0;
            if(direction == DIRECTION_INCOMING){
                res = R.layout.message_left;
            }else if (direction == DIRECTION_OUTGOING){
                res = R.layout.message_right;
            }
            //Add to view
            convertView = layoutInflater.inflate(res, parent, false);
        }

        WritableMessage message = messages.get(i).first;
        //Add to array
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message.getTextBody());

        return convertView;
    }

    /**
     * Gets the latest sent message, perfect when you want to check who this chat belonged to.
     * @return Latest message with direction outgoing
     */
    public Pair<WritableMessage, Integer> getLatestSentMessage(){
        for(Pair<WritableMessage, Integer> temp : messages){
            if(temp.second == DIRECTION_OUTGOING){
                return temp;
            }
        }
        return null;
    }

    /**
     * Checks if the conversation already contains a message.
     * Doesn't currently work since "Sinch" converts WriteableMessage to Message which for some reason
     * changes the ID for some reason. In theory this should work.
     * @param message Message in question
     * @return true if exists, false else
     */
    public boolean contains(Message message) {
        for(Pair<WritableMessage, Integer> temp : messages){
            if(message.getMessageId().equals(temp.first.getMessageId())) return true;
        }
        return false;
    }
}
