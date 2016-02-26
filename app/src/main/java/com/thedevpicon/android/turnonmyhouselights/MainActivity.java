package com.thedevpicon.android.turnonmyhouselights;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 1;
    private static final int TURN_ON_DISTANCE = 10;

    // My home -12.1122549,-77.0046259
    private static final double HOME_LONGITUDE = -12.1122549;
    private static final double HOME_LATITUDE = -77.0046259;
    LocationManager locationManager;
    TextView locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button onButton = (Button) findViewById(R.id.buttonOn);
        Button offButton = (Button) findViewById(R.id.buttonOff);
        locationText = (TextView) findViewById(R.id.textEditLocation);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        onButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
                    return;
                }
                enableLocationUpdates();
            }
        });

        offButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(listener);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if ((requestCode == LOCATION_REQUEST) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            enableLocationUpdates();
        }
    }

    private void enableLocationUpdates() {
        final int minTime = 0; // seconds
        final int minDistance = 0; // meter
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, minTime, minDistance, listener);
    }

    private void locationChanged(Location location) {
        Location homeLocation = new Location("spoofed");
        homeLocation.setLongitude(HOME_LONGITUDE);
        homeLocation.setLatitude(HOME_LATITUDE);

        float distance = location.distanceTo(homeLocation);
        locationText.setText(Float.toString(distance));

        // If location within XX m turn on lights.
        if (distance <= TURN_ON_DISTANCE) {
            toggleLights(true);
        }else{
            toggleLights(false);
        }
    }

    private void toggleLights(final boolean turnOn) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // Access the Internet.
                // Turn on the lights using the Interwebs.
                if (turnOn) {
                    // API / REST call to Turn lights on.
                } else {
                    // API / REST call to Turn lights off.
                }
                return turnOn;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                Log.d("Este TAG","por aca paso:" + aBoolean.toString());
                Toast.makeText(MainActivity.this,
                        aBoolean ? "Lights Turned On" : "Lights turned Off",
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            locationChanged(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            locationText.setText("Location Provider Disabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

    };

}
