package com.example.waygdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

import java.util.ArrayList;

import static com.example.waygdemo.LoginActivity.mContext;
import static com.example.waygdemo.LoginActivity.mGoogleSignInClient;
import static com.example.waygdemo.LoginActivity.mOAuthLoginModule;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnMapClickListener {
    /*for Naver Map field*/
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    ArrayList<LatLng> coor = new ArrayList<LatLng>(); //coordinate of departure
    ArrayList<Marker> markerList = new ArrayList<Marker>(); //list of mark
    ArrayList<String> titleList = new ArrayList<String>(); //list of title
    NaverMap Map;
    int mapMode = -1;
    int addMode = -1;
    private FusedLocationSource locationSource;

    /*for Logout field*/
    TextView TextView_name;
    Button logout_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        TextView_name = findViewById(R.id.TextView_name);
        logout_Button = findViewById(R.id.logout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        final String type = bundle.getString("type");

        TextView_name.setText(name);

        /*logout Button event ~ */
        logout_Button.setClickable(true);
        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_Dialog(v, type);
            }
        });
        /*logout Button event fin*/

        FragmentManager fm = getSupportFragmentManager();

        //make option(start knu,zoom 14)
        NaverMapOptions options = new NaverMapOptions()
                .camera(new CameraPosition(new LatLng(35.8888403, 128.608111), 14));

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(options);
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        //make FusedLocationProvider
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
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

        setMarkerList(naverMap);

        naverMap.setOnMapClickListener(this);
    }

    //change map mode
    public void changeMode(View v) {
        if (mapMode > 0) Map.setMapType(NaverMap.MapType.Basic);
        if (mapMode < 0) Map.setMapType(NaverMap.MapType.Navi);
        mapMode *= -1;
    }

    //change camera
    public void changeCamera(View v) {
        //LocationOverlay locationOverlay = Map.getLocationOverlay(); //
        //Map.moveCamera(CameraUpdate.scrollTo(locationOverlay.getPosition())); get current location LatLng
        Map.moveCamera(CameraUpdate.scrollTo(new LatLng(35.8796713, 128.6262873)).animate(CameraAnimation.Easing));
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

    //set marker example
    public void setMarker(NaverMap naverMap) {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(35.8796713, 128.6262873));
        marker.setMap(naverMap);

        Button button = (Button) findViewById(R.id.markerBtn);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.setPosition(new LatLng(35.8799713, 128.6262873));
                marker.setIcon(MarkerIcons.BLUE);
                //marker.setMap(null);
                marker.setWidth(Marker.SIZE_AUTO);
                marker.setHeight(Marker.SIZE_AUTO);
            }
        });


        //set infoWindow
        InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "정보 창 내용";
            }
        });

        infoWindow.open(marker);

        naverMap.setOnMapClickListener((coord, point) -> {
            infoWindow.close();
        });

        Overlay.OnClickListener listener = overlay -> {

            if (marker.getInfoWindow() == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker);
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        };

        marker.setOnClickListener(listener);

        Overlay.OnClickListener listener1 = overlay -> {
            Toast.makeText(MapActivity.this, "!!!!!!", Toast.LENGTH_SHORT).show();
            return true;
        };

        infoWindow.setOnClickListener(listener1);

    }

    public void setMarkerList(NaverMap naverMap) {

        titleList.add("동대구역 1번 출구");
        titleList.add("동대구역 2번 출구");
        titleList.add("동대구역 3번 출구");
        titleList.add("동대구역 4번 출구");
        titleList.add("동대구역 5번 출구");

        coor.add(new LatLng(35.8820713, 128.6262873));
        coor.add(new LatLng(35.8778713, 128.6262873));
        coor.add(new LatLng(35.8799713, 128.6262873));
        coor.add(new LatLng(35.8799713, 128.6283873));
        coor.add(new LatLng(35.8799713, 128.6241873));


        for (int i = 0; i < coor.size(); i++) {
            Marker marker = new Marker();
            InfoWindow infoWindow = new InfoWindow();

            // infowindow open
            Overlay.OnClickListener listener = overlay -> {
                if (marker.getInfoWindow() == null) {
                    // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                    infoWindow.open(marker);
                } else {
                    // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                    infoWindow.close();
                }

                return true;
            };

            marker.setOnClickListener(listener);

            //set marker coordinate

            marker.setPosition(coor.get(i));
            marker.setCaptionText(titleList.get(i));
            marker.setCaptionColor(Color.BLUE);
            markerList.add(marker);

            markerList.get(i).setMap(naverMap);

            //set marker's infoWindow
            int a = i + 2;

            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return "현제 채팅방 수 " + a;
                }
            });
            infoWindow.open(markerList.get(i));

            Overlay.OnClickListener listener1 = overlay -> {
                //Toast.makeText(Main2Activity.this,""+infoWindow.getMarker().getPosition(),Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
//                String title = "" + infoWindow.getMarker().getPosition();
//                intent.putExtra("title", title);
//
//                startActivity(intent);

                return true;
            };

            infoWindow.setOnClickListener(listener1);

        }
    }

    public void search(View v) {

    }

    public void markerAdd(View v) {
        addMode *= -1;
    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

        //close all marker's info
        for (int i = 0; i < markerList.size(); i++) {
            if (markerList.get(i).hasInfoWindow()) markerList.get(i).getInfoWindow().close();
        }

        if (addMode > 0) {
            Marker marker1 = new Marker();
            marker1.setPosition(latLng);
            marker1.setMap(Map);
            marker1.setSubCaptionText("????????");
            marker1.setSubCaptionColor(Color.RED);
            marker1.setSubCaptionHaloColor(Color.YELLOW);
            marker1.setSubCaptionTextSize(10);

            InfoWindow infoWindow1 = new InfoWindow();
            infoWindow1.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return "!!!!!!!!!!!!";
                }
            });

            infoWindow1.open(marker1);
        }

    }

    //Dialog display
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
}
