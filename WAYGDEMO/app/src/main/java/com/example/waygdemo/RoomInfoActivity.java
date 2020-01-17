package com.example.waygdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RoomInfoActivity extends AppCompatActivity {

    private EditText input_time;        //XML의 각각 editext 지정
    private EditText input_pay;
    private EditText input_end;
    private EditText input_id;
    private EditText input_name;
    Button genRoom;
    Button getRoom;
    String time_String = "";              //edittext 에서 받을 문자열들
    String pay_String = "";
    String end_String = "";
    String id_String = "";
    String name_String;
    FirebaseFirestore db;
    private static final String TAG = "DocSnippets";


    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);

        db = FirebaseFirestore.getInstance();
        input_end = findViewById(R.id.ed_end);      //edittext 와 XML 을 연결
        input_time = findViewById(R.id.ed_time);
        input_pay = findViewById(R.id.ed_pay);
        input_id = findViewById(R.id.ed_id);
        input_name = findViewById(R.id.ed_name);
        genRoom = findViewById(R.id.upload);
        getRoom = findViewById(R.id.download);

        genRoom.setOnClickListener(new Button.OnClickListener() {   // 방 생성 클릭 이벤트
            @Override
            public void onClick(View v) {
                //테스트 할려고 일단 정적으로 선언함
                String mail_String = "test@test.com";               //사용자 메일정보. 인텐트로 넘긴걸 쓰던가 api 호출하던가
                GeoPoint geoPoint = new GeoPoint(35.7,128.7);   //핑 찍은 곳의 위경도값
                String a = Double.toString(geoPoint.getLatitude());
                a = a +","+Double.toString(geoPoint.getLongitude());
                time_String = input_time.getText().toString();      //edittext에 입력한 문자열을 전달
                pay_String = input_pay.getText().toString();
                end_String = input_end.getText().toString();
                id_String = input_id.getText().toString();
                name_String = input_name.getText().toString();

                Map<String, Object> room = new HashMap<>();         // Map 의 형태로 DB에 저장
                room.put("time", time_String);
                room.put("dutch", pay_String);
                room.put("title", end_String);
                room.put("member", Arrays.asList(mail_String));
                room.put("memnum",1);

                db.collection("Location").document(a).collection("Rooms").document(name_String)  //이경로에 저장함
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
                //checkId();

            }
        });
    }
}
