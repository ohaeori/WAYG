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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;

import static java.sql.Types.NULL;

interface Callback
{
    void firebaseResponseCallback(ArrayList<String> result);
}

public class Room_Info extends AppCompatActivity {
    int index;
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
    static ArrayList<String> grdList = new ArrayList<>();
    static ArrayList<String> arrayList =new ArrayList<>();
    static ArrayList<String>  memList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room__info);
        timeText = findViewById(R.id.output_time);
        payText  = findViewById(R.id.output_pay);
        mem1Text = findViewById(R.id.mem1);
        grade1Text = findViewById(R.id.grade1);
        join_Room = findViewById(R.id.join_room);

        memRead(new Callback() {
            @Override
            public void firebaseResponseCallback(ArrayList<String> result) {
                memList = result;
                System.out.println(memList);
                uidRead(memList.get(0), new Callback() {
                    @Override
                    public void firebaseResponseCallback(ArrayList<String> result) {
                        System.out.println(result.get(0));
                        grdList.add(0,result.get(0));
                        if(memList.size()>1) {
                            uidRead(memList.get(1), new Callback() {
                                @Override
                                public void firebaseResponseCallback(ArrayList<String> result) {
                                    System.out.println(result.get(0));
                                    grdList.add(1,result.get(0));
                                    if (memList.size() > 2) {
                                        uidRead(memList.get(2), new Callback() {
                                            @Override
                                            public void firebaseResponseCallback(ArrayList<String> result) {
                                                System.out.println(result.get(0));
                                                grdList.add(2,result.get(0));
                                                if(memList.size()>3){
                                                    uidRead(memList.get(3), new Callback() {
                                                        @Override
                                                        public void firebaseResponseCallback(ArrayList<String> result) {
                                                            System.out.println(result.get(0));
                                                            grdList.add(3,result.get(0));
                                                            printGrade();
                                                        }
                                                    });
                                                }
                                                else printGrade();
                                            }
                                        });

                                    }else printGrade();
                                }
                            });

                        }else printGrade();
                    }
                });
            }
        });

    }
    void printGrade(){
        for (int i = 0; i<grdList.size();i++)
        {
            if(grdList.indexOf(i)!=NULL)
                grade1Text.setText(grade1Text.getText().toString() + "\n\n\n" + grdList.get(i));
        }
    }
    public  void  memRead(final Callback callback)
    {
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

                    memList = (ArrayList)map.get("member");

                    mem_Number = (Long)map.get("memnum");
                    timeString = (String)map.get("time");
                    payString = (String)map.get("pay");
                    timeText.setText(timeString);
                    payText.setText(payString);
                    room_Id=document.getId();
                    System.out.println(room_Id);
                    for(int i = 0;i<memList.size();i++)
                        if(memList.indexOf(i)!=NULL) {
                            mem1Text.setText(mem1Text.getText().toString() + "\n\n\n" + memList.get(i));
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
                callback.firebaseResponseCallback(memList);
            }
        });

    }

    public void uidRead (String uid,final Callback callback) {

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
                        arrayList.add(0,gg);
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

/*
�닔�젙�빐�빞�븷 遺�遺�: 諛⑹젙蹂대�� �씡紐낇겢�옒�뒪�뿉�꽌 �뿴�뼱 洹� �궡遺��뿉�꽌 �쑀���젙蹂대�� �떎�떆 �젒洹쇳븯�뒗�뜲 �룊�젏 �닚�꽌媛� �뮘二쎈컯二�.
�뼹�븦 蹂닿린�뿉�뒗 �뒪�젅�뱶 瑗ъ씠�뒗嫄곕옉 鍮꾩듂�빐 蹂댁씠湲곕뒗 �븯�뒗�뜲...
01.13
�뙆�씠�뼱�뒪�넗�뼱 api �뒗 鍮꾨룞湲곗떇�씠�씪 �벐�젅�뱶泥섎읆 �븯�굹�뒗 api �떎�뻾�븯怨� �븯�굹�뒗 洹� �떎�쓬以� �떎�뻾�븿.
memlist 瑜� �닚李⑥쟻�쑝濡� uidread �븯硫� 鍮꾨룞湲곗떇�씠�씪 癒쇱� �걹�굹�뒗�냸�씠 �쐞�뿉 �뜥吏�.
 */