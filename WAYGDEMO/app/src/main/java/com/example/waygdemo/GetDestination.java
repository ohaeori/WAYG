package com.example.waygdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class GetDestination extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnMapClickListener{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    NaverMap Map;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_destination);

        setStartLocation();
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

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
        naverMap.setOnMapClickListener(this);
    }
    @Override //set mapClick
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        //close all marker's info

        //make start location
        Marker marker = new Marker();
        marker.setPosition(latLng);
        marker.setMap(Map);

        //set marker add dialog
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("여기를 도착지로 하시겠습니까?").setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(GetDestination.this,MakeStartActivity.class);
                                intent.putExtra("lat",latLng.latitude);
                                intent.putExtra("lng",latLng.longitude);
                                setResult(1,intent);
                                finish();
                            }
                        }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        marker.setMap(null);
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.setTitle("도착지 생성");
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 255, 255, 255)));
        alert.show();

    }
}
