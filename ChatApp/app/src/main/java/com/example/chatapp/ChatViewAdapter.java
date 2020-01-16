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
    private boolean isMe;

    public ChatViewAdapter(){ }
    public ChatViewAdapter(Context context){
        this.mContext = context;
        this._messageList =  new ArrayList<_Message>();
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

    public void setIsMe(boolean a){
        isMe = a;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        _Message _message = _messageList.get(position);
        holder = new ViewHolder();
        mContext = parent.getContext();


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.listitemrow, parent, false);

        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView textView1 = (TextView) convertView.findViewById(R.id.textView1);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        _Message listViewItem = _messageList.get(position);

        textView1.setText(listViewItem.getRowtext1());
        textView2.setText(listViewItem.getRowtext2());

        /*
        convertView.setTag(holder);

        holder.id = (TextView) convertView.findViewById(R.id.userID);
        holder.message = (TextView) convertView.findViewById(R.id.message);

        holder.id.setText(_message.getUserName());
        holder.message.setText(_message.getMessage());
*/
        return convertView;
    }

    public void addItem(String username, String message) {
        _Message item = new _Message(username, message);
        if(isMe)item.setRowtext2();
        else item.setRowtext1();

        _messageList.add(item);
    }

    public void clearItem(){
        _messageList.clear();
    }
}
