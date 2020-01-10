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
    TextView grade1Text;
    FirebaseFirestore db;
    DocumentReference docRef;
    private static final String TAG = "DocSnippets";
    private OnCompleteListener<DocumentSnapshot> onCompleteListener;
    static String grd;
    ArrayList<String>  memlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room__info);
        timeText = findViewById(R.id.output_time);
        payText  = findViewById(R.id.output_pay);
        mem1Text = findViewById(R.id.mem1);
        grade1Text = findViewById(R.id.grade1);
        join_Room = findViewById(R.id.join_room);

        db= FirebaseFirestore.getInstance();
        docRef = db.collection("Room").document("11");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String timeString;
                    String payString;
String room_Id;
                    DocumentSnapshot document = task.getResult();
                    Map<String,Object> map = document.getData();
                    ArrayList<String> arrayList;
                    arrayList = (ArrayList)map.get("member");
                    memlist = arrayList;
                    mem_Number = (Long)map.get("memnum");
                    timeString = (String)map.get("time");
                    payString = (String)map.get("pay");
                    timeText.setText(timeString);
                    payText.setText(payString);
room_Id=document.getId();
System.out.println(room_Id);
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
        /*for(int i=0;i<memlist.size();i++) {
            if (memlist.indexOf(i) != NULL) {
                uidRead(memlist.get(i));
            }
        }*/
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