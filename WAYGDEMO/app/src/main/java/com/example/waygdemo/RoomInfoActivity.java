package com.example.waygdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class RoomInfoActivity extends AppCompatActivity {
    private TextView user_email;
    private Button create_room;
    private ListView chat_list;
    private String email;

    /*load database interface for use*/
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    /*in database handler field*/
    private String select_room;
    private boolean is_listen =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        email = intent.getStringExtra("email");

        user_email = (TextView)findViewById(R.id.emailText);
        create_room = (Button) findViewById(R.id.create_room);
        chat_list = (ListView) findViewById(R.id.chat_list);

        user_email.setText(email);

        /*Create Button event*/
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomInfoActivity.this, MakeStartActivity.class);
                intent.putExtra("email",email);
                intent.putExtra("lat",bundle.getDouble("lat"));
                intent.putExtra("lng",bundle.getDouble("lng"));
                intent.putExtra("title",bundle.getString("title"));
                intent.putExtra("cnt",bundle.getString("cnt"));
                startActivity(intent);
            }
        });
        showChatList();
    }

    private void showChatList() {
        // 리스트 어댑터 생성 및 세팅 (arrayadater(어떤 액티비티에서, 어뎁터 뷰의 레이아웃. 여기서는 텍스트 뷰, 어뎁터 뷰가 보여줄 객체))
        final ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_list.setAdapter(adapter);

        /*ListView Click Event*/
        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if(!is_listen) is_listen = true;
                /*check num of participants... not over 4*/
                select_room = adapter.getItem(position);
                databaseReference.child("chat").child(adapter.getItem(position)).addValueEventListener(mvalueEventListener);
            }
        });

        /*get data and add, del data etc... & add adapter, listener handling*/
        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                ChatDBS chatDBS = child.next().getValue(ChatDBS.class);
                if(chatDBS.getDeparture().equals("ㅇ"))
                    adapter.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*realtime database listener*/
    private ValueEventListener mvalueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(!is_listen) return;
            for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                ChatDBS chatdbs = snapshot.getValue(ChatDBS.class);
                String key = snapshot.getKey();

                /*Dialog display*/
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomInfoActivity.this);
                builder.setTitle(select_room + "에 입장하시겠습니까?").setMessage(chatdbs.getParticipantsList()).setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean is_newParticipant = true;
                        for(String participant : chatdbs.getParticipants()){
                            if(participant.equals(user_email.getText().toString()))
                                is_newParticipant = false;
                        }
                        if(is_newParticipant){//if this user is new participant
                            if(chatdbs.getNum_of_user()==4) {
                                ToastMessage("정원초과입니다.");
                                return;
                            }
                            else {
                                chatdbs.addParticipants(user_email.getText().toString());//add participant
                                DatabaseReference keyRef = databaseReference.child("chat").
                                        child(select_room).child(key);
                                keyRef.setValue(chatdbs);
                            }
                        }
                        is_listen = false;
                        Move_on_ChatActivity(select_room, user_email.getText().toString(), "false");
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void ToastMessage(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private void Move_on_ChatActivity(String roomname, String useremail, String is_create) {
        Intent intent = new Intent(RoomInfoActivity.this, ChatActivity.class);
        intent.putExtra("is_create", is_create);
        intent.putExtra("roomName", roomname);
        intent.putExtra("userEmail", useremail);
        startActivity(intent);
    }
}
