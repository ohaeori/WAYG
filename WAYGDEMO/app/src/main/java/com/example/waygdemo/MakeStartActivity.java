package com.example.waygdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_start);

        getMyIntent();

        Ldb = FirebaseFirestore.getInstance();
        input_end = findViewById(R.id.ed_end);      //edittext 와 XML 을 연결
        input_time = findViewById(R.id.ed_time);
        input_pay = findViewById(R.id.ed_pay);
        input_start = findViewById(R.id.ed_start);
        input_name = findViewById(R.id.ed_name);
        makeRoom = findViewById(R.id.upload);
        getEnd = findViewById(R.id.findMap);

        getEnd.setOnClickListener(new Button.OnClickListener(){     //목적지 위치 지도에서 받기. 목적지의 위경도값 endPoint 에 저장
            @Override
            public void onClick(View v) {

            }
        });

        // 사용자가 빈칸을 놔두었을때도 핸들링 해야... 완성하면 주석 삭제
        makeRoom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*목적지좌표를 지도에서 안찍고 왔을 때 핸들링 해줘야함.
                 * if(endPoint == NULL){
                 *   Toast.makeText(this,"목적지 좌표 안찍었음",Toast.LENGTH_SHORT).show();
                 *   return;
                 * }
                 * */

                String docName = Double.toString(startPoint.getLatitude());
                docName = docName +","+Double.toString(startPoint.getLongitude());
                time_String = input_time.getText().toString();      //edittext에 입력한 문자열을 전달
                pay_String = input_pay.getText().toString();
                arrival = input_end.getText().toString();
                departure = input_start.getText().toString();
                roomname = input_name.getText().toString();
                Map<String, Object> location = new HashMap<>();
                location.put("title", departure);
                location.put("coordinate",startPoint);
                location.put("cnt",1);

//                Map<String, Object> room = new HashMap<>();         // Map 의 형태로 DB에 저장
//                room.put("destination",endPoint);
//                room.put("time", time_String);
//                room.put("dutch", pay_String);
//                room.put("title", arrival);
//                room.put("member", Arrays.asList(useremail));
//                room.put("memnum",1);
//                //최초 시작점 db에 생성함.
//                Ldb.collection("Location").document(docName)
//                        .set(location)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot written with ID: ");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
//                // 이러면 DB 에서 순서가 안꼬일지 체크 해봐야함
//                Rdb.collection("Location").document(docName).collection("Rooms").document(roomname)  //이경로에 저장함.
//                        .set(room)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot written with ID: ");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });

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
                startActivity(intent);
            }
        });
    }
    public void getMyIntent(){
        Intent intent = getIntent();
        bundle = intent.getExtras();
        startPoint = new GeoPoint(bundle.getDouble("lat"),bundle.getDouble("lng"));
        useremail = bundle.getString("email");

    }
}
