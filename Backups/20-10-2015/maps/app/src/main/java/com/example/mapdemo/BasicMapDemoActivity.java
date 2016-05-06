/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class BasicMapDemoActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private TextView heading;

    private double latitude_value;
    private double longitude_value;
    private double altitude_value;
    private double heading_value;

    private String device_uid = "";
    private String area_uid = "";

    private TextView choice;
    private CheckBox fineAcc;
    private Button choose;
    private TextView provText;
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    private GoogleMap curMap;
    private Marker myMarker;
    private ArrayList<Marker> deviceMarkers;

    private Handler customHandler = new Handler();
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    private boolean networkAv = false;
    private boolean updatesDone = false;
    private boolean refreshAv = false;

    public static ArrayList<HashMap<String, String>> dataList;

    private static String url_updates = "http://pixelupstudios.com/iX-UP/eatinerari/db_logic/db_update.php";
    private static String url_refresh = "http://pixelupstudios.com/iX-UP/eatinerari/db_logic/db_read_all.php";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_DATA = "data";

    public static final String TAG_UID = "device_uid";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_ALTITUDE = "altitude";
    public static final String TAG_HEADING = "heading";
    public static final String TAG_TIMESTAMP = "timestamp";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        curMap = mapFragment.getMap();

        device_uid = Settings.Secure.getString(BasicMapDemoActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        setGeoLocation();
        dataList = new ArrayList<HashMap<String, String>>();
        deviceMarkers = new ArrayList<Marker>();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        customHandler.removeCallbacks(refreshDevicesThread);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        customHandler.postDelayed(refreshDevicesThread, 10000);
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        if(myMarker == null) {
            myMarker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Me"));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(0, 0))      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);

        }
        Log.d("DEVICE DEBUG", "DEVICE UID: " + device_uid);
    }


    private void setGeoLocation()
    {
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);
        altitude = (TextView) findViewById(R.id.alt);
        heading = (TextView) findViewById(R.id.hea);

        provText = (TextView) findViewById(R.id.prov);
        choice = (TextView) findViewById(R.id.choice);
        fineAcc = (CheckBox) findViewById(R.id.fineAccuracy);
        choose = (Button) findViewById(R.id.chooseRadio);
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

        // user defines the criteria
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(fineAcc.isChecked()){
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    choice.setText("fine accuracy selected");
                }
                else {
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    choice.setText("coarse accuracy selected");
                }
            }
        });
        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);
        // the last known location of this provider
        Location location = locationManager.getLastKnownLocation(provider);
        mylistener = new MyLocationListener();
        if (location != null) {
            mylistener.onLocationChanged(location);
        } else {
            // leads to the settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        // location updates: at least 1 meter and 200 millsecs change
        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
            latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
            longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
            altitude.setText("Altitude: " + String.valueOf(location.getAltitude()));
            heading.setText("Heading: " + String.valueOf(location.getBearing()));
            provText.setText(provider + " provider has been selected.");

            latitude_value = location.getLatitude();
            longitude_value = location.getLongitude();
            altitude_value = location.getAltitude();
            heading_value = location.getBearing();

            if(myMarker != null) {
                myMarker.remove();
            }
            if(curMap != null) {
                myMarker = curMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Me"));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(location.getBearing())                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                curMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);

            }

            new UpdateData().execute("1");

            Toast.makeText(BasicMapDemoActivity.this, "Location changed",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(BasicMapDemoActivity.this, provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(BasicMapDemoActivity.this, "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(BasicMapDemoActivity.this, "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null) {
            System.out.println("Internet Connection Not Present");
            return false;
        }
        //Log.d("Network Status: ", netInfo.getState().toString());
        if (netInfo != null && netInfo.isConnected()) {
            if (netInfo.isAvailable()) {
                return true;
            } else {
                System.out.println("Internet Connection Not Present");
                return false;
            }

        } else {
            System.out.println("Internet Connection Not Present");
            return false;
        }
    }


    /**
     * Background Async Task to Load all data by making HTTP Request
     */
    class RefreshData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deviceMarkers.clear();
            //spinProgress.setVisibility(View.VISIBLE);
        }


        /**
         * sendind All data to url
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            int success = -1;
            JSONObject json = null;

            networkAv = isOnline();
            if (networkAv) {
                updatesDone = false;
                // create parameters list
                params.add(new BasicNameValuePair("device_uid", device_uid));
                params.add(new BasicNameValuePair("area_uid", area_uid));

                // get JSON Object by using POST method
                json = jParser.makeHttpRequest(url_refresh, "POST", params);
                try {
                    if (json != null) {
                        Log.d("JSON", "PHP Response: " + json.toString());
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            refreshAv = true;
                        } else {
                            refreshAv = false;
                        }
                    } else {
                        refreshAv = false;
                    }
                    Log.d("JSON", "RESULT: " + refreshAv);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    updatesDone = false;
                }
                if (refreshAv) {
                    try {
                        LocalDatabase ld;
                        ld = new LocalDatabase();
                        ld.reset();
                        // Getting Array of Data

                        // new JSONArray
                        String message = json.getString("message");
                        Log.d("JSON", "MESSAGE: " + message);
                        JSONArray data = json.getJSONArray("data");
                        Log.d("JSON", "DATA: " + data);
                        dataList.clear();
                        int dataSize = data.length();

                        Log.d("JSON", "DATA SIZE: " + dataSize);
                        // looping through All Data

                        for (int i = 0; i < dataSize; i++) {
                            JSONObject c = data.getJSONObject(i);
                            ////Log.d("JSON", "ARRAY CELL: " + c);
                            // Storing each json item in variable

                            String cur_uid = c.getString(TAG_UID);
                            String cur_latitude = c.getString(TAG_LATITUDE);
                            String cur_longitude = c.getString(TAG_LONGITUDE);
                            String cur_altitude = c.getString(TAG_ALTITUDE);
                            String cur_heading = c.getString(TAG_HEADING);
                            String cur_timestamp = c.getString(TAG_TIMESTAMP);

                            Log.d("JSON", "UID: " + cur_uid);

                            // writing to db recordset
                            if (i == 0)
                                ld.insertDataDevice(cur_uid, cur_latitude, cur_longitude, cur_altitude, cur_heading, cur_timestamp, getApplicationContext(), true);
                            else
                                ld.insertDataDevice(cur_uid, cur_latitude, cur_longitude, cur_altitude, cur_heading, cur_timestamp, getApplicationContext(), false);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_UID, cur_uid);
                            map.put(TAG_LATITUDE, cur_latitude);
                            map.put(TAG_LONGITUDE, cur_longitude);
                            map.put(TAG_ALTITUDE, cur_altitude);
                            map.put(TAG_HEADING, cur_heading);
                            map.put(TAG_TIMESTAMP, cur_timestamp);

                            // adding HashList to ArrayList
                            dataList.add(map);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return (String.valueOf(refreshAv));
        }



        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all data

            //spinProgress.setVisibility(View.INVISIBLE);

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    for (int i = 0; i < dataList.size(); i++) {
                        final LatLng newGeoLoc = new LatLng(Double.parseDouble(dataList.get(i).get(TAG_LATITUDE)), Double.parseDouble(dataList.get(i).get(TAG_LONGITUDE)));
                        Marker tMarker = curMap.addMarker(new MarkerOptions()
                                .title(dataList.get(i).get(TAG_UID))
                                .position(newGeoLoc)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        deviceMarkers.add(tMarker);
                    }
                }
            });
        }
    }


    private Runnable refreshDevicesThread = new Runnable() {
        public void run() {
            new RefreshData().execute("1");
        }
    };


    /**
     * Background Async Task to Load all data by making HTTP Request
     */
    class UpdateData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //spinProgress.setVisibility(View.VISIBLE);
        }


        /**
         * sendind All data to url
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            int success = -1;
            JSONObject json = null;

            networkAv = isOnline();
            if (networkAv) {
                updatesDone = false;
                // create parameters list
                params.add(new BasicNameValuePair("device_uid", device_uid));
                params.add(new BasicNameValuePair("latitude", String.valueOf(latitude_value)));
                params.add(new BasicNameValuePair("longitude", String.valueOf(longitude_value)));
                params.add(new BasicNameValuePair("altitude", String.valueOf(altitude_value)));
                params.add(new BasicNameValuePair("heading", String.valueOf(heading_value)));

                // get JSON Object by using POST method
                json = jParser.makeHttpRequest(url_updates, "POST", params);
                try {
                    if(json != null) {
                        Log.d("PHP Response", json.toString());
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            updatesDone = true;
                        } else {
                            updatesDone = false;
                        }
                    }
                    else
                    {
                        updatesDone = false;
                    }
                    Log.d("UPDATES CHECK", "RESULT: " + updatesDone);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    updatesDone = false;
                }
            }

            return String.valueOf(updatesDone);
        }



        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all data

            //spinProgress.setVisibility(View.INVISIBLE);

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
        }
    }
}
