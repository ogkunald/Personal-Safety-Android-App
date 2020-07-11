package com.example.SurakshaPersonalSafetyApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.SurakshaPersonalSafetyApp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

   // private TextView tvShake;
    private CardView btn;
    private CardView btn2;
    private ImageButton mbtn2;
    int PERMISSION_ID = 44;
    public double lat,lon;
    public static double latt,lonn;
    private FusedLocationProviderClient client;
    private static final String TAG = "MyActivity";
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private static final String USERS="User";
    String email;
    public static String eno1,eno2;
////////////////////////////


/////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String emaill = user.getEmail();
            Log.i(TAG,""+emaill);
            email=emaill;

        }
       // Intent intentt=getIntent();
        //email=intentt.getStringExtra("txtEmail");
       // Log.i(TAG,""+email);

        database= FirebaseDatabase.getInstance();
        userRef=database.getReference(USERS);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().equals(email))
                    {
                       String enno1=ds.child("eno1").getValue(String.class);
                       String enno2=ds.child("eno2").getValue(String.class);
                        eno1=enno1;
                        eno2=enno2;
                        Log.i(TAG,""+eno1);
                        Log.i(TAG,""+eno2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        client= LocationServices.getFusedLocationProviderClient(this);
      // Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
      //  startActivity(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // tvShake = findViewById(R.id.tvShake);
        btn = findViewById(R.id.btn);
        requestPermission();
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat=location.getLatitude();
                lon=location.getLongitude();
                Log.i(TAG,""+lat);
                Log.i(TAG,""+lon);
                latt=lat;
                lonn=lon;
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                startActivity(intent);
            }
        });
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, GoogleMapsActivity.class);
                startActivity(intent);
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
            //    tvShake.setText("Shake Action is just detected!!");
                Toast.makeText(MainActivity.this, "Shaked!!!", Toast.LENGTH_SHORT).show();


            }
        });

        mbtn2 = findViewById(R.id.mbtn2);
        mbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmsManager sm = SmsManager.getDefault();
                //String number = "918691895687";
                String msg = "Location:"+"https://www.google.com/maps/search/?api=1&query="+MainActivity.latt+","+MainActivity.lonn;
                // sm.sendTextMessage(number, null, msg, null, null);
                sm.sendTextMessage(MainActivity.eno1, null, msg, null, null);
                sm.sendTextMessage(MainActivity.eno2, null, msg, null, null);
            }
        });



    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }



}