package com.geeksquad.locationdata;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Declaring necessary instances and variables
    private GPSTracker gpsTracker;
    private TextView tvLatitude, tvLongitude, tvZipcode;
    Button btn, btn_map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing variables
        tvLatitude = (TextView)findViewById(R.id.latitude);
        tvLongitude = (TextView)findViewById(R.id.longitude);
        tvZipcode = (TextView)findViewById(R.id.zipcode);
        btn = findViewById(R.id.btn);
        btn_map = findViewById(R.id.btn_map);
        btn_map.setEnabled(false);

        //Checking for Location Permission
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        //Button click event of 'GET LOCATION' button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //gpsTracker object has the data of latitude and longitude and many more data of location
                gpsTracker = new GPSTracker(MainActivity.this);

                if(gpsTracker.canGetLocation()){
                    //fetching data of latitude and longitude
                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();

                    //setting data of latitude and longitude to TextViews
                    tvLatitude.setText(String.valueOf(latitude));
                    tvLongitude.setText(String.valueOf(longitude));

                    try {
                        //Fetching pincode through location data
                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        //We need only postal code currently
                        String postalCode = addresses.get(0).getPostalCode();

                        //These are few data that can be also extracted from geocoder
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String knownName = addresses.get(0).getFeatureName();

                        //setting data of postalcode to TextView
                        tvZipcode.setText(postalCode);// Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        //'NAVIGATE' button will be enabled only if picode is fetched
                        btn_map.setEnabled(true);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error In Fething Location Data", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    gpsTracker.showSettingsAlert();
                }

            }
        });

        //Button click event of 'NAVIGATE' button
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locstr = "https://www.google.com/maps/search/?api=1&query="+tvLatitude.getText()+','+tvLongitude.getText();
                Uri uri = Uri.parse(locstr);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }



    }