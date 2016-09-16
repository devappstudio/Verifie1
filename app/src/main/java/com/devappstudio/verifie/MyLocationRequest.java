package com.devappstudio.verifie;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MyLocationRequest extends Activity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,


    };
    private static final String[] CAMERA_PERMS={
            Manifest.permission.CAMERA
    };
    private static final String[] CONTACTS_PERMS={
            Manifest.permission.READ_CONTACTS
    };
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] LOCATION1_PERMS={
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String[] READ_EXTERNAL_PER={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
   private static final String[] WRITE_EXTERNAL_PER={
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int INITIAL_REQUEST=1337;
    private static final int CAMERA_REQUEST=INITIAL_REQUEST+1;
    private static final int CONTACTS_REQUEST=INITIAL_REQUEST+2;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private static final int LOCATION1_REQUEST=INITIAL_REQUEST+4;
    private static final int READ_EXTERNAL_REQUEST=INITIAL_REQUEST+5;
    private static final int WRITE_EXTERNAL_REQUEST=INITIAL_REQUEST+6;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_request);

        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }
        else
        {

            mGoogleApiClient = new GoogleApiClient.Builder(getApplication())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();

        }




    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();


    }

      @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        final Intent intent = new Intent(MyLocationRequest.this, AppSetup.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MyLocationRequest.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     *@param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        final Intent intent = new Intent(MyLocationRequest.this, AppSetup.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to

                        Toast.makeText(MyLocationRequest.this, "Your Location Is Required For Verifie To Function Properly.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    }
                    default:
                    {
                        finish();
                        break;
                    }
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode) {
            case CAMERA_REQUEST:
                if (canAccessCamera()) {
                }
                else {
                }
                break;

            case CONTACTS_REQUEST:
                if (canAccessContacts()) {

                }
                else {
                }
                break;

            case LOCATION_REQUEST:
                if (canAccessLocation()) {

                    mGoogleApiClient = new GoogleApiClient.Builder(getApplication())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this).build();
                    mGoogleApiClient.connect();

                }
                else {
                    Toast.makeText(getApplication(),"Sorry Verifie Cannot Function Properly Withoout Your Location",Toast.LENGTH_LONG).show();
                    finish();;
                }
                break;

            case LOCATION1_REQUEST:
                if (canAccessLocation1()) {

                    mGoogleApiClient = new GoogleApiClient.Builder(getApplication())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this).build();
                    mGoogleApiClient.connect();

                }
                else {
                    Toast.makeText(getApplication(),"Sorry Verifie Cannot Function Properly Withoout Your Location",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case READ_EXTERNAL_REQUEST:
                if (canReadExternal()) {
                }
                else {
                }
                break;

            case WRITE_EXTERNAL_REQUEST:
                if (canWriteExternal()) {
                }
                else {
                }
                break;
        }
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean canAccessLocation1() {
        return(hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean canAccessCamera() {
        return(hasPermission(Manifest.permission.CAMERA));
    }

    private boolean canAccessContacts() {
        return(hasPermission(Manifest.permission.READ_CONTACTS));
    }

    private boolean canReadExternal() {
        return(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private boolean canWriteExternal() {
        return(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED== ActivityCompat.checkSelfPermission(getApplicationContext(),perm));
    }
}
