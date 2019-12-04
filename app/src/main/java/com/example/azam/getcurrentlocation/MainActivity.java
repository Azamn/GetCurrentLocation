package com.example.azam.getcurrentlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public Button button;
    public TextView textView;
    public String currentCity = " ";
    public String city = " ";
    EditText editText;
    Button button2;
    ImageView imageView;
    ImageView imageView2;

    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView);
        button2 = (Button)findViewById(R.id.button2);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        editText = (EditText)findViewById(R.id.editText);



        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                    }
                }else{
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try{

                        textView.setText(hereLocation(location.getLatitude(),location.getLongitude()));
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,"Location Not Found..!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });

        // Open The Camera If Location is Perfect

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city = editText.getText().toString();
                if(city.equals(currentCity)){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,0);
                }else{
                    Toast.makeText(MainActivity.this,"Location Not Matched..!",Toast.LENGTH_SHORT).show();
                }

                editText.getText().clear();
            }
        });

    }

    // Show the capture image
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       switch(requestCode){
           case MY_PERMISSION_REQUEST_LOCATION:{
               if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   if(ContextCompat.checkSelfPermission(MainActivity.this,
                           Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                       LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                       Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                       try{

                           textView.setText(hereLocation(location.getLatitude(),location.getLongitude()));


                       }catch (Exception e){
                           e.printStackTrace();
                           Toast.makeText(MainActivity.this," Location Not Matched..!",Toast.LENGTH_SHORT).show();
                       }
                   }
               }else {
                   Toast.makeText(MainActivity.this,"No Permission Granted.",Toast.LENGTH_SHORT).show();
               }
           }
       }
    }

    // Get Current city or closest city

    public String hereLocation(double lat, double lon){

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat,lon,1);
            if(addressList.size()>0){
                currentCity = addressList.get(0).getLocality();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return currentCity;
    }





}
