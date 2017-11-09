package com.example.xiaoxiazheng.socialnetworkapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private int zoomSize = 16;
    private LocationManager locationManager;
    private static final int UPDATESEC = 6000;
    private static final int UPDATEMET = 10;
    private Double latitude, longitude;
    String user, pwd, lat, longt;
    private Marker marker = null;
    String myJSON;
    JSONArray users = null;
    Button fetch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Receive data(username and password) from the login activity.
        Intent intent = getIntent();
        user = intent.getStringExtra("username");
        pwd = intent.getStringExtra("password");
        Log.d("Sasha", "username: " + user);

        fetch = (Button) findViewById(R.id.buttonFetch);
        fetch.setOnClickListener(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
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
        getManager();
        setCamera();
    }


    //Register the location manager.
    public void getManager() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATESEC, UPDATEMET, this);
    }


    //Initiate the map camera.
    public void setCamera() {
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
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
        Location location = locationManager.getLastKnownLocation(provider);
        onLocationChanged(location);

        Log.d("Sasha", "camera location:" +location);

        if (location != null)
        {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(zoomSize);
            builder.target(myLocation);
            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        }
    }


    //Get data from the location manager and begin the location asynctask.
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lat = latitude.toString();
        longt = longitude.toString();

        LocationTask locationTask = new LocationTask();
        locationTask.execute(user, pwd, lat, longt);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //Begin the findFriend task when clicking the fetch button.
    @Override
    public void onClick(View v) {

        findFridTask findfridtask = new findFridTask();
        findfridtask.execute(user, pwd, lat, longt);

    }


    //Location background task.
    public class LocationTask extends AsyncTask<String, Void, String> {

        public LocationTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            String update_url = "http://mpss.csce.uark.edu/~xiaoxia1/updateLocation.php";

            String username = params[0];
            String password = params[1];
            String latitude = params[2];
            String longitude = params[3];

            Log.d("Sasha", "latitude: " +latitude);


            try {
                URL url = new URL(update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //Write the latitude and longitude data into server.
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String message =
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                                URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" +
                                URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");

                bufferedWriter.write(message);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                Log.d("Sasha", "message: " +message);

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Update Success!";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String result) {
        }
    }



    //Find friends around me.
    public class findFridTask extends AsyncTask<String, Void, String> {

        public findFridTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://mpss.csce.uark.edu/~xiaoxia1/findFriend.php";

            String username = params[0];
            String password = params[1];
            String latitude = params[2];
            String longitude = params[3];

            String result = "";


            try {
                URL url = new URL(login_url);
                HttpURLConnection httpsURLConnection = (HttpURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setDoInput(true);

                OutputStream outputStream = httpsURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String message =
                                URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                                URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" +
                                URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");


                bufferedWriter.write(message);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();


                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),8);
                StringBuilder sb = new StringBuilder();

                String line = "";

                Log.d("Sasha", "latitude" +latitude );
                Log.d("Sasha", "longitude" +longitude );


                //Receive data from mysql server.
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();

                Log.d("Sasha", "response: " + result);

                bufferedReader.close();
                inputStream.close();
                httpsURLConnection.disconnect();
                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            //Create the JSON object and change the String data into JSON array.
            myJSON = result;
            Log.d("Sasha", "myJson: " +myJSON);

           // userList = new ArrayList<HashMap<String, String>>();

            try {

                JSONObject jsonObject = new JSONObject(myJSON);
                Log.d("Sasha", "jsonObject: " +jsonObject);

                users = jsonObject.getJSONArray("result");
                Log.d("Sasha", "jsonUser:" +users);


                //Create the map marker to show users.
                if (marker != null)
                {
                    marker.remove();
                }

                for (int i = 0; i<users.length(); i++) {
                     JSONObject obj = users.getJSONObject(i);
                     marker = mMap.addMarker(new MarkerOptions()
                            .title(obj.getString("username"))
                            .position(new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude")))
                    );
                }

                marker.showInfoWindow();

                Log.d("Sasha", "showMarker: " +marker);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Sasha", "test end");

        }

    }


}

