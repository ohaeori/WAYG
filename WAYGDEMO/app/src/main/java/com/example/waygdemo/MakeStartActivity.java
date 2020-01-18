package com.example.waygdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    String end_String;
    String start_String;
    String name_String;
    static String email_String;
    GeoPoint startPoint;
    GeoPoint endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_start);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        /*
        이부분에 전 액티비티에서 넘겨준 사용자이메일, 위경도 값을 저장해야함
        email_String = bundle.getString("EMAIL");
        startPoint =
        */
        Ldb = FirebaseFirestore.getInstance();
        input_end = findViewById(R.id.ed_end);      //edittext 와 XML 을 연결
        input_time = findViewById(R.id.ed_time);
        input_pay = findViewById(R.id.ed_pay);
        input_start = findViewById(R.id.ed_start);
        input_name = findViewById(R.id.ed_name);
        makeRoom = findViewById(R.id.findMap);
        getEnd = findViewById(R.id.upload);

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
                String mail_String = "test@test.com";               //사용자 메일정보. 인텐트로 넘긴걸 씀 여기선 테스트로 정적선언
                //GeoPoint geoPoint = new GeoPoint(35.7,128.7);   //핑 찍은 곳의 위경도값. 여긴 정적이지만 startPoint 값 써야함
                String docName = Double.toString(startPoint.getLatitude());
                docName = docName +","+Double.toString(startPoint.getLongitude());
                time_String = input_time.getText().toString();      //edittext에 입력한 문자열을 전달
                pay_String = input_pay.getText().toString();
                end_String = input_end.getText().toString();
                start_String = input_start.getText().toString();
                name_String = input_name.getText().toString();
                Map<String, Object> location = new HashMap<>();
                location.put("title",start_String);
                location.put("coordinate",startPoint);
                location.put("cnt",1);

                Map<String, Object> room = new HashMap<>();         // Map 의 형태로 DB에 저장
                room.put("destination",endPoint);
                room.put("time", time_String);
                room.put("dutch", pay_String);
                room.put("title", end_String);
                room.put("member", Arrays.asList(mail_String));
                room.put("memnum",1);
                //최초 시작점 db에 생성함.
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
                // 이러면 DB 에서 순서가 안꼬일지 체크 해봐야함
                Rdb.collection("Location").document(docName).collection("Rooms").document(name_String)  //이경로에 저장함.
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
        });
    }
}
