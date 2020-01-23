package com.example.waygdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.GoalRow;
import androidx.fragment.app.FragmentManager;

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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.example.waygdemo.LoginActivity.mContext;
import static com.example.waygdemo.LoginActivity.mGoogleSignInClient;
import static com.example.waygdemo.LoginActivity.mOAuthLoginModule;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnMapClickListener, OnCompleteListener<QuerySnapshot> {

    private final long FINISH_INTERVAL_TIME = 200000;
    private long backPressedTime = 0;



    //for firestore
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";

    /*for Naver Map field*/
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    ArrayList<LatLng> coor = new ArrayList<LatLng>(); //coordinate of departure
    ArrayList<Marker> markerList = new ArrayList<Marker>(); //list of mark
    ArrayList<String> titleList = new ArrayList<String>(); //list of title
    ArrayList<String> cntList = new ArrayList<>();          //list of count
    NaverMap Map;
    int addMode = -1;
    String roomNumber;
    private FusedLocationSource locationSource;

    /*for Logout field*/
    Bundle bundle;
    TextView TextView_name;

    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setMyEmail();

        setStartLocation();

        //make FusedLocationProvider
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    // show my email
    public void setMyEmail(){
        TextView_name = findViewById(R.id.TextView_name);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        email = bundle.getString("email");// 다음 화면으로 넘겨야함
        StringTokenizer token = new StringTokenizer(email,"@");
        TextView_name.setText(token.nextToken());
    }

    //make option(start knu,zoom 14)
    public void setStartLocation(){
        FragmentManager fm = getSupportFragmentManager();
        NaverMapOptions options = new NaverMapOptions()
                .camera(new CameraPosition(new LatLng(35.8888403, 128.608111), 14));

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(options);
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Map = naverMap;
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true);//show real time traffic information

        //set UI
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);//현위치
        uiSettings.setScaleBarEnabled(true);//축척

        //location set
        naverMap.setLocationSource(locationSource);

        //addMarker from server
        db.collection("Location").get().addOnCompleteListener(this);

        naverMap.setOnMapClickListener(this);
    }

    //get Location info from server //db.collection("Location").get().addOnCompleteListener(this);
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            coor.clear();
            titleList.clear();
            cntList.clear();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                java.util.Map<String, Object> mapG = document.getData();
                GeoPoint cur = (GeoPoint) mapG.get("coordinate");
                String title =mapG.get("title").toString();
                cntList.add(mapG.get("cnt").toString());
                coor.add(new LatLng(cur.getLatitude(),cur.getLongitude()));
                titleList.add(title);
            }
        } else {
            Log.d(TAG, "Error getting documents: ", task.getException());
        }
        setMarkerList(Map);
    }

    @Override //set mapClick
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        //close all marker's info
        for (int i = 0; i < markerList.size(); i++) {
            if (markerList.get(i).hasInfoWindow()) markerList.get(i).getInfoWindow().close();
        }

        //make start location
        makeStart(latLng);
        /*
        Geocoder geocoder = new Geocoder(this);
        try {
            Toast.makeText(MapActivity.this,""+geocoder.getFromLocation(latLng.latitude,latLng.longitude,1),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    //make new start location
    public void makeStart(LatLng latLng){
        if (addMode > 0) {
            Marker marker = new Marker();
            marker.setPosition(latLng);
            marker.setMap(Map);

            //set marker add dialog
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
            alt_bld.setMessage("여기서 출발 하시겠습니까?").setCancelable(false)
                    .setPositiveButton("네",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MapActivity.this,MakeStartActivity.class);
                                    intent.putExtra("lat",latLng.latitude);
                                    intent.putExtra("lng",latLng.longitude);
                                    intent.putExtra("email",email);
                                    marker.setMap(null);
                                    //finish();
                                    startActivity(intent);
                                }
                            }).setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            marker.setMap(null);
                        }
                    });
            AlertDialog alert = alt_bld.create();
            alert.setTitle("출발지 생성");
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 255, 255, 255)));
            alert.show();

            addMode *= -1;
        }
    }

    //chang mode to add marker
    public void markerAdd(View v) {
        addMode *= -1;
    }

    //set marker
    public void setMarkerList(NaverMap naverMap) {
        //Toast.makeText(MapActivity.this,""+titleList.size(),Toast.LENGTH_SHORT).show();

        for (int i = 0; i < coor.size(); i++) {
            InfoWindow infoWindow = setInfoWindow(i);
            Marker marker = new Marker();

            // infowindow open and close
            Overlay.OnClickListener listener = overlay -> {
                if (marker.getInfoWindow() == null) infoWindow.open(marker);
                else infoWindow.close();
                return true;
            };
            marker.setOnClickListener(listener);

            //set marker coordinate and others
            marker.setPosition(coor.get(i));
            marker.setCaptionText(titleList.get(i));
            marker.setSubCaptionColor(Color.RED);
            marker.setSubCaptionHaloColor(Color.YELLOW);
            marker.setSubCaptionTextSize(10);

            //add to List
            markerList.add(marker);
            markerList.get(i).setMap(naverMap);
        }
    }

    //setInfoWindow
    public InfoWindow setInfoWindow(int i){
        InfoWindow infoWindow = new InfoWindow();

        //set location's chat roon count
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "현재 채팅방 수 "+cntList.get(i);
            }
        });

        //set infoWindow OnClickListener
        Overlay.OnClickListener listener1 = overlay -> {
            Toast.makeText(MapActivity.this,""+infoWindow.getMarker().getPosition(),Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MapActivity.this, RoomInfoActivity.class);
            LatLng coordinate = infoWindow.getMarker().getPosition();
            intent.putExtra("email",email);
            intent.putExtra("title",titleList.get(i));
            intent.putExtra("lat", coordinate.latitude);
            intent.putExtra("lng",coordinate.longitude);
            intent.putExtra("cnt",cntList.get(i));
            startActivity(intent);
            return true;
        };
        infoWindow.setOnClickListener(listener1);

        return infoWindow;
    }


    //refresh marker info
    public void refresh(View v){
        db.clearPersistence();
        db.collection("Location").get().addOnCompleteListener(this);
    }

    /*logout Button event ~ */
    public void LogoutOnClick(View v){
        final String type = bundle.getString("type");
        show_Dialog(v, type);
    }

    //Logout Dialog display
    private void show_Dialog(View v, final String t) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
        alt_bld.setMessage("로그아웃 하시겠습니까?").setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*실제 각 사이트의 로그아웃 method*/
                                if (t.equals("NAVER")) mOAuthLoginModule.logout(mContext);
                                else if (t.equals("GOOGLE")) mGoogleSignInClient.signOut();
                                /*초기화면으로*/
                                finish();
                                startActivity(new Intent(MapActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // 대화창 제목 설정
        alert.setTitle("로그아웃");
        // 대화창 아이콘 설정
        //alert.setIcon(R.drawable.check_dialog_64);
        // 대화창 배경 색 설정
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 255, 255, 255)));
        alert.show();
    }

    //permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    //back Press
    @Override
    public void onBackPressed() {
        backPressDialog();
    }

    //back press finish
    private void backPressDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("종료 하시겠습니까?").setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               MapActivity.super.onBackPressed();
                            }
                        }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // 대화창 제목 설정
        alert.setTitle("종료");
        // 대화창 배경 색 설정
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 255, 255, 255)));
        alert.show();
    }
}
