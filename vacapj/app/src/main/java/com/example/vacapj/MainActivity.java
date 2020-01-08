package com.example.vacapj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private EditText input_end;
    private EditText input_time;
    private EditText input_pay;

    Button genRoom;
    Button getRoom;
    String time_String="";
    String pay_String="";
    FirebaseFirestore db;
    private static final String TAG = "DocSnippets";

    TextView timeText;
    TextView payText;
    String gotTime="";
    String gotPay="";
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        input_time = findViewById(R.id.ed_time);
        input_pay=findViewById(R.id.ed_pay);
        genRoom= findViewById(R.id.upload);

        getRoom = findViewById(R.id.download);

        genRoom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_String = input_time.getText().toString();
                pay_String = input_pay.getText().toString();
                Map<String, String> room = new HashMap<>();
                room.put("time", time_String);
                room.put("pay", pay_String);

                db.collection("Room").document("4MT4DutqlAqxT4JpqzTj")
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

        getRoom.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
               timeText = findViewById(R.id.got_time);
               payText = findViewById(R.id.got_pay);
                docRef = db.collection("Room").document("4MT4DutqlAqxT4JpqzTj");

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        gotTime = document.getString("time");
                        gotPay = document.getString("pay");

                        timeText.setText(gotTime);
                        payText.setText(gotPay);
                    }
                });
                Intent intent = new Intent(MainActivity.this,Room_Info.class);
                startActivity(intent);
            }
        });
    }
}
/*생각해 봐야할 점들
방 만들 때
* 출발지는 핑 찍어서 생성한다 -> 경도 위도 기반으로 찍힐 것임.
* 도착지는 어떻게?? 단순히 경도위도로 받으면 좀 그럼..
* 검색의 용이성을 위해서라면 실제 지도 서비스처럼 해야하나?
* 실제는 단순지명 입력시 연관지명으로 저장된 주소값들이 나열되어서 선택함.
* 쉬운 방법은 목적지에다가 핑찍으라고 하는 것 -> 위도 경도 바로 받아오면 되니까
* 방장이 방만들면 방 정보엔 유저와 평가정보 또한 있어야함. 유저 id?
* 방생성시 채팅방 생성도 해야함. 앱 내에서 챗방 만들어야할 듯
*
* 검색기능
* 방 만들 때 도착지 받는 방법의 연장선일듯
* 도착지 기반으로 주위검색???
* 도착지 핑 찍고 그 주위로 검색 결과를 나열하는 것이 어떨지...
*
* 방 들어갈 때
* 지도에 생성된 핑 클릭시 or 검색으로 나열된 방 클릭시
* 해당 방 DB에 저장된 목적지,도착지 기준으로 경로 생성. -> 지도 API
* 생성된 경로 기준으로 예상비용...
* 방에 참여중인 멤버들과 평점
* 참여요청버튼 : 방장에게 푸쉬가서 허락해야함. -> 푸시 API
* 요청자의 평가정보 기반으로 수락/거부
* 사용자는 내가 어느 방에 들가있는지 확인가능해야함. -> 마이페이지??
* 방 나가기, 이용후 상호평가기능
*/