package com.example.arcore_navigation_ce;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.schedulers.Schedulers;

public class BasicNavigation2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private ArFragment fragment;
    String origin;
    String destLatLong;


    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;
    Handler handler;


    Vibrator v;
    Button done;

    int count = 0;

    double latitude;
    double longitude;
    ArrayList<String> index_location = new ArrayList<>();
    ArrayList<String> index_instruction = new ArrayList<>();


    public BasicNavigation2() throws JSONException {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_detection);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Source and Destination String
        Bundle b = getIntent().getExtras();
        origin = b.getString("origin");
        destLatLong = b.getString("destLatLong");
        try {
            Root root = ServiceModule.providePlaceService().getRoute(origin, "car", destLatLong).subscribeOn(Schedulers.io()).blockingFirst();
            for (int i = 0; i < root.routes.get(0).legs.get(0).steps.size(); i++) {
                index_instruction.add(root.routes.get(0).legs.get(0).steps.get(i).instruction);
                index_location.add(root.routes.get(0).legs.get(0).steps.get(i).start_location.get(0) +
                        "," + root.routes.get(0).legs.get(0).steps.get(i).start_location.get(1));
//                desc.add(root.routes.get(0).legs.get(0).steps.get(i).instruction);

//                distance.add(Float.valueOf(root.routes.get(0).legs.get(0).steps.get(i).distance.value));
//                latitude.add(root.routes.get(0).legs.get(0).steps.get(i).start_location.get(0));
//                longitude.add(root.routes.get(0).legs.get(0).steps.get(i).start_location.get(1));
            }
            System.out.println(root.getRoutes().get(0).getLegs().get(0).summary);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.cam_fragment);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });

        GPSTracker gpsTracker = new GPSTracker(BasicNavigation2.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }
//        startNavigation();
        test();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startNavigation() {
        for (int i = 0; i < index_location.size(); i++) {
            int index = i;
            if (isNetworkConnected()) {
                if (isGPSEnabled(BasicNavigation2.this)) {
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                    handler = new Handler();
                    handler.post(new Runnable() {
                        public void run() {
                            GPSTracker gpsTracker = new GPSTracker(BasicNavigation2.this);
                            if (gpsTracker.canGetLocation()) {
                                latitude = gpsTracker.getLatitude();
                                longitude = gpsTracker.getLongitude();
                                Location user = new Location("");
                                Location location = new Location("");
                                String[] loc = index_location.get(index).split(",");
                                location.setLatitude(Double.parseDouble(loc[1]));
                                location.setLongitude(Double.parseDouble(loc[0]));
                                user.setLatitude(latitude);
                                user.setLongitude(longitude);
//                                float distanceInMetersOne = user.distanceTo(location);
                                double distanceInMetersOne = meterDistanceBetweenPoints(Float.parseFloat(String.valueOf(user.getLatitude())),
                                        Float.parseFloat(String.valueOf(user.getLongitude())), Float.parseFloat(String.valueOf(location.getLatitude())),
                                        Float.parseFloat(String.valueOf(location.getLongitude())));
                                if (distanceInMetersOne > 5) {
                                    handler.postDelayed(this, 5000); //now is every 5 seconds
                                } else {
                                    handler.removeCallbacks(this);
                                }
                                Toast.makeText(BasicNavigation2.this, distanceInMetersOne + "", Toast.LENGTH_SHORT).show();
                            } else {
                                gpsTracker.showSettingsAlert();
                            }
                        }
                    });
                }
            }


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void test() {
        Toast.makeText(BasicNavigation2.this, index_location.size() + "", Toast.LENGTH_SHORT).show();
        if (isNetworkConnected()) {
            if (isGPSEnabled(BasicNavigation2.this)) {
                handler = new Handler();
                handler.post(new Runnable() {
                    public void run() {
                        GPSTracker gpsTracker = new GPSTracker(BasicNavigation2.this);
                        if (gpsTracker.canGetLocation()) {
                            latitude = gpsTracker.getLatitude();
                            longitude = gpsTracker.getLongitude();
                            Location user = new Location("");
                            Location location = new Location("");
                            if (count < index_instruction.size()) {
                                String[] loc = index_location.get(count).split(",");
                                location.setLatitude(Double.parseDouble(loc[1]));
                                location.setLongitude(Double.parseDouble(loc[0]));
                                user.setLatitude(latitude);
                                user.setLongitude(longitude);
//                                float distanceInMetersOne = user.distanceTo(location);
                                double distanceInMetersOne = meterDistanceBetweenPoints(Float.parseFloat(String.valueOf(user.getLatitude())),
                                        Float.parseFloat(String.valueOf(user.getLongitude())), Float.parseFloat(String.valueOf(location.getLatitude())),
                                        Float.parseFloat(String.valueOf(location.getLongitude())));
                                if (distanceInMetersOne < 5) {
                                    Toast.makeText(BasicNavigation2.this, distanceInMetersOne + "if", Toast.LENGTH_SHORT).show();
                                    count++;
                                } else {
                                    Toast.makeText(BasicNavigation2.this, distanceInMetersOne + "else", Toast.LENGTH_SHORT).show();

                                }
                                Toast.makeText(BasicNavigation2.this, index_instruction.get(count), Toast.LENGTH_SHORT).show();
                                handler.postDelayed(this, 3000);
                            }

                        } else {
                            gpsTracker.showSettingsAlert();
                        }
                    }
                });
            }
        }


    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

    public int getRange(int degree) {
        int mRangeVal = 0;
        if (degree > 335 || degree < 25)
            mRangeVal = 1;    //N
        else if (degree > 65 && degree < 115)
            mRangeVal = 2;    //E
        else if (degree > 155 && degree < 205)
            mRangeVal = 3;    //S
        else if (degree > 245 && degree < 295)
            mRangeVal = 4;    //W
        return mRangeVal;
    }


    //-----------------------------AR Object placement----------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addObject(Uri uri) {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        if (frame != null) {
//            hits.addAll(frame.hitTest(pt.x, pt.y));
            for (HitResult hit : frame.hitTest(pt.x, pt.y)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    placeObject(fragment, hit.createAnchor(), uri);
                    break;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(fragment, anchor, modelRenderable))
                .exceptionally(throwable -> {
                            Toast.makeText(fragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            return null;
                        }

                );

    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);

        //Handling Rotaional orientation using Quaternion
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
//        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 90f));

        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    //---------------------------AR green dot center detection methods------------------------------

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    //----------------------------------------------------------------------------------------------

}