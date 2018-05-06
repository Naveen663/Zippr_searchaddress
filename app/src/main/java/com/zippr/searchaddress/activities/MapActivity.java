package com.zippr.searchaddress.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.zippr.searchaddress.R;
import com.zippr.searchaddress.fragments.CustomMapFragment;
import com.zippr.searchaddress.utils.Common;
import com.zippr.searchaddress.utils.PermissionUtils;

import static com.zippr.searchaddress.activities.CommonActivity.MIN_DISTANCE;
import static com.zippr.searchaddress.activities.CommonActivity.MIN_TIME;

/**
 * Created by user on 06-05-2018.
 */

public class MapActivity extends CommonActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, LocationListener, View.OnClickListener {


    private final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mLocationPermissionGranted = false;

    private final int MY_CALL_PHONE_PERMISSION_REQUEST_CODE = 2;


    Location currentLocation = null;

    private GoogleMap mMap = null;

    Context mContext;
    private UiSettings mUiSettings;
    CustomMapFragment mapFragment;
    float previousZoomLevel = 17.0f;

    EditText edt_place;
    Button btn_selectLocation;


    private static final String DATABASE_NAME = "myDb.db";
    private static final String DATABASE_TABLENAME = "ZIPPR";
    private static final String DATABASE_CREATE_TABLE ="create table if not exists ZIPPR (_id integer primary key autoincrement,place text not null)";

    String place;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mContext = this;


        initViews();

        mapFragment =
                (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Common.checkLocationPermission(this)) {
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
            mLocationPermissionGranted = true;

            // moveCameraToCurrentLocation(location);
        }



    }

    private void initViews() {

        edt_place = (EditText) findViewById(R.id.edt_place);
        btn_selectLocation = (Button) findViewById(R.id.btn_selectLocation);
        btn_selectLocation.setOnClickListener(this);

    }


    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_selectLocation:

                place = edt_place.getText().toString().trim();

                if(TextUtils.isEmpty(place)){
                    Toast.makeText(MapActivity.this,"Please enter place",Toast.LENGTH_SHORT).show();
                }else{
                    addToDB();
                }

                break;

        }

    }

    private void addToDB() {

        SQLiteDatabase myDB = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        myDB.execSQL(DATABASE_CREATE_TABLE);


        ContentValues newRow = new ContentValues();

        newRow.put("place", place);
        myDB.insert(DATABASE_TABLENAME, null, newRow);


        Toast.makeText(getApplicationContext(), "New Row Added to DB Successfully with Place : "+place, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        Log.d("onCameraMoveStarted", "onCameraMoveStarted = " + reason);
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            //  Toast.makeText(this, "The user gestured on the map.",
            //         Toast.LENGTH_SHORT).show();
            previousZoomLevel = mMap.getCameraPosition().zoom;
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            //  Toast.makeText(this, "The user tapped something on the map.",
            //        Toast.LENGTH_SHORT).show();
            previousZoomLevel = mMap.getCameraPosition().zoom;
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            // Toast.makeText(this, "The app moved the camera.",
            //         Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));

        // My LocationButton on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



        if (currentLocation == null) {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        /*currentLocation.setLatitude(dbl_vehl_lat);
        currentLocation.setLongitude(dbl_vehl_lng);*/

        moveCameraToCurrentLocation(currentLocation);

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);

        }

    }


    private void moveCameraToCurrentLocation(Location location) {
        Log.d("moveCameraToCurrentL", "moveCameraToCurrentLocation");
        currentLocation = location;
        if (mMap == null || location == null) {
            return;
        }
        //if (bookingDetails == null) {
        //previousZoomLevel = 17.0f;
        //}
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, previousZoomLevel);
        mMap.animateCamera(cameraUpdate);
    }

}
