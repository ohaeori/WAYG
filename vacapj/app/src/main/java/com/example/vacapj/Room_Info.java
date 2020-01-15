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
    Map<String,Object> mapS = new HashMap<>();		// initialize hash-map
    GeoPoint startCoor;
    String title_String;
    startCoor = //좌표값;
    mapS.put("coordinate",startCoor);
    mapS.put("title",title_String);
    
    db.collection("Location")
            .add(mapS)								//upload at server mapS's data by auto ID
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
}


/*
占쎈땾占쎌젟占쎈퉸占쎈튊占쎈막 �겫占썽겫占�: 獄쎻뫗�젟癰귣�占쏙옙 占쎌뵡筌뤿굟寃�占쎌삋占쎈뮞占쎈퓠占쎄퐣 占쎈였占쎈선 域뱄옙 占쎄땀�겫占쏙옙肉됵옙苑� 占쎌�占쏙옙占쎌젟癰귣�占쏙옙 占쎈뼄占쎈뻻 占쎌젔域뱀눛釉�占쎈뮉占쎈쑓 占쎈즸占쎌젎 占쎈떄占쎄퐣揶쏉옙 占쎈츟雅뚯럥而�雅뚳옙.
占쎈섰占쎈를 癰귣떯由곤옙肉됵옙�뮉 占쎈뮞占쎌쟿占쎈굡 �몭�딆뵠占쎈뮉椰꾧퀡�삂 �뜮袁⑸뱛占쎈퉸 癰귣똻�뵠疫꿸퀡�뮉 占쎈릭占쎈뮉占쎈쑓...
01.13
占쎈솁占쎌뵠占쎈선占쎈뮞占쎈꽅占쎈선 api 占쎈뮉 �뜮袁⑤짗疫꿸퀣�뻼占쎌뵠占쎌뵬 占쎈쾺占쎌쟿占쎈굡筌ｌ꼶�쓥 占쎈릭占쎄돌占쎈뮉 api 占쎈뼄占쎈뻬占쎈릭�⑨옙 占쎈릭占쎄돌占쎈뮉 域뱄옙 占쎈뼄占쎌벉餓ο옙 占쎈뼄占쎈뻬占쎈맙.
memlist �몴占� 占쎈떄筌△뫁�읅占쎌몵嚥∽옙 uidread 占쎈릭筌롳옙 �뜮袁⑤짗疫꿸퀣�뻼占쎌뵠占쎌뵬 �솒�눘占� 占쎄국占쎄돌占쎈뮉占쎈꺒占쎌뵠 占쎌맄占쎈퓠 占쎈쑅筌욑옙.
 */