package com.example.vacapj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.NULL;

interface Callback
{
    void firebaseResponseCallback(ArrayList<String> result);
}

public class Room_Info extends AppCompatActivity {

    long mem_Number;
    Button join_Room;
    TextView timeText;
    TextView payText;
    TextView destiText;
    TextView mem1Text;
    TextView grade1Text;
    TextView roomText;
    FirebaseFirestore db;
    DocumentReference docRef;
    private static final String TAG = "DocSnippets";
    private OnCompleteListener<DocumentSnapshot> onCompleteListener;
    static ArrayList<String> grdList = new ArrayList<>();
    static ArrayList<String> arrayList = new ArrayList<>();
    static ArrayList<String> memList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room__info);
        timeText = findViewById(R.id.output_time);
        payText = findViewById(R.id.output_pay);
        destiText = findViewById(R.id.out_desti);
        mem1Text = findViewById(R.id.mem1);
        grade1Text = findViewById(R.id.grade1);
        roomText = findViewById(R.id.out_room);
        join_Room = findViewById(R.id.join_room);
    //simpleRead();
        memRead(new Callback() {
            @Override
            public void firebaseResponseCallback(ArrayList<String> result) {
                memList = result;
              // System.out.println(memList);
                uidRead(memList.get(0), new Callback() {
                    @Override
                    public void firebaseResponseCallback(ArrayList<String> result) {
                        //System.out.println(result.get(0));
                        grdList.add(0, result.get(0));
                        if (memList.size() > 1) {
                            uidRead(memList.get(1), new Callback() {
                                @Override
                                public void firebaseResponseCallback(ArrayList<String> result) {
                                   // System.out.println(result.get(0));
                                    grdList.add(1, result.get(0));
                                    if (memList.size() > 2) {
                                        uidRead(memList.get(2), new Callback() {
                                            @Override
                                            public void firebaseResponseCallback(ArrayList<String> result) {
                                                //System.out.println(result.get(0));
                                                grdList.add(2, result.get(0));
                                                if (memList.size() > 3) {
                                                    uidRead(memList.get(3), new Callback() {
                                                        @Override
                                                        public void firebaseResponseCallback(ArrayList<String> result) {
                                                            //System.out.println(result.get(0));
                                                            grdList.add(3, result.get(0));
                                                            printGrade();
                                                        }
                                                    });
                                                } else printGrade();
                                            }
                                        });

                                    } else printGrade();
                                }
                            });

                        } else printGrade();
                    }
                });
            }
        });

        getGeopoint();
    }

    void printGrade() {
        for (int i = 0; i < grdList.size(); i++) {
            if (grdList.indexOf(i) != NULL)
                grade1Text.setText(grade1Text.getText().toString() + "\n\n\n" + grdList.get(i));
        }
    }

    public void memRead(final Callback callback) {
        db = FirebaseFirestore.getInstance();
        // 일단 정적으로 선언해서 읽어왔음.
        // 동적: docRef = db.collection("Location").document(/*위경도?*/).collection("Room").document(/*방 id*/);
        docRef = db.collection("Location").document("0HxUmGGPAtxSfyljj450").collection("Room").document("여기가실분?");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String timeString;
                    String payString;
                    String room_Id;
                    String destString;
                    String roomString;
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map = document.getData();
                    memList = (ArrayList) map.get("member");
                    mem_Number = (Long) map.get("memnum");
                    destString = (String) map.get("title");
                    timeString = (String) map.get("time");
                    payString = (String) map.get("dutch");
                    roomString = document.getId();
                    timeText.setText(timeString);
                    payText.setText(payString);
                    destiText.setText(destString);
                    roomText.setText(roomString);
                    room_Id = document.getId();
                  //  System.out.println(room_Id);
                    for (int i = 0; i < memList.size(); i++)
                        if (memList.indexOf(i) != NULL) {
                            mem1Text.setText(mem1Text.getText().toString() + "\n\n\n" + memList.get(i));
                        }

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                callback.firebaseResponseCallback(memList);
            }
        });

    }

    public void uidRead(String uid, final Callback callback) {

        docRef = db.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            String gg;

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map1 = document.getData();
                    long sum = (long) map1.get("gradesum");
                    long num = (long) map1.get("gradenum");
                    double grade = (double) sum / (double) num;
                    gg = Double.toString(Math.round(grade * 100) / 100.0);
                    //grade1Text.setText(grade1Text.getText().toString()+"\n\n\n"+gg);
                    arrayList.add(0, gg);
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

                callback.firebaseResponseCallback(arrayList);
            }
        });
    }

    public void getGeopoint() {

        db.collection("Room")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                GeoPoint cur;
                                Map<String,Object> mapG = document.getData();
                                cur = (GeoPoint)mapG.get("start");

                                //System.out.println(cur);
                                //coor.add(new LatLng(cur.getLatitude(),cur.getLongitude());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void setGeopoint()
    {
        Map<String,Object> mapS = new HashMap<>();
        GeoPoint startCoor;
        String title_String = "";
          //  mapS.put("coordinate",startCoor);
            mapS.put("title",title_String);

        db.collection("Location")
                .add(mapS)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    public void simpleRead(){   //핑 위 메시지칸 눌렀을 때 방들의 간단한 정보들 출력?
        //현 위치 문서 이름은 받아와야함.
        FirebaseFirestore sdb = FirebaseFirestore.getInstance();
        sdb.collection("Location").document(/*현위치문서이름*/"0HxUmGGPAtxSfyljj450").collection("Room")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String,Object> mapZ = new HashMap<>();
                                mapZ = document.getData();
                                String time;
                                String desti;
                                time = (String)mapZ.get("time");
                                desti = (String)mapZ.get("title");
                                System.out.println(document.getId()+time+desti);
                                /* 이거를 한 단위의 UI 에 한 문서씩 집어넣어줘야함니다.
                                글고 만든 UI 하나마다 클릭이벤트가 있어서 클릭하면
                                문서id 를 인텐트로 넘겨서 새 액티비티에 상세정보를 출력함니다.
                                 */
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}



/*
수정해야할 부분: 방정보를 익명클래스에서 열어 그 내부에서 유저정보를 다시 접근하는데 평점 순서가 뒤죽박죽.
얼핏 보기에는 스레드 꼬이는거랑 비슷해 보이기는 하는데...
01.13
파이어스토어 api 는 비동기식이라 쓰레드처럼 하나는 api 실행하고 하나는 그 다음줄 실행함.
memlist 를 순차적으로 uidread 하면 비동기식이라 먼저 끝나는놈이 위에 써짐.
 */