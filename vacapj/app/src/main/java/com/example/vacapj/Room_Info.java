package com.example.vacapj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import static java.sql.Types.NULL;

public class Room_Info extends AppCompatActivity {

    long mem_Number;
    Button join_Room;
    TextView timeText;
    TextView payText;
    TextView mem1Text;
    TextView mem2Text;
    TextView mem3Text;
    TextView mem4Text;
    TextView grade1Text;
    TextView grade2Text;
    TextView grade3Text;
    TextView grade4Text;
    FirebaseFirestore db;
    DocumentReference docRef;
    private static final String TAG = "DocSnippets";
    String uid1;
    String uid2;
    String uid3;
    String uid4;
    private OnCompleteListener<DocumentSnapshot> onCompleteListener;
    static String grd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room__info);
        timeText = findViewById(R.id.output_time);
        payText  = findViewById(R.id.output_pay);
        mem1Text = findViewById(R.id.mem1);
        //mem2Text = findViewById(R.id.mem2);
        //mem3Text = findViewById(R.id.mem3);
        //mem4Text = findViewById(R.id.mem4);
        grade1Text = findViewById(R.id.grade1);
        //grade2Text = findViewById(R.id.grade2);
        //grade3Text = findViewById(R.id.grade3);
        //grade4Text = findViewById(R.id.grade4);


        db= FirebaseFirestore.getInstance();
        join_Room = findViewById(R.id.join_room);
        docRef = db.collection("Room").document("11");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String timeString;
                    String payString;

                    DocumentSnapshot document = task.getResult();
                    Map<String,Object> map = document.getData();
                    ArrayList<String> arrayList;
                    arrayList = (ArrayList)map.get("member");
                    mem_Number = (Long)map.get("memnum");
                    timeString = (String)map.get("time");
                    payString = (String)map.get("pay");
                    timeText.setText(timeString);
                    payText.setText(payString);
                    /*uid1 = arrayList.get(0);
                    mem1Text.setText(uid1);
                    uidRead(uid1);
                    System.out.println(grd);
                    grade1Text.setText(grd); //왜 grd 를 함수로 넘기는건 안되지?
                    if(mem_Number>1) {
                        uid2 = arrayList.get(1);
                        mem2Text.setText(uid2);
                        uidRead(uid2);
                        grade2Text.setText(grd);
                        if(mem_Number>2) {
                            uid3 = arrayList.get(2);
                            mem3Text.setText(uid3);
                            uidRead(uid3);
                            grade3Text.setText(grd);
                            if(mem_Number>3) {
                                uid4 = arrayList.get(3);
                                mem4Text.setText(uid4);
                                uidRead(uid4);
                                grade4Text.setText(grd);
                            }
                        }
                    }*/
                    for(int i = 0;i<arrayList.size();i++)
                        if(arrayList.indexOf(i)!=NULL) {
                            mem1Text.setText(mem1Text.getText().toString() + "\n\n\n" + arrayList.get(i));
                            uidRead(arrayList.get(i));
                        }
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }//여기서부터 아이디정보 기반으로 평점을 불러와야하는데...
    public void setGrd(String g)
    {
        Room_Info.grd = g;
        //this.grd = g;
    }

    public void uidRead (String uid){

        docRef = db.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            String gg;

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map1 = document.getData();
                    long sum =  (long)map1.get("gradesum");
                    long num =  (long)map1.get("gradenum");
                    double grade = (double)sum / (double)num;
                    gg = Double.toString(Math.round(grade*100)/100.0);
                    setGrd(gg);
                    Room_Info.grd = gg;
                    grade1Text.setText(grade1Text.getText().toString()+"\n\n\n"+gg);
                    SystemClock.sleep(5);
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }

        });

    }

}
/*
수정해야할 부분: 방정보를 익명클래스에서 열어 그 내부에서 유저정보를 다시 접근하는데 평점 순서가 뒤죽박죽.
얼핏 보기에는 스레드 꼬이는거랑 비슷해 보이기는 하는데...
 */