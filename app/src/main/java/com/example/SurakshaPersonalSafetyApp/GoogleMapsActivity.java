package com.example.SurakshaPersonalSafetyApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.SurakshaPersonalSafetyApp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GoogleMapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_code = 99;
    private double latitude,longitude;
    private int ProximityRadius = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void onClick(View v){
        String hospital = "hospital" , pharmacy = "pharmacy",ambulance = "ambulance",police_staion = "police";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        switch (v.getId()){
            case R.id.search_address:
                EditText addressField = findViewById(R.id.location_search);
                String address = addressField.getText().toString();
                List<Address> addressList = null;
                MarkerOptions usermarkerOptions = new MarkerOptions();

                if (!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder = new Geocoder(this);
                    try{
                        addressList = geocoder.getFromLocationName(address,6);
                        if (addressList != null){
                            for (int i=0;i<addressList.size(); i++){
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                usermarkerOptions.position(latLng);
                                usermarkerOptions.title(address);
                                usermarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(usermarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                        else {
                            Toast.makeText(this,"Location not found.. ",Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(this,"Please write any location name.. ",Toast.LENGTH_SHORT).show();
                }

                break;


            case R.id.hospitals_nearby:
                mMap.clear();
                String url = getUrl(latitude,longitude,hospital);
                transferData[0] = mMap;
                transferData[1] = url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"Searching for Nearby Hospitals..",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing Nearby Hospitals..",Toast.LENGTH_SHORT).show();

                break;

            case R.id.pharmacy_nearby:
                mMap.clear();
                String url1 = getUrl(latitude,longitude,pharmacy);
                transferData[0] = mMap;
                transferData[1] = url1;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"Searching for Nearby Pharmacy..",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing Nearby Pharmacy..",Toast.LENGTH_SHORT).show();

                break;

            case R.id.ambulance_nearby:
                mMap.clear();
                String url2 = getUrl(latitude,longitude,ambulance);
                transferData[0] = mMap;
                transferData[1] = url2;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"Searching for Nearby Ambulance..",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing Nearby Ambulance..",Toast.LENGTH_SHORT).show();

                break;

            case R.id.police_nearby:
                mMap.clear();
                String url3 = getUrl(latitude,longitude,police_staion);
                transferData[0] = mMap;
                transferData[1] = url3;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"Searching for Nearby Police Station..",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing Nearby Police Station..",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private String getUrl(double latitude,double longitude,String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyCFSTNaTlEcLg0za4K3ZGQFsF7j5ss4G_g");

        Log.d("GoogleMapsActivity","url = " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_code);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_code);

            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override //this method handles the permission
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        { //if permission granted or not
            case Request_User_Location_code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    //if permission is not granted or denied
                    Toast.makeText(this,"Permission Denied .. ",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;
        if (currentUserLocationMarker != null){
            currentUserLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if (googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            //fuselocation api
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }}

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
