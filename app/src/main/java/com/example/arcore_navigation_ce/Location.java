package com.example.arcore_navigation_ce;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Location extends AppCompatActivity {

    ListView listView;
    String destination;
    String destLatLong;
    String origin;
    Handler handler;
    double latitude;
    double longitude;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinations_list2);

        if (isNetworkConnected()) {
            if (isGPSEnabled(Location.this)) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                handler = new Handler();
                handler.post(new Runnable() {
                    public void run() {
                        GPSTracker gpsTracker = new GPSTracker(Location.this);
                        if (gpsTracker.canGetLocation()) {
                            double latitude = gpsTracker.getLatitude();
                            double longitude = gpsTracker.getLongitude();
                            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                        } else {
                            gpsTracker.showSettingsAlert();
                        }
                    }
                });
            }
        }

        listView = (ListView) findViewById(R.id.destination_list2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) listView.getItemAtPosition(position);
                String values = ((TextView) view).getText().toString();
                destination = values;
                try {
                    JSONObject latlongJson = new JSONObject(loadJSONFromAsset("latlong.json"));
                    JSONArray latlong = latlongJson.getJSONArray("coordinates");
                    JSONObject latlongObject = new JSONObject(latlong.get(0).toString());
                    destLatLong = latlongObject.get(destination).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Location.this, BasicNavigation2.class);
                intent.putExtra("origin", origin);
                intent.putExtra("destLatLong", destLatLong);
                startActivity(intent);

            }
        });
    }


    public String loadJSONFromAsset(String address) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(address);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
