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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText input_time;        //XML의 각각 editext 지정
    private EditText input_pay;
    private EditText input_end;
    private EditText input_id;
    Button genRoom;
    Button getRoom;
    String time_String="";              //edittext 에서 받을 문자열들
    String pay_String="";
    String end_String="";
    String id_String="";
    FirebaseFirestore db;
    private static final String TAG = "DocSnippets";

    TextView timeText;                  // 서버에서 받아온 내용을 출력할 textview
    TextView payText;
    String gotTime="";                  // 서버에서 받은 내용을 저장할 변수
    String gotPay="";
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        input_end = findViewById(R.id.ed_end);      //edittext 와 XML 을 연결
        input_time = findViewById(R.id.ed_time);
        input_pay=findViewById(R.id.ed_pay);
        input_id = findViewById(R.id.ed_id);
        genRoom= findViewById(R.id.upload);
        getRoom = findViewById(R.id.download);

        genRoom.setOnClickListener(new Button.OnClickListener() {   // 방 생성 클릭 이벤트
            @Override
            public void onClick(View v) {
                time_String = input_time.getText().toString();      //edittext에 입력한 문자열을 전달
                pay_String = input_pay.getText().toString();
                end_String = input_end.getText().toString();
                id_String = input_id.getText().toString();
                Map<String, String> room = new HashMap<>();         // Map 의 형태로 DB에 저장
                room.put("time", time_String);
                room.put("pay", pay_String);
                room.put("end_String",end_String);
                db.collection("Room").document("4MT4DutqlAqxT4JpqzTj")  //이경로에 저장함
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
                db.collection("Users").whereEqualTo("id",id_String).get()   // 아이디 찾기
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().isEmpty()) {                            // Query 수행한 Task 가 비어 있으면??
                                        Toast myToast = Toast.makeText(MainActivity.this,"아이디 없음", Toast.LENGTH_SHORT);
                                        myToast.show();
                                        Map<String,Object> map2 = new HashMap<>();
                                        map2.put("gradenum",0);
                                        map2.put("gradesum",0);
                                        map2.put("id",id_String);
                                        db.collection("Users").document(id_String)  // 서버에 아이디가 없으니 아이디를 저장, 초기화
                                                .set(map2)
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
                                    else {
                                        for (QueryDocumentSnapshot document : task.getResult()) {           //아이디가 있으면 정보를 출력
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            Toast myToast = Toast.makeText(MainActivity.this, "아이디 있음", Toast.LENGTH_SHORT);
                                            myToast.show();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        getRoom.setOnClickListener(new Button.OnClickListener(){        //정보 불러오기 클릭 이벤트
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
                Intent intent = new Intent(MainActivity.this,Room_Info.class);  // 상세정보 출력위한 액티비티로 넘어감
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