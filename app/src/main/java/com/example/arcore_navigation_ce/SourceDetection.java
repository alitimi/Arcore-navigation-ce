package com.example.arcore_navigation_ce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.sceneform.ux.ArFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SourceDetection extends AppCompatActivity {

    Button mDetect;
    ArFragment fragment;
    String place;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_detection);
        //Camera Permissions
        if (ContextCompat.checkSelfPermission(com.example.arcore_navigation_ce.SourceDetection.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(com.example.arcore_navigation_ce.SourceDetection.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
        mDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.example.arcore_navigation_ce.SourceDetection.this, BasicNavigation.class);
                startActivity(intent);
            }
        });

        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.cam_fragment);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");
            } else {
                Log.e("Scan", "Scanned");
                place = result.getContents();
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
