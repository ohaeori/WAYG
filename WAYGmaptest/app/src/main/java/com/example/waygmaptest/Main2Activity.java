package com.example.waygmaptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback {

    NaverMap Map;
    int mapMode = -1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FragmentManager fm = getSupportFragmentManager();

        //make option(start knu,zoom 14)
        NaverMapOptions options = new NaverMapOptions()
                .camera(new CameraPosition(new LatLng(35.8888403,128.608111), 14));

        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
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
        Map=naverMap;
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC,true);//show real time traffic information

        //set UI
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);//현위치
        uiSettings.setScaleBarEnabled(true);//축척

        //location set
        naverMap.setLocationSource(locationSource);

        /*marker mode change if map mode change
        naverMap.addOnOptionChangeListener(() -> {
            if (naverMap.isNightModeEnabled()) {
                //marker.setIcon(OverlayImage.fromResource(R.drawable.night_icon));
            } else {
                //marker.setIcon(OverlayImage.fromResource(R.drawable.day_icon));
            }
        });*/

    }

    //change map mode
    public void changeMode(View v){
        if (mapMode>0) Map.setMapType(NaverMap.MapType.Basic);
        if (mapMode<0)Map.setMapType(NaverMap.MapType.Navi);
        mapMode*=-1;
    }

    //change camera
    public void changeCamera(View v){
        Map.moveCamera(CameraUpdate.scrollTo(new LatLng(35.8796713,128.6262873)).animate(CameraAnimation.Easing));
    }

    //permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }
}
