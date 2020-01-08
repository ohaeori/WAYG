package com.example.n_login_test;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

import static com.example.n_login_test.MainActivity.mContext;
import static com.example.n_login_test.MainActivity.mOAuthLoginModule;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnMapClickListener {

    NaverMap Map;

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
//        String name = bundle.getString("name");
//
//        TextView_name.setText(name);

        /*logout Button event ~ */
        logout_Button.setClickable(true);
        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule.logout(mContext);
                finish();
                startActivity(new Intent(MapActivity.this, MainActivity.class));
            }
        });
        /* ~ logout Button event*/

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @UiThread
    @Override
    public void onMapReady(NaverMap naverMap) {
        naverMap.setMapType(NaverMap.MapType.Basic);
        naverMap.setSymbolScale(1.5f);
        Map = naverMap;

        naverMap.setOnMapClickListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
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
