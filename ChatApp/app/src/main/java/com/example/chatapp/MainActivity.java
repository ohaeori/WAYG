package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText room_name, user_name, departure, arrival;
    private Button create_room;
    private ListView chat_list;

    /*내가 사용할 데이터베이스의 인터페이스를 불러옴*/
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        room_name = (EditText) findViewById(R.id.room_name);
        user_name = (EditText) findViewById(R.id.user_name);
        departure = (EditText) findViewById(R.id.departure);
        arrival = (EditText) findViewById(R.id.arrival);
        create_room = (Button) findViewById(R.id.create_room);
        chat_list = (ListView) findViewById(R.id.chat_list);

        /*Create Button event*/
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_name.getText().toString().equals("") || room_name.getText().toString().equals("")) {
                    ToastMessage("USER,ROOM NAME을 입력해주세요"); return;
                }else if (departure.getText().toString().equals("") || arrival.getText().toString().equals("")) {
                    ToastMessage("출발지, 도착지를 입력해주세요"); return;
                }

                Move_on_ChatActivity(room_name.getText().toString(), user_name.getText().toString()
                        ,"true");
                room_name.setText("");
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
                if (user_name.getText().toString().equals("")) {
                    ToastMessage("USER NAME을 입력해주세요");
                    return;
                }
                /*check num of participants... not over 4*/
                databaseReference.child("chat").child(adapter.getItem(position).toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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

                /*Dialog display*/
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(adapter.getItem(position) + "에 입장하시겠습니까?").setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Move_on_ChatActivity(adapter.getItem(position).toString(), user_name.getText().toString()
                                , "false");
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
            }
        });

        /*get data and add, del data etc... & add adapter, listener handling*/
        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
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

    private void ToastMessage(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private void Move_on_ChatActivity(String roomname, String username, String is_create) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("is_create", is_create);
        if(is_create.equals("true")) {
            intent.putExtra("departure", departure.getText().toString());
            intent.putExtra("arrival", arrival.getText().toString());
        }
        intent.putExtra("roomName", roomname);
        intent.putExtra("userName", username);
        startActivity(intent);
    }
}
