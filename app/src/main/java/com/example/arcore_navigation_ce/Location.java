package com.example.arcore_navigation_ce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class Location extends AppCompatActivity {

    ListView listView;
    String destination;
    String destLatLong;
    String origin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinations_list2);

        //TODO get users origin (lat-long)

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
}
