package com.example.chatapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatViewAdapter extends BaseAdapter {
    private ArrayList<_Message> _messageList = new ArrayList<_Message>();
    private Context mContext;

    public ChatViewAdapter(){ }
    public ChatViewAdapter(Context context, ArrayList<_Message> arrayList){
        this.mContext = context;
        this._messageList = arrayList;
    }

    @Override
    public int getCount() {
        return _messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return _messageList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    public class ViewHolder{
        TextView id;
        TextView message;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        _Message _message = _messageList.get(position);
        Log.d("mytag", "in here...");
        holder = new ViewHolder();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(_message.getRes(), parent, false);

        convertView.setTag(holder);

        holder.id = (TextView) convertView.findViewById(R.id.userID);
        holder.message = (TextView) convertView.findViewById(R.id.message);

        holder.id.setText(_message.getUserName());
        holder.message.setText(_message.getMessage());

        return convertView;
    }

    public void addItem(String username, String message) {
        _Message item = new _Message(username, message);
        _messageList.add(item);
    }

    public void clearItem(){
        _messageList.clear();
    }
}
