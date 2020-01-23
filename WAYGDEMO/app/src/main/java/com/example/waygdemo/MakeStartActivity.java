package com.example.waygdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MakeStartActivity extends AppCompatActivity {

    FirebaseFirestore Ldb;
    FirebaseFirestore Rdb;
    private static final String TAG = "DocSnippets";
    private EditText input_time;        //XML의 각각 editext 지정
    private EditText input_pay;
    private EditText input_end;
    private EditText input_name;
    private EditText input_start;
    Button getEnd;
    Button makeRoom;
    String time_String;              //edittext 에서 받을 문자열들
    String pay_String;
    String arrival;
    String departure;
    String roomname;
    String useremail;
    GeoPoint startPoint;
    GeoPoint endPoint;
    Bundle bundle;
    int cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_start);

        input_end = findViewById(R.id.ed_end);      //edittext 와 XML 을 연결
        input_time = findViewById(R.id.ed_time);
        input_pay = findViewById(R.id.ed_pay);
        input_start = findViewById(R.id.ed_start);
        input_name = findViewById(R.id.ed_name);
        makeRoom = findViewById(R.id.upload);
        getEnd = findViewById(R.id.findMap);

        getMyIntent();

        getEnd.setOnClickListener(new Button.OnClickListener(){     //목적지 위치 지도에서 받기. 목적지의 위경도값 endPoint 에 저장
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakeStartActivity.this, GetDestination.class);
                startActivityForResult(intent, 0);
            }
        });

        makeRoom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String docName = Double.toString(startPoint.getLatitude());
                docName = docName +","+Double.toString(startPoint.getLongitude());
                time_String = input_time.getText().toString();      //edittext에 입력한 문자열을 전달
                pay_String = input_pay.getText().toString();
                arrival = input_end.getText().toString();
                departure = input_start.getText().toString();
                roomname = input_name.getText().toString();
                if (checkNullElements()) {
                    setParentDoc(docName);
                    setRoomDoc(docName);
                    makeChatAct();
                }
                else
                    Toast.makeText(MakeStartActivity.this,"양식을 모두 채워주세요",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getMyIntent(){                      // get intent of MapActivity
        Intent intent = getIntent();
        bundle = intent.getExtras();
        startPoint = new GeoPoint(bundle.getDouble("lat"),bundle.getDouble("lng"));
        useremail = bundle.getString("email");
        departure = bundle.getString("title");
        if(departure!=null) {
            input_start.setText(departure);
            input_start.setEnabled(false);
        }
        if(bundle.getString("cnt")!=null) {
            cnt = Integer.parseInt(bundle.getString("cnt"));
            System.out.println(cnt);
        }
        else cnt =0;
    }
    public void setParentDoc(String docName)        // make doc at "Location" collection by starting point
    {
        Ldb = FirebaseFirestore.getInstance();
        Map<String, Object> location = new HashMap<>();
        location.put("title", departure);
        location.put("coordinate",startPoint);
        location.put("cnt",++cnt);
        System.out.println(cnt);
        Ldb.collection("Location").document(docName)
                .set(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot written with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
    public void setRoomDoc(String docName)                  // make doc at "Room" subcollection
    {
        Rdb = FirebaseFirestore.getInstance();
        Map<String, Object> room = new HashMap<>();
        room.put("destination",endPoint);
        room.put("time", time_String);
        room.put("dutch", pay_String);
        room.put("title", arrival);
        room.put("member", Arrays.asList(useremail));
        room.put("memnum",1);

        Rdb.collection("Location").document(docName).collection("Room").document(roomname)  //save at this DB path
                .set(room)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot written with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    public boolean checkNullElements()
    {
        if(time_String.equals(""))
            return false;
        else if (pay_String.equals(""))
            return false;
        else if (arrival.equals(""))
            return false;
        else if (departure.equals(""))
            return false;
        else if (endPoint == null) {
            Toast.makeText(this,"목적지를 지도에서 찾아주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (roomname.equals(""))
            return false;
        return true;

    }
    public void makeChatAct()
    {
        /*
                    send intent
                    room_name, user_name, departure, arrival
                */
        Intent intent = new Intent(MakeStartActivity.this, ChatActivity.class);
        intent.putExtra("roomName", roomname);
        intent.putExtra("userEmail", useremail);
        intent.putExtra("departure", departure);
        intent.putExtra("arrival", arrival);
        intent.putExtra("is_create", "true");
        finish();
        RoomInfoActivity RA = (RoomInfoActivity)RoomInfoActivity._RoomInfoAct;
        if(RA!=null)
            RA.finish();
        startActivity(intent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)
        {
            Bundle bundle = data.getExtras();
            endPoint = new GeoPoint(bundle.getDouble("lat"),bundle.getDouble("lng"));
        }
    }
}
