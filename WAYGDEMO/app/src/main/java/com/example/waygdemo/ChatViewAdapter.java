package com.example.waygdemo;

import android.content.Context;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.Distribution;

import java.util.ArrayList;

public class ChatViewAdapter extends BaseAdapter {
    private ArrayList<_Message> _messageList;
    private Context mContext;
    private boolean isMe;

    public ChatViewAdapter(){
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

    public void setIsMe(boolean b){
        isMe = b;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_messagelist, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView other_id = (TextView) convertView.findViewById(R.id.other_id);
        TextView otherChat_mess = (TextView) convertView.findViewById(R.id.other_chat_View);
        TextView myChat_mess = (TextView) convertView.findViewById(R.id.my_chat_View);
        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        _Message messageViewItem = _messageList.get(position);

        other_id.setText(messageViewItem.getNametext());
        otherChat_mess.setText(messageViewItem.getLefttext());
        myChat_mess.setText(messageViewItem.getRighttext());

        return convertView;
    }

    public void addItem(String username, String message) {
        _Message item = new _Message(username, message);
        if(isMe) item.setRighttext();
        else {
            item.setLefttext();
            item.setNametext();
        }

        _messageList.add(item);
    }

    public void clearItem(){
        _messageList.clear();
    }
}
