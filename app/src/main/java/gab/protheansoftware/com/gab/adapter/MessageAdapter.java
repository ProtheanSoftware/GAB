package gab.protheansoftware.com.gab.adapter;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import android.widget.Toast;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;

import gab.protheansoftware.com.gab.R;

/**
 * Created by boking on 2015-10-07.
 */
public class MessageAdapter extends BaseAdapter {
    public static final String TAG = "MESSAGE ADAPTER";

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<WritableMessage, Integer>> messages;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Activity activity){
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<WritableMessage, Integer>>();
    }

    public void addMessage(WritableMessage message, int direction){
        Log.d(TAG, "Adding message to adapater");
        messages.add(new Pair(message,direction));
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
            convertView = layoutInflater.inflate(res, parent, false);
        }

        WritableMessage message = messages.get(i).first;

        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message.getTextBody());

        return convertView;
    }

}
