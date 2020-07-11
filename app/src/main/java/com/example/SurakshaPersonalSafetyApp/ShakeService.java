package com.example.SurakshaPersonalSafetyApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;

import java.util.Random;

import android.telephony.SmsManager;

public class ShakeService extends Service implements SensorEventListener {
    int coun=0;
    int PERMISSION_ID = 44;
    LocationManager locationManager;
    double latitude,longitude;




    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;

    }
   //




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 11) {

            Random rnd = new Random();

            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            coun++;
            if(coun==2)
            {
                ServiceActivity.tvShakeService.setText("3 SHAKES DETECTED SENDING MESSAGE");
                SmsManager sm = SmsManager.getDefault();
                //String number = "918691895687";
                String msg = "Location:"+"https://www.google.com/maps/search/?api=1&query="+MainActivity.latt+","+MainActivity.lonn;
               // sm.sendTextMessage(number, null, msg, null, null);
                sm.sendTextMessage(MainActivity.eno1, null, msg, null, null);
                sm.sendTextMessage(MainActivity.eno2, null, msg, null, null);


            }

        }

    }


}