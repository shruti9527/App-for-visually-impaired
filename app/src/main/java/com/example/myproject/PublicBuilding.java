package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PublicBuilding extends AppCompatActivity {

    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_building);

        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        String[] placeNameList = {"Public Building","ATM", "Hospital", "Restaurant"};

        spType.setAdapter(new ArrayAdapter<>(PublicBuilding.this, android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(PublicBuilding.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(PublicBuilding.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNearbyPublicBuildings();
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            public void onSuccess(Location location) {

                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 10
                            ));
                        }
                    });
                }
            }
        });
    }

    private void displayNearbyPublicBuildings() {
        if (map != null) {
            map.clear(); // Clear previous markers

//            // Add markers for 2-3 nearby ATMs (hardcoded latitude and longitude values)
//            List<LatLng> atms = new ArrayList<>();
//            atms.add(new LatLng(18.489460, 73.813770)); //State bank atm
//            atms.add(new LatLng(9.990741, 76.287924)); //icici bank atm
//            atms.add(new LatLng(18.500350, 73.839140));//bom atm
//
//            for (LatLng atm : atms) {
//                map.addMarker(new MarkerOptions().position(atm).title("ATM"));
            List<LatLng> publicbuilding = new ArrayList<>();
            publicbuilding.add(new LatLng(18.489460, 73.813770)); // State bank atm
            publicbuilding.add(new LatLng(18.489300, 73.814460)); // ICICI bank atm
            publicbuilding.add(new LatLng(18.490000, 73.822440)); // BoM atm

            List<String> publicbuildingNames = new ArrayList<>();
            publicbuildingNames.add("State Bank ATM");
            publicbuildingNames.add("ICICI Bank ATM");
            publicbuildingNames.add("Federal Bank ATM");

            for (int i = 0; i < publicbuilding.size(); i++) {
                LatLng publicbuildingLatLng = publicbuilding.get(i);
                String publicbuildingName = publicbuildingNames.get(i);
                map.addMarker(new MarkerOptions().position(publicbuildingLatLng).title(publicbuildingName));
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}

