package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    int count = 0;

    private boolean is_create;

    private String ROOM_NAME;
    private String USER_NAME;
    private String DEPARTURE;
    private String ARRIVAL;

    private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_view = (ListView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);

        /*get intent info*/
        Intent intent = getIntent();
        ROOM_NAME = intent.getStringExtra("roomName");
        USER_NAME = intent.getStringExtra("userName");
        DEPARTURE = intent.getStringExtra("departure");
        ARRIVAL = intent.getStringExtra("arrival");
        if(intent.getStringExtra("is_create").equals("true")) is_create = true;
        else is_create = false;

        if(is_create) {
            ChatDBS chatDBS = new ChatDBS(USER_NAME, DEPARTURE, ARRIVAL);
            databaseReference.child("chat").child(ROOM_NAME).push().setValue(chatDBS);
        }

        // 채팅 방 입장
        openChat(ROOM_NAME);

        // 보내기 버튼에 대한 클릭리스너
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;

                _Message chat = new _Message(USER_NAME, chat_edit.getText().toString());
                databaseReference.child("chat").child(ROOM_NAME).child("messages").push().setValue(chat);
                chat_edit.setText("");
            }
        });
    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        _Message _message = dataSnapshot.getValue(_Message.class);
        //if(_message.getUserName().equals(USER_NAME)
        if(count%2==0) chat_view.setTextDirection(View.TEXT_DIRECTION_RTL);
        else chat_view.setTextDirection(View.TEXT_DIRECTION_LTR);
        adapter.add(_message.getUserName() + " : " + _message.getMessage());
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        _Message chatDBS = dataSnapshot.getValue(_Message.class);
        adapter.remove(chatDBS.getUserName() + " : " + chatDBS.getMessage());
    }

    private void openChat(String chatName) {
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_view.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").child(chatName).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot, adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMessage(dataSnapshot, adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}